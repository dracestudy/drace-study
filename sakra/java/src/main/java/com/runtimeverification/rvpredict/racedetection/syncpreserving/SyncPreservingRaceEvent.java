package com.runtimeverification.rvpredict.racedetection.syncpreserving;

import java.nio.ByteBuffer;
import java.util.HashSet;

import com.runtimeverification.rvpredict.racedetection.event.RaceDetectionEvent;
import com.runtimeverification.rvpredict.racedetection.event.RapidEventType;
import com.runtimeverification.rvpredict.util.Logger;
import com.runtimeverification.rvpredict.util.Pair;
import com.runtimeverification.rvpredict.util.Quintet;
import com.runtimeverification.rvpredict.util.ll.EfficientLLView;
import com.runtimeverification.rvpredict.util.vectorclock.VectorClock;

public class SyncPreservingRaceEvent extends RaceDetectionEvent<SyncPreservingRaceState> {
    // Keep consistent with the naming in the paper
    // https://dl.acm.org/doi/10.1145/3434317
    // CHECKSTYLE.OFF: LocalVariableName
    // CHECKSTYLE.OFF: ParameterName
    static final int flushEventDuration = 100000;
    static int flush_event_ctr = 0;

    private final Logger logger;

    public SyncPreservingRaceEvent() {
        this.logger = Logger.getGlobal();
    }

    @Override
    public ByteBuffer toThreadVectorClockBytes(SyncPreservingRaceState state) {
        VectorClock C_t = state.clockThread.get(this.getThread());
        if (C_t == null) {
            C_t = new VectorClock();
        }
        return C_t.toBinaryFormat();
    }

    @Override
    public void preProcess(SyncPreservingRaceState state) {}

    @Override
    public boolean handle(SyncPreservingRaceState state) {
        RapidEventType tp = this.getType();

        // Check if this is a write and the last event was a write
        // on the same variable by the same thread
        // If so, skip everything
        if (tp.isWrite()) {
            if (state.lastType != null) {
                if (state.lastType.isWrite()) {
                    if (state.lastDecor == this.getVariable()) {
                        if (state.lastThread == this.getThread()) {
                            return state.lastAnswer;
                        }
                    }
                }
            }
        }

        state.checkAndAddThread(this.getThread());
        if (tp.isAccessType()) {
            state.checkAndAddVariable(this.getVariable());
        }
        if (tp.isLockType()) {
            state.checkAndAddLock(this.getLock());
        }
        if (tp.isExtremeType()) {
            state.checkAndAddThread(this.getTarget());
        }

        boolean toReturn;
        toReturn = this.handleSub(state);

        state.lastDecor = -1;
        state.lastThread = -1;
        state.lastType = null;
        state.lastAnswer = toReturn;
        if (this.getType().isWrite()) {
            state.lastDecor = this.getVariable();
            state.lastThread = this.getThread();
            state.lastType = tp;
        }

        flush_event_ctr = flush_event_ctr + 1;
        if (flush_event_ctr == flushEventDuration) {
            state.flushAcquireViews();
            flush_event_ctr = 0;
        }

        return toReturn;
    }

    protected void printRaceInfoHelper(SyncPreservingRaceState state, String typeInfo) {
        StringBuilder output = new StringBuilder("#");
        output.append(Integer.toString(getLocId()));
        output.append("|");
        output.append(this.getType().toString());
        output.append("|");
        output.append(typeInfo);
        output.append("|");
        VectorClock C_t = state.clockThread.get(this.getThread());
        output.append(C_t.toString());
        output.append("|");
        output.append(Long.toString(this.getThread()));
        this.logger.report(output.toString(), Logger.MSGTYPE.VERBOSE);
    }

    @Override
    public void printRaceInfoLockType(SyncPreservingRaceState state) {
        printRaceInfoHelper(state, this.getLock().toString());
    }

    @Override
    public void printRaceInfoAccessType(SyncPreservingRaceState state) {
        printRaceInfoHelper(state, Long.toString(this.getVariable()));
    }

    @Override
    public void printRaceInfoExtremeType(SyncPreservingRaceState state) {
        printRaceInfoHelper(state, Long.toString(this.getTarget()));
    }

    /**
     * Handles acquire events. Updating the data associated with the current
     * in the given {@code SyncPreservingRaceState}.
     *
     * @param state Stores all the data required.
     * @return Always returns false, as acquire events cannot be in a race.
     */
    @Override
    public boolean handleSubAcquire(SyncPreservingRaceState state) {
        Long t = this.getThread();
        Long l = this.getLock();

        if (!state.threadsAccessingLocks.containsKey(l)) {
            state.threadsAccessingLocks.put(l, new HashSet<Long>());
        }
        state.threadsAccessingLocks.get(l).add(t);

        state.numAcquires = state.numAcquires + 1;
        state.incClockThread(getThread());
        state.updateViewAsWriterAtAcquire(l, t);

        state.addLockHeld(t, l);

        return false;
    }

    /**
     * Handles release events. Updating the data associated with the current
     * in the given {@code SyncPreservingRaceState}.
     *
     * @param state Stores all the data required.
     * @return Always returns false, as release events cannot be in a race.
     */
    @Override
    public boolean handleSubRelease(SyncPreservingRaceState state) {
        Long t = this.getThread();
        Long l = this.getLock();
        state.incClockThread(t);
        state.updateViewAsWriterAtRelease(l, t);
        state.removeLockHeld(t, l);

        return false;
    }

    private RapidEventType getEarlierConflictingEvent(
            SyncPreservingRaceState state, Long t1, Long var, RapidEventType tp, Long t2) {
        Pair<VectorClock, Integer> writeTriplet = null;
        int writeClock = 0;
        EfficientLLView<Long, Pair<VectorClock, Integer>> store_write =
                state.accessInfo.get(t2).get(RapidEventType.WRITE).get(var);
        if (!store_write.isEmpty(t1)) {
            writeTriplet = store_write.bottom(t1);
            writeClock = writeTriplet.second;
        }
        Pair<VectorClock, Integer> readTriplet = null;
        int readClock = 0;
        if (tp.equals(RapidEventType.WRITE)) {
            EfficientLLView<Long, Pair<VectorClock, Integer>> store_read =
                    state.accessInfo.get(t2).get(RapidEventType.READ).get(var);
            if (!store_read.isEmpty(t1)) {
                readTriplet = store_read.bottom(t1);
                readClock = readTriplet.second;
            }
        }

        if (writeClock > 0 && readClock > 0) {
            return readClock < writeClock ? RapidEventType.READ : RapidEventType.WRITE;
        } else if (writeClock > 0) {
            return RapidEventType.WRITE;
        } else if (readClock > 0) {
            return RapidEventType.READ;
        }
        return null;
    }

    /**
     * Checks for races involving the specified thread 'tid', variable 'var', and event type 'tp'
     * in the given 'state' under the predecessor clock 'C_pred_t'. Races are detected by examining
     * conflicting events in the access information store associated with each thread accessing 'var'.
     * If races are found, the conflicting events are flushed eagerly to maintain consistency.
     *
     * @param state      The {@code SyncPreservingRaceState} representing the current state of the race detection.
     * @param tid        The thread ID 'tid' associated with the event being checked for races.
     * @param var        The variable 'var' associated with the event being checked for races.
     * @param tp         The event type 'tp' of the event being checked for races.
     * @param C_pred_t   The predecessor vector clock 'C_pred_t' associated with the event being checked for races.
     * @return {@code true} if races are detected, {@code false} otherwise.
     *
     * @see SyncPreservingRaceState
     * @see VectorClock
     * @see RapidEventType
     * @see EfficientLLView
     */
    private boolean checkRaces(
            SyncPreservingRaceState state, Long tid, Long var, RapidEventType tp, VectorClock C_pred_t) {
        HashSet<Long> threadSet_x = state.variableToThreadSet.get(var);
        for (Long u : threadSet_x) {
            if (!u.equals(tid)) {

                while (true) {
                    RapidEventType aprime = getEarlierConflictingEvent(state, tid, var, tp, u);
                    if (!(aprime == null)) {
                        EfficientLLView<Long, Pair<VectorClock, Integer>> store =
                                state.accessInfo.get(u).get(aprime).get(var);
                        if (!store.isEmpty(tid)) {
                            Pair<VectorClock, Integer> conflictingTriplet = store.bottom(tid);
                            VectorClock C_pred_u = conflictingTriplet.first;
                            int C_u_u = conflictingTriplet.second;

                            // Cheap check
                            if (C_u_u <= state.getIndex(C_pred_t, u)) {
                                state.flushConflictingEventsEagerly(
                                        store, tid, tp, var, u, C_u_u, C_pred_t);
                                continue;
                            }

                            Quintet<Long, RapidEventType, Long, RapidEventType, Long>
                                    acquireInfoKey =
                                            new Quintet<
                                                    Long,
                                                    RapidEventType,
                                                    Long,
                                                    RapidEventType,
                                                    Long>(u, aprime, tid, tp, var);

                            VectorClock I = new VectorClock(C_pred_t);
                            I.update(C_pred_u);
                            VectorClock lastIeal = state.lastIdeal.get(acquireInfoKey);
                            I.update(lastIeal);
                            I.copyFrom(state.fixPointIdeal(acquireInfoKey, I, tid));

                            state.lastIdeal.put(acquireInfoKey, new VectorClock(I));

                            if (!(C_u_u <= state.getIndex(I, u))) {
                                return true;
                            } else {
                                state.flushConflictingEventsEagerly(store, tid, tp, var, u, C_u_u, I);
                            }
                        }
                    } else {
                        break;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Handles read events. Updating the data associated with the current
     * in the given {@code SyncPreservingRaceState}.
     *
     * @param state Stores all the data required.
     * @return Returns true if a race is detected.
     */
    @Override
    public boolean handleSubRead(SyncPreservingRaceState state) {
        Long t = this.getThread();
        Long v = this.getVariable();
        RapidEventType tp = this.getType();
        VectorClock C_t = state.clockThread.get(t);

        VectorClock C_pred_t = new VectorClock(C_t);
        // Check race
        boolean raceDetected = false;
        if (!this.isVolatile && !this.toIgnore) {
            boolean emptyLS = state.updateLocksetAtAccess(t, v, tp);

            if (emptyLS) {
                raceDetected = checkRaces(state, t, v, tp, C_pred_t);
            }
        }

        // Update and send clocks
        state.incClockThread(t);
        VectorClock LW_v = state.lastWriteVariable.get(v);
        C_t.update(LW_v);
        // Eager computation of closure. Do it after checking for races.
        C_t.copyFrom(state.updatePointersAtAccessAndGetFixPoint(t, C_t));

        Pair<VectorClock, Integer> infoToStore =
                new Pair<VectorClock, Integer>(C_pred_t, state.getIndex(C_t, t));
        state.accessInfo.get(t).get(RapidEventType.READ).get(v).pushTop(infoToStore);

        return raceDetected;
    }

    /**
     * Handles write events. Updating the data associated with the current
     * in the given {@code SyncPreservingRaceState}.
     *
     * @param state Stores all the data required.
     * @return Returns true if a race is detected.
     */
    @Override
    public boolean handleSubWrite(SyncPreservingRaceState state) {
        Long t = this.getThread();
        Long v = this.getVariable();
        RapidEventType tp = this.getType();
        VectorClock C_t = state.clockThread.get(t);

        VectorClock C_pred_t = new VectorClock(C_t);
        // Check race
        boolean raceDetected = false;
        if (!this.isVolatile && !this.toIgnore) {
            boolean emptyLS = state.updateLocksetAtAccess(t, v, tp);

            if (emptyLS) {
                raceDetected = checkRaces(state, t, v, tp, C_pred_t);
            }
        }

        // Do send stuff
        state.incClockThread(t);
        VectorClock LW_v = state.lastWriteVariable.get(v);
        LW_v.copyFrom(C_t);
        Pair<VectorClock, Integer> infoToStore =
                new Pair<VectorClock, Integer>(C_pred_t, state.getIndex(C_t, t));
        state.accessInfo.get(t).get(RapidEventType.WRITE).get(v).pushTop(infoToStore);

        return raceDetected;
    }

    /**
     * Handles fork events. Updating the data associated with the current
     * in the given {@code SyncPreservingRaceState}.
     *
     * @param state Stores all the data required.
     * @return Always returns false, as fork events cannot be in a race.
     */
    @Override
    public boolean handleSubFork(SyncPreservingRaceState state) {
        VectorClock C_t = state.clockThread.get(this.getThread());
        VectorClock C_tc = state.clockThread.get(this.getTarget());
        C_tc.copyFrom(C_t);
        state.setIndex(C_tc, this.getTarget(), 1);
        state.incClockThread(getThread());
        return false;
    }

    /**
     * Handles join events. Updating the data associated with the current
     * in the given {@code SyncPreservingRaceState}.
     *
     * @param state Stores all the data required.
     * @return Always returns false, as join events cannot be in a race.
     */
    @Override
    public boolean handleSubJoin(SyncPreservingRaceState state) {
        VectorClock C_t = state.clockThread.get(this.getThread());
        VectorClock C_tc = state.clockThread.get(this.getTarget());
        C_t.update(C_tc);
        state.incClockThread(getThread());
        // Eager computation of closure.
        C_t.copyFrom(state.updatePointersAtAccessAndGetFixPoint(this.getThread(), C_t));
        return false;
    }

    @Override
    public void printRaceInfoTransactionType(SyncPreservingRaceState state) {}

    @Override
    public boolean handleSubBegin(SyncPreservingRaceState state) {
        return false;
    }

    @Override
    public boolean handleSubEnd(SyncPreservingRaceState state) {
        return false;
    }
    // CHECKSTYLE.ON: LocalVariableName
    // CHECKSTYLE.ON: ParameterName
}
