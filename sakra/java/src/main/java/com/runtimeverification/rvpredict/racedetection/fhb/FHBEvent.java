package com.runtimeverification.rvpredict.racedetection.fhb;

import java.nio.ByteBuffer;

import com.runtimeverification.rvpredict.racedetection.event.RaceDetectionEvent;
import com.runtimeverification.rvpredict.racedetection.event.RapidEventType;
import com.runtimeverification.rvpredict.util.Logger;
import com.runtimeverification.rvpredict.util.vectorclock.VectorClock;

public class FHBEvent extends RaceDetectionEvent<FHBState> {
    // CHECKSTYLE.OFF: LocalVariableName - keep variable names consistent with paper
    // https://dl.acm.org/doi/10.1145/3276515
    private final Logger logger;

    public FHBEvent() {
        this.logger = Logger.getGlobal();
    }

    @Override
    public ByteBuffer toThreadVectorClockBytes(FHBState state) {
        VectorClock C_t = state.getThreadVectorClock(getThread());
        return C_t.toBinaryFormat();
    }

    @Override
    public void preProcess(FHBState state) {
        RapidEventType tp = this.getType();

        state.checkAndAddThread(this.getThread());
        if (tp.isAccessType()) {
            state.checkAndAddVariable(this.getVariable(), this.toIgnore());
        } else if (tp.isLockType()) {
            state.checkAndAddLock(this.getLock());
        } else if (tp.isExtremeType()) {
            state.checkAndAddThread(this.getTarget());
        }
    }

    @Override
    public boolean handle(FHBState state) {
        return this.handleSub(state);
    }

    protected void printRaceInfoHelper(FHBState state, String typeInfo) {
        StringBuilder output = new StringBuilder("#");
        output.append(Integer.toString(getLocId()));
        output.append("|");
        output.append(this.getType().toString());
        output.append("|");
        output.append(typeInfo);
        output.append("|");
        VectorClock C_t = state.getThreadVectorClock(this.getThread());
        output.append(C_t.toString());
        output.append("|");
        output.append(Long.toString(this.getThread()));
        this.logger.report(output.toString(), Logger.MSGTYPE.VERBOSE);
    }

    @Override
    public void printRaceInfoLockType(FHBState state) {
        printRaceInfoHelper(state, this.getLock().toString());
    }

    @Override
    public void printRaceInfoAccessType(FHBState state) {
        printRaceInfoHelper(state, Long.toString(this.getVariable()));
    }

    @Override
    public void printRaceInfoExtremeType(FHBState state) {
        printRaceInfoHelper(state, Long.toString(this.getTarget()));
    }

    @Override
    public void printRaceInfoTransactionType(FHBState state) {}

    /**
     * Handles acquire events, updating the vector clocks associated with the current
     * thread and lock data in the given {@code FHBState}.
     *
     * <p>This method updates the vector clock of the current thread (C_t) with the vector clock of
     * the lock that is being acquired (L_l).
     *
     * @param state Stores all the vector clocks and other data required.
     * @return Always returns false, as acquire events cannot be in a race.
     */
    @Override
    public boolean handleSubAcquire(FHBState state) {
        VectorClock C_t = state.getThreadVectorClock(this.getThread());
        VectorClock L_l = state.getLockVectorClock(this.getLock());
        C_t.update(L_l);
        return false;
    }

    /**
     * Handles release events, updating the vector clocks associated with the current
     * thread and lock data in the given {@code FHBState}.
     *
     * <p>This method updates the vector clock of the lock being released (L_l) with the vector clock of
     * the current thread's vector clock (C_t). Current thread's clock is then incremented.
     *
     * @param state Stores all the vector clocks and other data required.
     * @return Always returns false, as release events cannot be in a race.
     */
    @Override
    public boolean handleSubRelease(FHBState state) {
        VectorClock C_t = state.getThreadVectorClock(this.getThread());
        VectorClock L_l = state.getLockVectorClock(this.getLock());
        L_l.update(C_t);
        state.incrementThreadClock(getThread());
        return false;
    }

    /**
     * Handles read events, updating the vector clocks associated with the current
     * thread and variable data in the given {@code FHBState}.
     *
     * @param state Stores all the vector clocks and other data required.
     * @return Returns true if a race is detected.
     */
    @Override
    public boolean handleSubRead(FHBState state) {
        if (this.isVolatile() || this.toIgnore()) {
            return false;
        }
        boolean raceDetected = false;
        VectorClock C_t = state.getThreadVectorClock(this.getThread());
        VectorClock W_v = state.getWriteVectorClock(this.getVariable());

        if (!W_v.isLessThanOrEqual(C_t)) {
            raceDetected = true;
            state.addLocPair(state.getLWLocId(this.getVariable()), this.getLocId());
            // Force order
            C_t.update(W_v);
        }
        VectorClock R_v = state.getReadVectorClock(this.getVariable());
        R_v.update(C_t);

        state.setLastReadData(this.getVariable(), this.getThread(), this.getLocId(), C_t, this.toIgnore());
        state.incrementThreadClock(getThread());

        return raceDetected;
    }

    /**
     * Handles write events, updating the vector clocks associated with the current
     * thread and variable data in the given {@code FHBState}.
     *
     * @param state Stores all the vector clocks and other data required.
     * @return Returns true if a race is detected.
     */
    @Override
    public boolean handleSubWrite(FHBState state) {
        if (this.isVolatile() || this.toIgnore()) {
            return false;
        }
        boolean raceDetected = false;
        VectorClock C_t = state.getThreadVectorClock(this.getThread());
        VectorClock W_v = state.getWriteVectorClock(this.getVariable());
        VectorClock R_v = state.getReadVectorClock(this.getVariable());

        if (R_v.isLessThanOrEqual(W_v)) {
            if (!W_v.isLessThanOrEqual(C_t)) {
                raceDetected = true;
                state.addLocPair(state.getLWLocId(this.getVariable()), this.getLocId());
            }
        } else {
            raceDetected = state.checkRaceWithReadsAndAddLocPairs(
                    this.getThread(), this.getVariable(), C_t, this.getLocId());
        }

        C_t.update(W_v);
        C_t.update(R_v);
        W_v.copyFrom(C_t);
        state.setLWLocId(this.getVariable(), this.getLocId());

        state.incrementThreadClock(getThread());

        return raceDetected;
    }

    /**
     * Handles fork events, updating the vector clocks associated with the current
     * thread data in the given {@code FHBState}.
     *
     * <p>This method updates the vector clock of the thread that is being forked (C_tc) with the
     * the current thread's vector clock (C_t). Current thread's clock is then incremented.
     *
     * @param state Stores all the vector clocks and other data required.
     * @return Always returns false, as fork events cannot be in a race.
     */
    @Override
    public boolean handleSubFork(FHBState state) {
        VectorClock C_t = state.getThreadVectorClock(this.getThread());
        VectorClock C_tc = state.getThreadVectorClock(this.getTarget());
        C_tc.update(C_t);
        state.incrementThreadClock(this.getThread());
        return false;
    }

    /**
     * Handles join events, updating the vector clocks associated with the current
     * thread data in the given {@code FHBState}.
     *
     * <p>This method updates the vector clock of the current thread (C_t) with the
     * the vector clock of the thread that is being joined on (C_tc). Current thread's
     * clock is then incremented.
     *
     * @param state Stores all the vector clocks and other data required.
     * @return Always returns false, as join events cannot be in a race.
     */
    @Override
    public boolean handleSubJoin(FHBState state) {
        VectorClock C_t = state.getThreadVectorClock(this.getThread());
        VectorClock C_tc = state.getThreadVectorClock(this.getTarget());
        C_t.update(C_tc);
        return false;
    }

    @Override
    public boolean handleSubBegin(FHBState state) {
        return false;
    }

    @Override
    public boolean handleSubEnd(FHBState state) {
        return false;
    }
    // CHECKSTYLE.ON: LocalVariableName
}
