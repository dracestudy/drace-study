package com.runtimeverification.rvpredict.racedetection.goldilocks;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;

import com.runtimeverification.rvpredict.order.VectorClock;
import com.runtimeverification.rvpredict.racedetection.event.RaceDetectionEvent;
import com.runtimeverification.rvpredict.racedetection.event.RapidEventType;
import com.runtimeverification.rvpredict.util.Logger;

public class GoldilocksEvent extends RaceDetectionEvent<GoldilocksState> {
    private final Logger logger;
    private final ByteBuffer emptyVC;

    public GoldilocksEvent() {
        this.logger = Logger.getGlobal();
        VectorClock vc = new VectorClock();
        this.emptyVC = vc.toBinaryFormat();
    }

    @Override
    public ByteBuffer toThreadVectorClockBytes(GoldilocksState state) {
        return emptyVC;
    }

    @Override
    public void preProcess(GoldilocksState state) {
        RapidEventType tp = this.getType();

        state.checkAndAddThread(this.getThread());
        if (tp.isExtremeType()) {
            state.checkAndAddThread(this.getTarget());
        }
    }

    @Override
    public boolean handle(GoldilocksState state) {
        return this.handleSub(state);
    }

    @Override
    public void printRaceInfoLockType(GoldilocksState state) {
        this.logger.report("Dummy method printRaceInfoLockType called", Logger.MSGTYPE.VERBOSE);
    }

    @Override
    public void printRaceInfoAccessType(GoldilocksState state) {
        this.logger.report("Dummy method printRaceInfoAccessType called", Logger.MSGTYPE.VERBOSE);
    }

    @Override
    public void printRaceInfoExtremeType(GoldilocksState state) {
        this.logger.report("Dummy method printRaceInfoExtremeType called", Logger.MSGTYPE.VERBOSE);
    }

    /**
     * Handles acquire events. Updating the data associated with the current
     * in the given {@code GoldilocksState}.
     *
     * @param state Stores all the data required.
     * @return Always returns false, as acquire events cannot be in a race.
     */
    @Override
    public boolean handleSubAcquire(GoldilocksState state) {
        for (long variable : state.writeLockSet.keySet()) {
            if (state.writeLockSet.get(variable).contains(this.getLock())) {
                state.writeLockSet.get(variable).add(state.threadLocks.get(this.getThread()));
            }
        }
        for (long tid : state.threadLocks.keySet()) {
            long threadLock = state.threadLocks.get(tid);
            if (state.readLockSet.containsKey(threadLock)) {
                for (long variable : state.readLockSet.get(threadLock).keySet()) {
                    if (state.readLockSet.get(threadLock).get(variable).contains(this.getLock())) {
                        state.readLockSet
                                .get(threadLock)
                                .get(variable)
                                .add(state.threadLocks.get(this.getThread()));
                    }
                }
            }
        }
        return false;
    }

    /**
     * Handles release events. Updating the data associated with the current
     * in the given {@code GoldilocksState}.
     *
     * @param state Stores all the data required.
     * @return Always returns false, as release events cannot be in a race.
     */
    @Override
    public boolean handleSubRelease(GoldilocksState state) {
        for (long x : state.writeLockSet.keySet()) {
            if (state.writeLockSet.get(x).contains(state.threadLocks.get(this.getThread()))) {
                state.writeLockSet.get(x).add(this.getLock());
            }
        }
        for (long tid : state.threadLocks.keySet()) {
            long threadLock = state.threadLocks.get(tid);
            if (state.readLockSet.containsKey(threadLock)) {
                for (long x : state.readLockSet.get(threadLock).keySet()) {
                    if (state.readLockSet
                            .get(threadLock)
                            .get(x)
                            .contains(state.threadLocks.get(this.getThread()))) {
                        state.readLockSet.get(threadLock).get(x).add(this.getLock());
                    }
                }
            }
        }
        return false;
    }

    /**
     * Handles read events. Updating the data associated with the current
     * in the given {@code GoldilocksState}.
     *
     * @param state Stores all the data required.
     * @return Returns true if a race is detected.
     */
    @Override
    public boolean handleSubRead(GoldilocksState state) {
        if (this.isVolatile || this.toIgnore) {
            return false;
        }
        boolean raceDetected = false;

        long tid = this.getThread();
        long variable = this.getVariable();
        long threadLock = state.threadLocks.get(tid);

        if (state.writeLockSet.containsKey(variable)) {
            if (!state.writeLockSet.get(variable).contains(threadLock)) {
                raceDetected = true;
            }
        }

        if (!state.readLockSet.containsKey(threadLock)) {
            state.readLockSet.put(threadLock, new HashMap<Long, HashSet<Long>>());
        }
        state.readLockSet.get(threadLock).put(variable, new HashSet<Long>());
        state.readLockSet.get(threadLock).get(variable).add(threadLock);

        return raceDetected;
    }

    /**
     * Handles write events. Updating the data associated with the current
     * in the given {@code GoldilocksState}.
     *
     * @param state Stores all the data required.
     * @return Returns true if a race is detected.
     */
    @Override
    public boolean handleSubWrite(GoldilocksState state) {
        if (this.isVolatile || this.toIgnore) {
            return false;
        }
        boolean raceDetected = false;

        long tid = this.getThread();
        long variable = this.getVariable();
        long threadLock = state.threadLocks.get(tid);

        if (state.writeLockSet.containsKey(variable)) {
            if (!state.writeLockSet.get(variable).contains(threadLock)) {
                raceDetected = true;
            }
        }
        for (long u : state.threadLocks.keySet()) {
            long targetLock = state.threadLocks.get(u);
            if (state.readLockSet.containsKey(targetLock)) {
                if (state.readLockSet.get(targetLock).containsKey(variable)) {
                    if (!state.readLockSet.get(targetLock).get(variable).contains(threadLock)) {
                        raceDetected = true;
                    }
                }
            }
        }
        state.writeLockSet.put(variable, new HashSet<Long>());
        state.writeLockSet.get(variable).add(threadLock);

        return raceDetected;
    }

    /**
     * Handles fork events. Updating the data associated with the current
     * in the given {@code GoldilocksState}.
     *
     * @param state Stores all the data required.
     * @return Always returns false, as fork events cannot be in a race.
     */
    @Override
    public boolean handleSubFork(GoldilocksState state) {
        long targetLock = state.threadLocks.get(this.getTarget());

        for (long variable : state.writeLockSet.keySet()) {
            if (state.writeLockSet.get(variable).contains(state.threadLocks.get(this.getThread()))) {
                state.writeLockSet.get(variable).add(targetLock);
            }
        }
        for (long tid : state.threadLocks.keySet()) {
            long threadLock = state.threadLocks.get(tid);
            if (state.readLockSet.containsKey(threadLock)) {
                for (long variable : state.readLockSet.get(threadLock).keySet()) {
                    if (state.readLockSet
                            .get(threadLock)
                            .get(variable)
                            .contains(state.threadLocks.get(this.getThread()))) {
                        state.readLockSet.get(threadLock).get(variable).add(targetLock);
                    }
                }
            }
        }
        return false;
    }

    /**
     * Handles join events. Updating the data associated with the current
     * in the given {@code GoldilocksState}.
     *
     * @param state Stores all the data required.
     * @return Always returns false, as join events cannot be in a race.
     */
    @Override
    public boolean handleSubJoin(GoldilocksState state) {
        long targetLock = state.threadLocks.get(this.getTarget());

        for (long variable : state.writeLockSet.keySet()) {
            if (state.writeLockSet.get(variable).contains(targetLock)) {
                state.writeLockSet.get(variable).add(state.threadLocks.get(this.getThread()));
            }
        }
        for (long tid : state.threadLocks.keySet()) {
            long threadLock = state.threadLocks.get(tid);
            if (state.readLockSet.containsKey(threadLock)) {
                for (long variable : state.readLockSet.get(threadLock).keySet()) {
                    if (state.readLockSet.get(threadLock).get(variable).contains(targetLock)) {
                        state.readLockSet
                                .get(threadLock)
                                .get(variable)
                                .add(state.threadLocks.get(this.getThread()));
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void printRaceInfoTransactionType(GoldilocksState state) {
    }

    @Override
    public boolean handleSubBegin(GoldilocksState state) {
        return false;
    }

    @Override
    public boolean handleSubEnd(GoldilocksState state) {
        return false;
    }
}
