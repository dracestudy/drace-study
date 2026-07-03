package com.runtimeverification.rvpredict.racedetection.shbepoch;

import java.nio.ByteBuffer;

import com.runtimeverification.rvpredict.racedetection.event.RaceDetectionEvent;
import com.runtimeverification.rvpredict.racedetection.event.RapidEventType;
import com.runtimeverification.rvpredict.util.Logger;
import com.runtimeverification.rvpredict.util.vectorclock.FullAdaptiveVC;
import com.runtimeverification.rvpredict.util.vectorclock.SemiAdaptiveVC;
import com.runtimeverification.rvpredict.util.vectorclock.VectorClock;

public class SHBEpochEvent extends RaceDetectionEvent<SHBEpochState> {
    // CHECKSTYLE.OFF: LocalVariableName - more readable with these variable names from the paper
    // https://dl.acm.org/doi/10.1145/3276515
    private final Logger logger;

    public SHBEpochEvent() {
        this.logger = Logger.getGlobal();
    }

    @Override
    public ByteBuffer toThreadVectorClockBytes(SHBEpochState state) {
        VectorClock C_t = state.getThreadVectorClock(getThread());
        return C_t.toBinaryFormat();
    }

    @Override
    public void preProcess(SHBEpochState state) {
        RapidEventType tp = this.getType();

        state.checkAndAddThread(this.getThread());
        if (tp.isAccessType()) {
            state.checkAndAddVariable(this.getVariable());
        } else if (tp.isLockType()) {
            state.checkAndAddLock(this.getLock());
        } else if (tp.isExtremeType()) {
            state.checkAndAddThread(this.getTarget());
        }
    }

    @Override
    public boolean handle(SHBEpochState state) {
        return this.handleSub(state);
    }

    private void printRaceInfoHelper(SHBEpochState state, String typeInfo) {
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
    public void printRaceInfoLockType(SHBEpochState state) {
        printRaceInfoHelper(state, this.getLock().toString());
    }

    @Override
    public void printRaceInfoAccessType(SHBEpochState state) {
        printRaceInfoHelper(state, Long.toString(this.getVariable()));
    }

    @Override
    public void printRaceInfoExtremeType(SHBEpochState state) {
        printRaceInfoHelper(state, Long.toString(this.getTarget()));
    }

    @Override
    public void printRaceInfoTransactionType(SHBEpochState state) {}

    /**
     * Handles acquire events. Updating the data associated with the current
     * in the given {@code SHBState}.
     *
     * @param state Stores all the data required.
     * @return Always returns false, as acquire events cannot be in a race.
     */
    @Override
    public boolean handleSubAcquire(SHBEpochState state) {
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
    public boolean handleSubRelease(SHBEpochState state) {
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
    public boolean handleSubRead(SHBEpochState state) {
        if (this.isVolatile() || this.toIgnore) {
            return false;
        }
        boolean raceDetected = false;
        VectorClock C_t = state.getThreadVectorClock(this.getThread());
        VectorClock LW_v = state.getLastWriteVectorClock(getVariable());
        FullAdaptiveVC W_v = state.getWriteVectorClock(getVariable());

        if (!W_v.isLessThanOrEqual(C_t)) {
            raceDetected = true;
        }
        C_t.update(LW_v);

        SemiAdaptiveVC R_v = state.getReadVectorClock(getVariable());
        R_v.updateWithMax(C_t, Math.toIntExact(this.getThread()));

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
    public boolean handleSubWrite(SHBEpochState state) {
        VectorClock C_t = state.getThreadVectorClock(this.getThread());
        if (this.isVolatile() || this.toIgnore) {
            VectorClock LW_v = state.getLastWriteVectorClock(getVariable());
            LW_v.copyFrom(C_t);
            state.incClockThread(getThread());
            return false;
        }
        boolean raceDetected = false;
        SemiAdaptiveVC R_v = state.getReadVectorClock(getVariable());
        FullAdaptiveVC W_v = state.getWriteVectorClock(getVariable());

        if (!R_v.isLessThanOrEqual(C_t)) {
            raceDetected = true;
        }

        if (!raceDetected) {
            boolean W_v_isLTE_C_t = W_v.isLTEUpdateWithMax(C_t, Math.toIntExact(this.getThread()));
            if (!W_v_isLTE_C_t) {
                raceDetected = true;
            }
        }
        VectorClock LW_v = state.getLastWriteVectorClock(getVariable());
        LW_v.copyFrom(C_t);
        state.incClockThread(getThread());
        state.setLWLocId(this.getVariable(), this.getLocId());

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
    public boolean handleSubFork(SHBEpochState state) {
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
    public boolean handleSubJoin(SHBEpochState state) {
        VectorClock C_t = state.getThreadVectorClock(this.getThread());
        VectorClock C_tc = state.getThreadVectorClock(this.getTarget());
        C_t.update(C_tc);
        return false;
    }

    @Override
    public boolean handleSubBegin(SHBEpochState state) {
        return false;
    }

    @Override
    public boolean handleSubEnd(SHBEpochState state) {
        return false;
    }
    // CHECKSTYLE.ON: LocalVariableName
}
