package com.runtimeverification.rvpredict.racedetection.wcp;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Stack;

import com.runtimeverification.rvpredict.racedetection.event.RaceDetectionEvent;
import com.runtimeverification.rvpredict.racedetection.event.RapidEventType;
import com.runtimeverification.rvpredict.util.Logger;
import com.runtimeverification.rvpredict.util.vectorclock.VectorClock;

public class WCPEvent extends RaceDetectionEvent<WCPState> {
    // CHECKSTYLE.OFF: LocalVariableName - more readable with these variable names from the paper
    // https://dl.acm.org/doi/10.1145/3062341.3062374
    private final Logger logger;

    public WCPEvent() {
        this.logger = Logger.getGlobal();
    }

    @Override
    public ByteBuffer toThreadVectorClockBytes(WCPState state) {
        VectorClock C_t = state.generateVectorClockFromClockThread(getThread());
        return C_t.toBinaryFormat();
    }

    @Override
    public void preProcess(WCPState state) {
        // Do some booking
        if (!(state.mapThreadLockStack.containsKey(this.thread))) {
            state.mapThreadLockStack.put(thread, new Stack<Long>());
            state.mapThreadReadVarSetStack.put(thread, new Stack<HashSet<Long>>());
            state.mapThreadWriteVarSetStack.put(thread, new Stack<HashSet<Long>>());
        }

        RapidEventType tp = this.getType();

        state.checkAndAddThread(this.getThread());
        if (tp.isAccessType()) {
            state.checkAndAddVariable(this.getVariable(), this.toIgnore());
        }
        if (tp.isLockType()) {
            state.checkAndAddLock(this.getLock());
        }
        if (tp.isExtremeType()) {
            state.checkAndAddThread(this.getTarget());
        }
    }

    @Override
    public boolean handle(WCPState state) {
        return this.handleSub(state);
    }

    //************** Pretty Printing *******************/
    protected void printRaceInfoHelper(WCPState state, String typeInfo) {
        StringBuilder output = new StringBuilder("#");
        output.append(Integer.toString(getLocId()));
        output.append("|");
        output.append(this.getType().toString());
        output.append("|");
        output.append(typeInfo);
        output.append("|");
        VectorClock C_t = state.generateVectorClockFromClockThread(this.getThread());
        output.append(C_t.toString());
        output.append("|");
        output.append(Long.toString(this.getThread()));
        this.logger.report(output.toString(), Logger.MSGTYPE.VERBOSE);
    }

    @Override
    public void printRaceInfoLockType(WCPState state) {
        printRaceInfoHelper(state, this.getLock().toString());
    }

    @Override
    public void printRaceInfoAccessType(WCPState state) {
        printRaceInfoHelper(state, Long.toString(this.getVariable()));
    }

    @Override
    public void printRaceInfoExtremeType(WCPState state) {
        printRaceInfoHelper(state, Long.toString(this.getTarget()));
    }

    /**
     * Handles acquire events. Updating the data associated with the current
     * in the given {@code WCPState}.
     *
     * @param state Stores all the data required.
     * @return Always returns false, as acquire events cannot be in a race.
     */
    @Override
    public boolean handleSubAcquire(WCPState state) {

        //*** Extra Pre-processing for reEntrants *****/
        boolean reEntrant = state.isLockAcquired(this.getThread(), this.getLock());
        //*** Extra Pre-processing for reEntrants done *****/

        //****** Annotation phase starts **********/
        state.mapThreadLockStack.get(getThread()).push(getLock());
        state.mapThreadReadVarSetStack.get(getThread()).push(new HashSet<Long>());
        state.mapThreadWriteVarSetStack.get(getThread()).push(new HashSet<Long>());
        //****** Annotation phase ends **********/

        VectorClock H_t = state.HBPredecessorThread.get(this.getThread());
        VectorClock H_l = state.HBPredecessorLock.get(this.getLock());
        H_t.update(H_l); // Line 1 of algorithm

        VectorClock P_t = state.WCPPredecessorThread.get(this.getThread());
        VectorClock P_l = state.WCPPredecessorLock.get(this.getLock());
        P_t.update(P_l); // Line 2 of algorithm

        // No need to update the queue(s) for re-entrant lock
        if (!reEntrant) {
            state.updateViewAsWriterAtAcquire(getLock(), getThread()); // Line 3 of algorithm
        }

        return false;
    }

    /**
     * Handles release events. Updating the data associated with the current
     * in the given {@code WCPState}.
     *
     * @param state Stores all the data required.
     * @return Always returns false, as release events cannot be in a race.
     */
    @Override
    public boolean handleSubRelease(WCPState state) {

        //****** Annotation phase starts **********/
        HashSet<Long> readVarSet = state.mapThreadReadVarSetStack.get(getThread()).pop();
        HashSet<Long> writeVarSet = state.mapThreadWriteVarSetStack.get(getThread()).pop();

        this.setReadVarSet(readVarSet);
        this.setWriteVarSet(writeVarSet);

        state.mapThreadLockStack.get(getThread()).pop();

        if (!(state.mapThreadLockStack.get(getThread()).isEmpty())) {
            state.mapThreadReadVarSetStack.get(getThread()).peek().addAll(readVarSet);
            state.mapThreadWriteVarSetStack.get(getThread()).peek().addAll(writeVarSet);
        }
        //****** Annotation phase ends **********/

        state.readViewOfWriters(getLock(), getThread()); // Lines 4-6 of algorithm

        VectorClock H_t = state.HBPredecessorThread.get(this.getThread());
        VectorClock C_t = state.generateVectorClockFromClockThread(this.getThread());

        for (long tPrime : state.clockThread.keySet()) {
            if (tPrime != this.getThread()) {

                for (long r : this.readVarSet) {
                    VectorClock L_r_l_x_tprime =
                            state.getLastReleaseLockReadVarThreadVectorClock(
                                    this.getLock(), r, tPrime);
                    L_r_l_x_tprime.update(H_t);
                    L_r_l_x_tprime.update(C_t);
                    // Body of loop at Line 7 of algorithm.
                    // This is slightly different from the presented algorithm owing to an optimization
                }

                for (Long w : this.writeVarSet) {
                    VectorClock L_w_l_x_tprime =
                            state.getLastReleaseLockWriteVarThreadVectorClock(
                                    this.getLock(), w, tPrime);
                    L_w_l_x_tprime.update(H_t);
                    L_w_l_x_tprime.update(C_t);
                    // Body of loop at Line 8 of algorithm.
                    // This is slightly different from the presented algorithm owing to an optimization
                }
            }
        }

        VectorClock H_l = state.HBPredecessorLock.get(this.getLock());
        H_l.copyFrom(H_t);
        H_l.update(C_t); // Line 9 of algorithm

        VectorClock P_l = state.WCPPredecessorLock.get(this.getLock());
        VectorClock P_t = state.WCPPredecessorThread.get(this.getThread());
        P_l.copyFrom(P_t); // Line 9 of algorithm

        state.updateViewAsWriterAtRelease(this.getLock(), this.getThread()); // Line 10 of algorithm

        state.incrementClockThread(this.getThread()); // Vector clock increment at the end of a release event

        return false;
    }

    /**
     * Handles read events. Updating the data associated with the current
     * in the given {@code WCPState}.
     *
     * @param state Stores all the data required.
     * @return Returns true if a race is detected.
     */
    @Override
    public boolean handleSubRead(WCPState state) {
        if (this.isVolatile() || this.toIgnore()) {
            return false;
        }
        /****** Annotation phase starts **********/
        if (!(state.mapThreadLockStack.get(getThread()).isEmpty())) {
            state.mapThreadReadVarSetStack.get(getThread()).peek().add(getVariable());
            this.setLockSet(state.getSetFromStack(getThread()));
        }
        //****** Annotation phase ends **********/

        //***** Update P_t **************/
        VectorClock P_t = state.WCPPredecessorThread.get(this.getThread());
        // Line 11 of algorithm
        for (Long l : this.getLockSet()) {
            VectorClock writeClock =
                    state.getLastReleaseLockWriteVarThreadVectorClock(
                            l, this.getVariable(), this.getThread());
            P_t.update(writeClock); // Body of loop at Line 11 of algorithm
        }
        //***** P_t updated **************/

        boolean raceDetected = false;
        VectorClock C_t = state.generateVectorClockFromClockThread(this.getThread());
        VectorClock R_v = state.readVariable.get(this.getVariable());
        VectorClock W_v = state.writeVariable.get(this.getVariable());

        // Check if an earlier write of same variable is incomparable with this read event
        if (!W_v.isLessThanOrEqual(C_t)) {
            raceDetected = true;
        }

        // Update the Read(v) clock
        R_v.update(C_t);

        return raceDetected;
    }

    /**
     * Handles write events. Updating the data associated with the current
     * in the given {@code WCPState}.
     *
     * @param state Stores all the data required.
     * @return Returns true if a race is detected.
     */
    @Override
    public boolean handleSubWrite(WCPState state) {
        if (this.isVolatile || this.toIgnore) {
            return false;
        }

        //****** Annotation phase starts **********/
        if (!(state.mapThreadLockStack.get(getThread()).isEmpty())) {
            state.mapThreadWriteVarSetStack.get(getThread()).peek().add(getVariable());
            this.setLockSet(state.getSetFromStack(getThread()));
        }
        //****** Annotation phase ends **********/

        //***** Update P_t **************/
        VectorClock P_t = state.WCPPredecessorThread.get(this.getThread());
        // Line 12 of algorithm
        for (Long l : this.getLockSet()) {
            VectorClock writeClock =
                    state.getLastReleaseLockWriteVarThreadVectorClock(
                            l, this.getVariable(), this.getThread());
            P_t.update(writeClock); // Body of loop at Line 12 of algorithm

            VectorClock readClock =
                    state.getLastReleaseLockReadVarThreadVectorClock(
                            l, this.getVariable(), this.getThread());
            P_t.update(readClock); // Body of loop at Line 12 of algorithm
        }
        //***** P_t updated **************/

        boolean raceDetected = false;
        VectorClock C_t = state.generateVectorClockFromClockThread(this.getThread());
        VectorClock R_v = state.readVariable.get(this.getVariable());
        VectorClock W_v = state.writeVariable.get(this.getVariable());

        if (!R_v.isLessThanOrEqual(C_t)) {
            raceDetected = true;
        }

        if (!raceDetected) {
            if (!W_v.isLessThanOrEqual(C_t)) {
                raceDetected = true;
            }
        }

        // Update the Write(v) clock
        W_v.update(C_t);

        return raceDetected;
    }

    /**
     * Handles fork events. Updating the data associated with the current
     * in the given {@code WCPState}.
     *
     * @param state Stores all the data required.
     * @return Always returns false, as fork events cannot be in a race.
     */
    @Override
    public boolean handleSubFork(WCPState state) {
        VectorClock H_t = state.HBPredecessorThread.get(this.getThread());
        VectorClock C_t = state.generateVectorClockFromClockThread(this.getThread());

        VectorClock H_tc = state.HBPredecessorThread.get(this.getTarget());
        H_tc.copyFrom(C_t);
        H_tc.update(H_t); // Update the HB predecessor of the child (forked) thread

        VectorClock P_tc = state.WCPPredecessorThread.get(this.getTarget());
        P_tc.copyFrom(C_t);
        P_tc.update(H_t); // Update the WCP predecessor of the child (forked) thread

        state.incrementClockThread(this.getThread());
        return false;
    }

    /**
     * Handles join events. Updating the data associated with the current
     * in the given {@code WCPState}.
     *
     * @param state Stores all the data required.
     * @return Always returns false, as join events cannot be in a race.
     */
    @Override
    public boolean handleSubJoin(WCPState state) {
        VectorClock H_t = state.HBPredecessorThread.get(this.getThread());
        VectorClock H_tc = state.HBPredecessorThread.get(this.getTarget());
        VectorClock C_tc = state.generateVectorClockFromClockThread(this.getTarget());
        VectorClock P_t = state.WCPPredecessorThread.get(this.getThread());

        H_t.update(H_tc);
        H_t.update(C_tc); // Update the HB predecessor of this thread
        P_t.update(H_tc);
        P_t.update(C_tc); // Update the WCP predecessor of this thread
        return false;
    }

    @Override
    public void printRaceInfoTransactionType(WCPState state) {}

    @Override
    public boolean handleSubBegin(WCPState state) {
        return false;
    }

    @Override
    public boolean handleSubEnd(WCPState state) {
        return false;
    }
    //CHECKSTYLE.ON: LocalVariableName
}
