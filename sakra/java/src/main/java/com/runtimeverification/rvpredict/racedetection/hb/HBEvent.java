package com.runtimeverification.rvpredict.racedetection.hb;

import java.nio.ByteBuffer;

import com.runtimeverification.rvpredict.racedetection.event.RaceDetectionEvent;
import com.runtimeverification.rvpredict.racedetection.event.RapidEventType;
import com.runtimeverification.rvpredict.util.Logger;
import com.runtimeverification.rvpredict.util.vectorclock.VectorClock;

public class HBEvent extends RaceDetectionEvent<HBState> {
    // CHECKSTYLE.OFF: LocalVariableName - keep variable names consistent with paper
    // https://dl.acm.org/doi/10.1145/3276515
    private final Logger logger;

    public HBEvent() {
        this.logger = Logger.getGlobal();
    }

    @Override
    public ByteBuffer toThreadVectorClockBytes(HBState state) {
        VectorClock C_t = state.getThreadClock(getThread());
        return C_t.toBinaryFormat();
    }

    @Override
    public void preProcess(HBState state) {
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
    public boolean handle(HBState state) {
        return this.handleSub(state);
    }

    protected void printRaceInfoHelper(HBState state, String typeInfo) {
        StringBuilder output = new StringBuilder("#");
        output.append(Integer.toString(getLocId()));
        output.append("|");
        output.append(this.getType().toString());
        output.append("|");
        output.append(typeInfo);
        output.append("|");
        VectorClock C_t = state.getThreadClock(this.getThread());
        output.append(C_t.toString());
        output.append("|");
        output.append(Long.toString(this.getThread()));
        this.logger.report(output.toString(), Logger.MSGTYPE.VERBOSE);
    }

    @Override
    public void printRaceInfoLockType(HBState state) {
        printRaceInfoHelper(state, this.getLock().toString());
    }

    @Override
    public void printRaceInfoAccessType(HBState state) {
        printRaceInfoHelper(state, Long.toString(this.getVariable()));
    }

    @Override
    public void printRaceInfoExtremeType(HBState state) {
        printRaceInfoHelper(state, Long.toString(this.getTarget()));
    }

    /**
     * Handles acquire events, updating the vector clocks associated with the current
     * thread and lock data in the given {@code HBState}.
     *
     * <p>This method updates the vector clock of the current thread (C_t) with the vector clock of
     * the lock that is being acquired (L_l).
     *
     * @param state Stores all the vector clocks and other data required.
     * @return Always returns false, as acquire events cannot be in a race.
     */
    @Override
    public boolean handleSubAcquire(HBState state) {
        VectorClock C_t = state.getThreadClock(this.getThread());
        VectorClock L_l = state.getLockVectorClock(this.getLock());
        C_t.update(L_l);
        return false;
    }

    /**
     * Handles release events, updating the vector clocks associated with the current
     * thread and lock data in the given {@code HBState}.
     *
     * <p>This method sets the vector clock of the lock being released (L_l) to the vector clock of
     * the current thread's vector clock (C_t). Current thread's clock is then incremented.
     *
     * @param state Stores all the vector clocks and other data required.
     * @return Always returns false, as release events cannot be in a race.
     */
    @Override
    public boolean handleSubRelease(HBState state) {
        VectorClock C_t = state.getThreadClock(this.getThread());
        VectorClock L_l = state.getLockVectorClock(this.getLock());
        L_l.update(C_t);
        state.incrementThreadClock(this.getThread());
        return false;
    }

    /**
     * Handles read events, updating the vector clocks associated with the current
     * thread and variable data in the given {@code HBState}.
     *
     * <p>This method checks if the accessed variable's write vector clock (W_v) is less than or equal
     * to the current thread's vector clock (C_t). A race is reported if W_v is not less than or
     * equal to C_t. The accessed variable's read vector clock (R_v) is then updated with C_t.
     *
     * @param state Stores all the vector clocks and other data required.
     * @return Returns true if a race is detected.
     */
    @Override
    public boolean handleSubRead(HBState state) {
        if (this.isVolatile() || this.toIgnore()) {
            return false;
        }
        boolean raceDetected = false;
        VectorClock C_t = state.getThreadClock(this.getThread());
        VectorClock W_v = state.getWriteVectorClock(this.getVariable());
        VectorClock R_v = state.getReadVectorClock(this.getVariable());

        if (!W_v.isLessThanOrEqual(C_t)) {
            raceDetected = true;
        }

        R_v.update(C_t);

        return raceDetected;
    }

    /**
     * Handles write events, updating the vector clocks associated with the current
     * thread and variable data in the given {@code HBState}.
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
    public boolean handleSubWrite(HBState state) {
        if (this.isVolatile() || this.toIgnore()) {
            return false;
        }
        boolean raceDetected = false;
        VectorClock C_t = state.getThreadClock(this.getThread());
        VectorClock W_v = state.getWriteVectorClock(this.getVariable());
        VectorClock R_v = state.getReadVectorClock(this.getVariable());

        if (!(R_v.isLessThanOrEqual(C_t))) {
            raceDetected = true;
        }
        if (!raceDetected) {
            raceDetected = !W_v.isLessThanOrEqual(C_t);
        }

        W_v.update(C_t);

        return raceDetected;
    }

    /**
     * Handles fork events, updating the vector clocks associated with the current
     * thread data in the given {@code HBState}.
     *
     * <p>This method updates the vector clock of the thread that is being forked (C_tc) with the
     * the current thread's vector clock (C_t). Current thread's clock is then incremented.
     *
     * @param state Stores all the vector clocks and other data required.
     * @return Always returns false, as fork events cannot be in a race.
     */
    @Override
    public boolean handleSubFork(HBState state) {
        VectorClock C_t = state.getThreadClock(this.getThread());
        VectorClock C_tc = state.getThreadClock(this.getTarget());
        C_tc.update(C_t);
        state.incrementThreadClock(this.getThread());
        return false;
    }

    /**
     * Handles join events, updating the vector clocks associated with the current
     * thread data in the given {@code HBState}.
     *
     * <p>This method updates the vector clock of the current thread (C_t) with the
     * the vector clock of the thread that is being joined on (C_tc). Current thread's
     * clock is then incremented.
     *
     * @param state Stores all the vector clocks and other data required.
     * @return Always returns false, as join events cannot be in a race.
     */
    @Override
    public boolean handleSubJoin(HBState state) {
        VectorClock C_t = state.getThreadClock(this.getThread());
        VectorClock C_tc = state.getThreadClock(this.getTarget());
        C_t.update(C_tc);
        return false;
    }

    @Override
    public void printRaceInfoTransactionType(HBState state) {
    }

    @Override
    public boolean handleSubBegin(HBState state) {
        return false;
    }

    @Override
    public boolean handleSubEnd(HBState state) {
        return false;
    }
    // CHECKSTYLE.ON: LocalVariableName
}
