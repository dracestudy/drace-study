package com.runtimeverification.rvpredict.racedetection.hbepoch;

import java.nio.ByteBuffer;

import com.runtimeverification.rvpredict.racedetection.event.RaceDetectionEvent;
import com.runtimeverification.rvpredict.racedetection.event.RapidEventType;
import com.runtimeverification.rvpredict.util.Logger;
import com.runtimeverification.rvpredict.util.vectorclock.SemiAdaptiveVC;
import com.runtimeverification.rvpredict.util.vectorclock.VectorClock;

public class HBEpochEvent extends RaceDetectionEvent<HBEpochState> {
    // CHECKSTYLE.OFF: LocalVariableName - keep variable names consistent with paper
    // https://dl.acm.org/doi/10.1145/3276515
    private final Logger logger;

    public HBEpochEvent() {
        this.logger = Logger.getGlobal();
    }

    @Override
    public ByteBuffer toThreadVectorClockBytes(HBEpochState state) {
        VectorClock C_t = state.generateVectorClockFromClockThread(getThread());
        return C_t.toBinaryFormat();
    }

    @Override
    public void preProcess(HBEpochState state) {
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
    public boolean handle(HBEpochState state) {
        return this.handleSub(state);
    }

    protected void printRaceInfoHelper(HBEpochState state, String typeInfo) {
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
    public void printRaceInfoLockType(HBEpochState state) {
        printRaceInfoHelper(state, this.getLock().toString());
    }

    @Override
    public void printRaceInfoAccessType(HBEpochState state) {
        printRaceInfoHelper(state, Long.toString(this.getVariable()));
    }

    @Override
    public void printRaceInfoExtremeType(HBEpochState state) {
        printRaceInfoHelper(state, Long.toString(this.getTarget()));
    }

    @Override
    public void printRaceInfoTransactionType(HBEpochState state) {}

    /**
     * Handles acquire events, updating the vector clocks associated with the current
     * thread and lock data in the given {@code HBEpochState}.
     *
     * <p>This method updates the vector clock of the current thread (C_t) with the vector clock of
     * the lock that is being acquired (L_l).
     *
     * @param state Stores all the vector clocks and other data required.
     * @return Always returns false, as acquire events cannot be in a race.
     */
    @Override
    public boolean handleSubAcquire(HBEpochState state) {
        VectorClock H_t = state.getHBPredecessorThreadVectorClock(this.getThread());
        VectorClock L_l = state.getLockVectorClock(this.getLock());
        H_t.update(L_l);
        return false;
    }

    /**
     * Handles release events, updating the vector clocks associated with the current
     * thread and lock data in the given {@code HBEpochState}.
     *
     * <p>This method sets the vector clock of the lock being released (L_l) to the vector clock of
     * the current thread's vector clock (C_t). Current thread's clock is then incremented.
     *
     * @param state Stores all the vector clocks and other data required.
     * @return Always returns false, as release events cannot be in a race.
     */
    @Override
    public boolean handleSubRelease(HBEpochState state) {
        VectorClock C_t = state.generateVectorClockFromClockThread(this.getThread());
        VectorClock L_l = state.getLockVectorClock(this.getLock());
        L_l.update(C_t);
        state.incrementClockThread(this.getThread());
        return false;
    }

    /**
     * Handles read events, updating the vector clocks associated with the current
     * thread and variable data in the given {@code HBEpochState}.
     *
     * <p>This method checks if the accessed variable's write vector clock (W_v) is less than or equal
     * to the current thread's vector clock (C_t). A race is reported if W_v is not less than or
     * equal to C_t. The accessed variable's read vector clock (R_v) is then updated with C_t.
     *
     * @param state Stores all the vector clocks and other data required.
     * @return Returns true if a race is detected.
     */
    @Override
    public boolean handleSubRead(HBEpochState state) {
        if (this.isVolatile() || this.toIgnore) {
            return false;
        }
        boolean raceDetected = false;
        VectorClock C_t = state.generateVectorClockFromClockThread(this.getThread());
        SemiAdaptiveVC W_v = state.getWriteVectorClock(this.getVariable());
        SemiAdaptiveVC R_v = state.getReadVectorClock(this.getVariable());

        if (!(W_v.isLessThanOrEqual(C_t))) {
            raceDetected = true;
        } else {
            int c = C_t.get(Math.toIntExact(this.getThread()));
            if (!R_v.isSameEpoch(c, Math.toIntExact(this.getThread()))) {
                R_v.updateWithMax(C_t, Math.toIntExact(this.getThread()));
            }
        }
        return raceDetected;
    }

    /**
     * Handles write events, updating the vector clocks associated with the current
     * thread and variable data in the given {@code HBEpochState}.
     *
     * <p>This method checks if the accessed variable's read vector clock (R_v) and write vector clock
     * (W_v) is less than or equal to the current thread's vector clock (C_t). A race is reported
     * if R_v or W_v is not less than or equal to C_t. The accessed variable's write vector clock
     * (W_v) is then updated with C_t.
     *
     * @param state Stores all the vector clocks and other data required.
     * @return Returns true if a race is detected.
     */
    @Override
    public boolean handleSubWrite(HBEpochState state) {
        if (this.isVolatile() || this.toIgnore) {
            return false;
        }
        boolean raceDetected = false;
        VectorClock C_t = state.generateVectorClockFromClockThread(this.getThread());
        SemiAdaptiveVC W_v = state.getWriteVectorClock(this.getVariable());
        SemiAdaptiveVC R_v = state.getReadVectorClock(this.getVariable());

        if (!(W_v.isLessThanOrEqual(C_t))) {
            raceDetected = true;
        }
        if (!raceDetected) {
            raceDetected = !(R_v.isLessThanOrEqual(C_t));
        }
        int c = C_t.get(Math.toIntExact(this.getThread()));
        if (!W_v.isSameEpoch(c, Math.toIntExact(this.getThread()))) {
            W_v.setEpoch(c, Math.toIntExact(this.getThread()));
            if (!R_v.isEpoch()) {
                R_v.forceBottomEpoch();
            }
        }

        return raceDetected;
    }

    /**
     * Handles fork events, updating the vector clocks associated with the current
     * thread data in the given {@code HBEpochState}.
     *
     * <p>This method updates the vector clock of the thread that is being forked (C_tc) with the
     * the current thread's vector clock (C_t). Current thread's clock is then incremented.
     *
     * @param state Stores all the vector clocks and other data required.
     * @return Always returns false, as fork events cannot be in a race.
     */
    @Override
    public boolean handleSubFork(HBEpochState state) {
        VectorClock C_t = state.generateVectorClockFromClockThread(this.getThread());
        VectorClock H_tc = state.getHBPredecessorThreadVectorClock(this.getTarget());
        H_tc.copyFrom(C_t);
        state.incrementClockThread(this.getThread());
        return false;
    }

    /**
     * Handles join events, updating the vector clocks associated with the current
     * thread data in the given {@code HBEpochState}.
     *
     * <p>This method updates the vector clock of the current thread (C_t) with the
     * the vector clock of the thread that is being joined on (C_tc). Current thread's
     * clock is then incremented.
     *
     * @param state Stores all the vector clocks and other data required.
     * @return Always returns false, as join events cannot be in a race.
     */
    @Override
    public boolean handleSubJoin(HBEpochState state) {
        VectorClock H_t = state.getHBPredecessorThreadVectorClock(this.getThread());
        VectorClock C_tc = state.generateVectorClockFromClockThread(this.getTarget());
        H_t.update(C_tc);
        return false;
    }

    @Override
    public boolean handleSubBegin(HBEpochState state) {
        return false;
    }

    @Override
    public boolean handleSubEnd(HBEpochState state) {
        return false;
    }
    // CHECKSTYLE.ON: LocalVariableName
}
