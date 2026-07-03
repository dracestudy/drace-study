package com.runtimeverification.rvpredict.racedetection.shb;

import java.nio.ByteBuffer;

import com.runtimeverification.rvpredict.racedetection.event.RaceDetectionEvent;
import com.runtimeverification.rvpredict.racedetection.event.RapidEventType;
import com.runtimeverification.rvpredict.util.Logger;
import com.runtimeverification.rvpredict.util.vectorclock.VectorClock;

public class SHBEvent extends RaceDetectionEvent<SHBState> {
    // CHECKSTYLE.OFF: LocalVariableName - more readable with these variable names from the paper
    // https://dl.acm.org/doi/10.1145/3276515
    private final Logger logger;

    public SHBEvent() {
        this.logger = Logger.getGlobal();
    }

    @Override
    public ByteBuffer toThreadVectorClockBytes(SHBState state) {
        VectorClock C_t = state.getThreadVectorClock(getThread());
        return C_t.toBinaryFormat();
    }

    @Override
    public void preProcess(SHBState state) {
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
    public boolean handle(SHBState state) {
        return this.handleSub(state);
    }

    private void printRaceInfoHelper(SHBState state, String typeInfo) {
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
    public void printRaceInfoLockType(SHBState state) {
        printRaceInfoHelper(state, this.getLock().toString());
    }

    @Override
    public void printRaceInfoAccessType(SHBState state) {
        printRaceInfoHelper(state, Long.toString(this.getVariable()));
    }

    @Override
    public void printRaceInfoExtremeType(SHBState state) {
        printRaceInfoHelper(state, Long.toString(this.getTarget()));
    }

    @Override
    public void printRaceInfoTransactionType(SHBState state) {}

    /**
     * Handles acquire events. Updating the data associated with the current
     * in the given {@code SHBState}.
     *
     * @param state Stores all the data required.
     * @return Always returns false, as acquire events cannot be in a race.
     */
    @Override
    public boolean handleSubAcquire(SHBState state) {
        VectorClock C_t = state.getThreadVectorClock(this.getThread());
        VectorClock L_l = state.getLockVectorClock(this.getLock());
        C_t.update(L_l);
        return false;
    }

    /**
     * Handles release events. Updating the data associated with the current
     * in the given {@code SHBState}.
     *
     * @param state Stores all the data required.
     * @return Always returns false, as release events cannot be in a race.
     */
    @Override
    public boolean handleSubRelease(SHBState state) {
        VectorClock C_t = state.getThreadVectorClock(this.getThread());
        VectorClock L_l = state.getLockVectorClock(this.getLock());
        L_l.update(C_t);
        state.incClockThread(getThread());
        return false;
    }

    /**
     * Handles read events. Updating the data associated with the current
     * in the given {@code SHBState}.
     *
     * @param state Stores all the data required.
     * @return Returns true if a race is detected.
     */
    @Override
    public boolean handleSubRead(SHBState state) {
        if (this.isVolatile() || this.toIgnore()) {
            return false;
        }
        boolean raceDetected = false;
        VectorClock C_t = state.getThreadVectorClock(this.getThread());
        VectorClock LW_v = state.getLastWriteVectorClock(getVariable());
        VectorClock W_v = state.getWriteVectorClock(getVariable());

        if (!W_v.isLessThanOrEqual(C_t)) {
            raceDetected = true;
        }
        C_t.update(LW_v);

        VectorClock R_v = state.getReadVectorClock(getVariable());
        int c_t_t = state.getIndex(C_t, this.getThread());
        state.setIndex(R_v, this.getThread(), c_t_t);

        return raceDetected;
    }

    /**
     * Handles write events. Updating the data associated with the current
     * in the given {@code SHBState}.
     *
     * @param state Stores all the data required.
     * @return Returns true if a race is detected.
     */
    @Override
    public boolean handleSubWrite(SHBState state) {
        VectorClock C_t = state.getThreadVectorClock(this.getThread());
        if (this.isVolatile() || this.toIgnore()) {
            VectorClock LW_v = state.getLastWriteVectorClock(getVariable());
            LW_v.copyFrom(C_t);
            state.incClockThread(getThread());
            return false;
        }
        boolean raceDetected = false;
        VectorClock R_v = state.getReadVectorClock(getVariable());
        VectorClock W_v = state.getWriteVectorClock(getVariable());

        if (!R_v.isLessThanOrEqual(C_t)) {
            raceDetected = true;
        }
        if (!raceDetected) {
            if (!W_v.isLessThanOrEqual(C_t)) {
                raceDetected = true;
            }
        }

        int c_t_t = state.getIndex(C_t, this.getThread());
        state.setIndex(W_v, this.getThread(), c_t_t);
        VectorClock LW_v = state.getLastWriteVectorClock(getVariable());
        LW_v.copyFrom(C_t);
        state.incClockThread(getThread());

        return raceDetected;
    }

    /**
     * Handles fork events. Updating the data associated with the current
     * in the given {@code SHBState}.
     *
     * @param state Stores all the data required.
     * @return Always returns false, as fork events cannot be in a race.
     */
    @Override
    public boolean handleSubFork(SHBState state) {
        VectorClock C_t = state.getThreadVectorClock(this.getThread());
        VectorClock C_tc = state.getThreadVectorClock(this.getTarget());
        C_tc.copyFrom(C_t);
        state.setIndex(C_tc, this.getTarget(), 1);
        state.incClockThread(getThread());
        return false;
    }

    /**
     * Handles join events. Updating the data associated with the current
     * in the given {@code SHBState}.
     *
     * @param state Stores all the data required.
     * @return Always returns false, as join events cannot be in a race.
     */
    @Override
    public boolean handleSubJoin(SHBState state) {
        VectorClock C_t = state.getThreadVectorClock(this.getThread());
        VectorClock C_tc = state.getThreadVectorClock(this.getTarget());
        C_t.update(C_tc);
        return false;
    }

    @Override
    public boolean handleSubBegin(SHBState state) {
        return false;
    }

    @Override
    public boolean handleSubEnd(SHBState state) {
        return false;
    }
    // CHECKSTYLE.ON: LocalVariableName
}
