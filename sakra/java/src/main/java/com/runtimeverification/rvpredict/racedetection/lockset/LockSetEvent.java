package com.runtimeverification.rvpredict.racedetection.lockset;

import java.nio.ByteBuffer;
import java.util.HashSet;

import com.runtimeverification.rvpredict.order.VectorClock;
import com.runtimeverification.rvpredict.racedetection.event.RaceDetectionEvent;
import com.runtimeverification.rvpredict.racedetection.event.RapidEventType;
import com.runtimeverification.rvpredict.util.Logger;

public class LockSetEvent extends RaceDetectionEvent<LockSetState> {
    private final Logger logger;
    private final ByteBuffer emptyVC;

    public LockSetEvent() {
        this.logger = Logger.getGlobal();
        VectorClock vc = new VectorClock();
        this.emptyVC = vc.toBinaryFormat();
    }

    @Override
    public ByteBuffer toThreadVectorClockBytes(LockSetState state) {
        return emptyVC;
    }

    @Override
    public void preProcess(LockSetState state) {
        RapidEventType tp = this.getType();

        state.checkAndAddThread(this.getThread());
        if (tp.isExtremeType()) {
            state.checkAndAddThread(this.getTarget());
        }
    }

    @Override
    public boolean handle(LockSetState state) {
        return this.handleSub(state);
    }

    @Override
    public void printRaceInfoLockType(LockSetState state) {
        this.logger.report("Dummy method printRaceInfoLockType called", Logger.MSGTYPE.VERBOSE);
    }

    @Override
    public void printRaceInfoAccessType(LockSetState state) {
        this.logger.report("Dummy method printRaceInfoAccessType called", Logger.MSGTYPE.VERBOSE);
    }

    @Override
    public void printRaceInfoExtremeType(LockSetState state) {
        this.logger.report("Dummy method printRaceInfoExtremeType called", Logger.MSGTYPE.VERBOSE);
    }

    /**
     * Handles acquire events. Updating the data associated with the current
     * in the given {@code LockSetState}.
     *
     * @param state Stores all the data required.
     * @return Always returns false, as acquire events cannot be in a race.
     */
    @Override
    public boolean handleSubAcquire(LockSetState state) {
        int lock = 1;
        if (state.locksHeldNesting.get(this.getThread()).containsKey(this.getLock())) {
            lock = 1 + state.locksHeldNesting.get(this.getThread()).get(this.getLock());
        }
        state.locksHeldNesting.get(this.getThread()).put(this.getLock(), lock);
        state.locksHeldSet.get(this.getThread()).add(this.getLock());
        return false;
    }

    /**
     * Handles release events. Updating the data associated with the current
     * in the given {@code LockSetState}.
     *
     * @param state Stores all the data required.
     * @return Always returns false, as release events cannot be in a race.
     */
    @Override
    public boolean handleSubRelease(LockSetState state) {
        int lock;
        if (state.locksHeldNesting.get(this.getThread()).containsKey(this.getLock())) {
            lock = state.locksHeldNesting.get(this.getThread()).get(this.getLock()) - 1;
        } else {
            throw new IllegalArgumentException(
                    "Thread " + Long.toString(this.getThread())
                              + " is releasing lock "
                              + Long.toString(this.getLock())
                              + " without acquiring it enough number of times .");
        }
        state.locksHeldNesting.get(this.getThread()).put(this.getLock(), lock);
        if (lock == 0) {
            state.locksHeldSet.get(this.getThread()).remove(this.getLock());
        }
        return false;
    }

    /**
     * Handles read events. Updating the data associated with the current
     * in the given {@code LockSetState}.
     *
     * @param state Stores all the data required.
     * @return Returns true if lockset principle is violated.
     */
    @Override
    public boolean handleSubRead(LockSetState state) {
        boolean raceDetected = false;
        if (!state.lockSet.containsKey(this.getVariable())) {
            state.lockSet.put(
                    getVariable(), new HashSet<Long>(state.locksHeldSet.get(this.getThread())));
            state.lockSet.get(this.getVariable()).add(state.dummyReadLock);
        }
        if (state.lockSet.get(this.getVariable()).contains(state.dummyReadLock)) {
            state.lockSet
                    .get(this.getVariable())
                    .retainAll(state.locksHeldSet.get(this.getThread()));
            state.lockSet.get(this.getVariable()).add(state.dummyReadLock);
        } else {
            state.lockSet
                    .get(this.getVariable())
                    .retainAll(state.locksHeldSet.get(this.getThread()));
        }
        if (!this.isVolatile && !this.toIgnore) {
            if (state.lockSet.get(this.getVariable()).isEmpty()) {
                raceDetected = true;
            }
        }
        return raceDetected;
    }

    /**
     * Handles write events. Updating the data associated with the current
     * in the given {@code LockSetState}.
     *
     * @param state Stores all the data required.
     * @return Returns true if lockset principle is violated.
     */
    @Override
    public boolean handleSubWrite(LockSetState state) {
        boolean raceDetected = false;
        if (!state.lockSet.containsKey(this.getVariable())) {
            state.lockSet.put(
                    getVariable(), new HashSet<Long>(state.locksHeldSet.get(this.getThread())));
        }
        state.lockSet.get(this.getVariable()).retainAll(state.locksHeldSet.get(this.getThread()));
        if (!this.isVolatile && !this.toIgnore) {
            if (state.lockSet.get(this.getVariable()).isEmpty()) {
                raceDetected = true;
            }
        }
        return raceDetected;
    }

    @Override
    public boolean handleSubFork(LockSetState state) {
        return false;
    }

    @Override
    public boolean handleSubJoin(LockSetState state) {
        return false;
    }

    @Override
    public void printRaceInfoTransactionType(LockSetState state) {}

    @Override
    public boolean handleSubBegin(LockSetState state) {
        return false;
    }

    @Override
    public boolean handleSubEnd(LockSetState state) {
        return false;
    }
}
