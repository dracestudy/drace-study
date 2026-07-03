package com.runtimeverification.rvpredict.racedetection.syncpreserving;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.runtimeverification.rvpredict.racedetection.engine.State;
import com.runtimeverification.rvpredict.racedetection.event.RapidEventType;
import com.runtimeverification.rvpredict.util.Logger;
import com.runtimeverification.rvpredict.util.Pair;
import com.runtimeverification.rvpredict.util.PairComparators;
import com.runtimeverification.rvpredict.util.Quintet;
import com.runtimeverification.rvpredict.util.Triplet;
import com.runtimeverification.rvpredict.util.TripletComparators;
import com.runtimeverification.rvpredict.util.ll.EfficientLLView;
import com.runtimeverification.rvpredict.util.ll.EfficientLinkedList;
import com.runtimeverification.rvpredict.util.ll.EfficientNode;
import com.runtimeverification.rvpredict.util.vectorclock.VectorClock;

public class SyncPreservingRaceState extends State {
    // Keep consistent with the naming in the paper
    // https://dl.acm.org/doi/10.1145/3434317
    //CHECKSTYLE.OFF: LocalVariableName
    //CHECKSTYLE.OFF: ParameterName
    //CHECKSTYLE.OFF: DeclarationOrder - better organization with this order
    public static RapidEventType[] accessTypes = {RapidEventType.READ, RapidEventType.WRITE};

    // == Data used for algorithm ==

    // 0. ThreadSet
    public HashSet<Long> threads;
    public HashSet<Long> locks;
    public HashSet<Long> variables;

    // 1. Vector clocks
    public HashMap<Long, VectorClock> clockThread;
    public HashMap<Long, VectorClock> lastWriteVariable;
    public HashMap<Quintet<Long, RapidEventType, Long, RapidEventType, Long>, VectorClock>
            lastIdeal;

    // 2. Scalars
    public long numAcquires; // counts the total number of acquire events seen

    // 3. Views
    public HashMap<Long, HashMap<RapidEventType,
                            HashMap<Long, EfficientLLView<Long, Pair<VectorClock, Integer>>>>> accessInfo;
    public HashMap<Long, HashMap<Long,
                            EfficientLLView<
                                    Quintet<Long, RapidEventType, Long, RapidEventType, Long>,
                                    Triplet<Integer, Long, VectorClock>>>> acquireInfo;
    // == End of data used for algorithm ==

    public TripletComparators.FirstComparator<Integer, Long, VectorClock>
            firstComparatorAcquireInfo;
    public PairComparators.FirstComparator<VectorClock, Integer> firstComparatorAccessInfo;
    public PairComparators.SecondComparator<VectorClock, Integer> secondComparatorAccessInfo;
    public PairComparators.FirstComparator<Integer, HashSet<Long>> firstComparatorOpenLockInfo;
    public Pair<VectorClock, VectorClock> bottomVCTriplet;

    // == parameter flags ==
    public boolean forceOrder;

    // Lockset optimization
    public HashMap<Long, HashSet<Long>> threadToLocksHeld;
    public HashMap<Long, HashSet<Long>> variableToLockset;
    private Long readLock;
    private HashMap<Long, Long> threadLock;

    // Reducing number of pointers
    public HashMap<Long, HashSet<Long>> variableToThreadSet; // x -> set of threads that access x
    public HashMap<Long, HashSet<Long>> threadsAccessingLocks;

    // fastpath
    public long lastThread = -1;
    public long lastDecor = -1;
    public RapidEventType lastType = null;
    public boolean lastAnswer = false;

    // == Internal data ==
    private int numThreads;
    private int numLocks;
    private int numVariables;
    private HashMap<Long, HashMap<
                            Long,
                            HashSet<Quintet<Long, RapidEventType, Long, RapidEventType, Long>>>>
            threadPairToAcquireInfoKeys;
    private HashMap<Long, HashSet<Quintet<Long, RapidEventType, Long, RapidEventType, Long>>>
            secondThreadToAcquireInfoKeys; // t -> {< t1 , a1, t, a2, x_> | t1, a1, a2, x}
    private HashMap<Long, Quintet<Long, RapidEventType, Long, RapidEventType, Long>>
            threadToDummyKey;
    private HashSet<Quintet<Long, RapidEventType, Long, RapidEventType, Long>> acquireInfoKeys;

    private final Logger logger;

    public SyncPreservingRaceState() {
        this.logger = Logger.getGlobal();
        initInternalData();
        initData();
    }

    private void initInternalData() {
        this.numThreads = 0;
        this.numLocks = 0;
        this.numVariables = 0;
        this.readLock = -1L;
        this.threadLock = new HashMap<Long, Long>();
    }

    public void initData() {
        this.threads = new HashSet<Long>();
        this.locks = new HashSet<Long>();
        this.variables = new HashSet<Long>();

        this.secondThreadToAcquireInfoKeys =
                new HashMap<
                        Long, HashSet<Quintet<Long, RapidEventType, Long, RapidEventType, Long>>>();
        this.threadToDummyKey =
                new HashMap<Long, Quintet<Long, RapidEventType, Long, RapidEventType, Long>>();
        this.threadPairToAcquireInfoKeys =
                new HashMap<Long, HashMap<Long, HashSet<Quintet<
                                                            Long,
                                                            RapidEventType,
                                                            Long,
                                                            RapidEventType,
                                                            Long>>>>();
        this.acquireInfoKeys =
                new HashSet<Quintet<Long, RapidEventType, Long, RapidEventType, Long>>();

        // initialize clockThread
        this.clockThread = new HashMap<>();

        // initialize lastWriteVariable
        this.lastWriteVariable = new HashMap<>();

        // initialize lastIdeal
        this.lastIdeal =
                new HashMap<
                        Quintet<Long, RapidEventType, Long, RapidEventType, Long>, VectorClock>();

        // initialize numAcquires
        this.numAcquires = 0L;

        // initialize ReadInfo and WriteInfo
        this.accessInfo =
                new HashMap<Long, HashMap<RapidEventType, HashMap<
                                        Long,
                                        EfficientLLView<Long, Pair<VectorClock, Integer>>>>>();

        // initialize AcquireInfo
        this.acquireInfo =
                new HashMap<Long, HashMap<Long, EfficientLLView<
                                        Quintet<Long, RapidEventType, Long, RapidEventType, Long>,
                                        Triplet<Integer, Long, VectorClock>>>>();

        firstComparatorAcquireInfo =
                new TripletComparators.FirstComparator<Integer, Long, VectorClock>();
        firstComparatorAccessInfo = new PairComparators.FirstComparator<VectorClock, Integer>();
        secondComparatorAccessInfo = new PairComparators.SecondComparator<VectorClock, Integer>();
        firstComparatorOpenLockInfo = new PairComparators.FirstComparator<Integer, HashSet<Long>>();
        bottomVCTriplet = new Pair<VectorClock, VectorClock>(new VectorClock(), new VectorClock());

        this.threadToLocksHeld = new HashMap<Long, HashSet<Long>>();
        this.variableToLockset = new HashMap<Long, HashSet<Long>>();

        this.variableToThreadSet = new HashMap<Long, HashSet<Long>>();
        this.threadsAccessingLocks = new HashMap<Long, HashSet<Long>>();
    }

    public void updateThreadVarInfo(Long t1, Long t2, Long v) {
        Quintet<Long, RapidEventType, Long, RapidEventType, Long> new_key_read_write =
                new Quintet<Long, RapidEventType, Long, RapidEventType, Long>(
                        t1, RapidEventType.READ, t2, RapidEventType.WRITE, v);
        Quintet<Long, RapidEventType, Long, RapidEventType, Long> new_key_write_read =
                new Quintet<Long, RapidEventType, Long, RapidEventType, Long>(
                        t1, RapidEventType.WRITE, t2, RapidEventType.READ, v);
        Quintet<Long, RapidEventType, Long, RapidEventType, Long> new_key_write_write =
                new Quintet<Long, RapidEventType, Long, RapidEventType, Long>(
                        t1, RapidEventType.WRITE, t2, RapidEventType.WRITE, v);

        HashSet<Quintet<Long, RapidEventType, Long, RapidEventType, Long>> otherKeys =
                this.threadPairToAcquireInfoKeys.get(t1).get(t2);
        for (Long s : this.threads) {
            for (Long l : this.locks) {
                this.acquireInfo.get(s).get(l).addKeyToTopOfKeys(new_key_read_write, otherKeys);
                this.acquireInfo.get(s).get(l).addKeyToTopOfKeys(new_key_write_read, otherKeys);
                this.acquireInfo.get(s).get(l).addKeyToTopOfKeys(new_key_write_write, otherKeys);
            }
        }

        this.acquireInfoKeys.add(new_key_read_write);
        this.acquireInfoKeys.add(new_key_write_read);
        this.acquireInfoKeys.add(new_key_write_write);
        this.threadPairToAcquireInfoKeys.get(t1).get(t2).add(new_key_read_write);
        this.threadPairToAcquireInfoKeys.get(t1).get(t2).add(new_key_write_read);
        this.threadPairToAcquireInfoKeys.get(t1).get(t2).add(new_key_write_write);
        this.secondThreadToAcquireInfoKeys.get(t2).add(new_key_read_write);
        this.secondThreadToAcquireInfoKeys.get(t2).add(new_key_write_read);
        this.secondThreadToAcquireInfoKeys.get(t2).add(new_key_write_write);

        this.lastIdeal.put(new_key_read_write, new VectorClock());
        this.lastIdeal.put(new_key_write_read, new VectorClock());
        this.lastIdeal.put(new_key_write_write, new VectorClock());
    }

    public void checkAndAddThread(Long tid) {
        if (!this.threads.contains(tid)) {
            this.numThreads++;
            this.threads.add(tid);
            VectorClock vc = new VectorClock();
            vc.put(Math.toIntExact(tid), 1);
            clockThread.put(tid, vc);

            long tLock = tid;
            this.threadLock.put(tid, tLock);

            Quintet<Long, RapidEventType, Long, RapidEventType, Long> dummy =
                    new Quintet<Long, RapidEventType, Long, RapidEventType, Long>(
                            tid, null, null, null, null);
            this.threadToDummyKey.put(tid, dummy);
            this.acquireInfoKeys.add(dummy);

            this.secondThreadToAcquireInfoKeys.put(
                    tid, new HashSet<Quintet<Long, RapidEventType, Long, RapidEventType, Long>>());
            HashMap<Long, HashSet<Quintet<Long, RapidEventType, Long, RapidEventType, Long>>>
                    acqInfo_tid =
                            new HashMap<Long, HashSet<Quintet<Long, RapidEventType, Long, RapidEventType, Long>>>();
            this.threadPairToAcquireInfoKeys.put(tid, acqInfo_tid);

            for (Long t2 : this.threads) {
                if (tid.equals(t2)) {
                    continue;
                }
                this.threadPairToAcquireInfoKeys
                        .get(tid)
                        .put(t2, new HashSet<Quintet<Long, RapidEventType, Long, RapidEventType, Long>>());
                this.threadPairToAcquireInfoKeys
                        .get(t2)
                        .put(tid, new HashSet<Quintet<Long, RapidEventType, Long, RapidEventType, Long>>());
            }

            this.accessInfo.put(
                    tid,
                    new HashMap<
                            RapidEventType,
                            HashMap<Long, EfficientLLView<Long, Pair<VectorClock, Integer>>>>());
            this.accessInfo
                    .get(tid)
                    .put(RapidEventType.READ, new HashMap<Long, EfficientLLView<Long, Pair<VectorClock, Integer>>>());
            this.accessInfo
                    .get(tid)
                    .put(RapidEventType.WRITE, new HashMap<Long, EfficientLLView<Long, Pair<VectorClock, Integer>>>());
            this.acquireInfo.put(tid,
                    new HashMap<Long, EfficientLLView<
                                        Quintet<Long, RapidEventType, Long, RapidEventType, Long>,
                                        Triplet<Integer, Long, VectorClock>>>());
            this.threadToLocksHeld.put(tid, new HashSet<Long>());

            for (Long lock : this.locks) {
                this.acquireInfo
                        .get(tid)
                        .put(lock, new EfficientLLView<
                                        Quintet<Long, RapidEventType, Long, RapidEventType, Long>,
                                        Triplet<Integer, Long, VectorClock>>(this.acquireInfoKeys));

                for (Long t2 : this.threads) {
                    this.acquireInfo.get(t2).get(lock).addKeyToBottom(dummy);
                }
            }

            for (Long var : this.variables) {
                this.variableToThreadSet.get(var).add(tid);
                this.accessInfo
                        .get(tid)
                        .get(RapidEventType.READ)
                        .put(var, new EfficientLLView<Long, Pair<VectorClock, Integer>>(this.threads));
                this.accessInfo
                        .get(tid)
                        .get(RapidEventType.WRITE)
                        .put(var, new EfficientLLView<Long, Pair<VectorClock, Integer>>(this.threads));
                for (Long t2 : this.threads) {
                    if (t2.equals(tid)) {
                        continue;
                    }

                    this.accessInfo.get(t2).get(RapidEventType.READ).get(var).addKeyToBottom(tid);
                    this.accessInfo.get(t2).get(RapidEventType.WRITE).get(var).addKeyToBottom(tid);

                    updateThreadVarInfo(tid, t2, var);
                    updateThreadVarInfo(t2, tid, var);
                }
            }
        }
    }

    public void checkAndAddLock(Long lock) {
        if (!this.locks.contains(lock)) {
            locks.add(lock);
            this.numLocks++;
            for (Long t : this.threads) {
                this.acquireInfo.get(t)
                                .put(lock, new EfficientLLView<
                                        Quintet<Long, RapidEventType, Long, RapidEventType, Long>,
                                        Triplet<Integer, Long, VectorClock>>(this.acquireInfoKeys));
            }
        }
    }

    public void checkAndAddVariable(Long var) {
        if (!this.variables.contains(var)) {
            variables.add(var);
            this.numVariables++;
            this.lastWriteVariable.put(var, new VectorClock());
            this.variableToThreadSet.put(var, this.threads);

            for (Long t : this.threads) {
                this.accessInfo
                        .get(t)
                        .get(RapidEventType.READ)
                        .put(var, new EfficientLLView<Long, Pair<VectorClock, Integer>>(this.threads));
                this.accessInfo
                        .get(t)
                        .get(RapidEventType.WRITE)
                        .put(var, new EfficientLLView<Long, Pair<VectorClock, Integer>>(this.threads));
                for (Long u : this.threads) {
                    if (u.equals(t)) {
                        continue;
                    }

                    updateThreadVarInfo(u, t, var);
                }
            }
            this.variableToLockset.put(var, null);
        }
    }

    public void incClockThread(long tid) {
        this.clockThread.get(tid).increment(Math.toIntExact(tid));
    }

    public void setIndex(VectorClock vc, Long tid, int val) {
        vc.put(Math.toIntExact(tid), val);
    }

    public int getIndex(VectorClock vc, Long tid) {
        return vc.get(Math.toIntExact(tid));
    }

    public void addLockHeld(Long tid, Long lock) {
        this.threadToLocksHeld.get(tid).add(lock);
    }

    public void removeLockHeld(Long tid, Long lock) {
        this.threadToLocksHeld.get(tid).remove(lock);
    }

    public boolean updateLocksetAtAccess(Long tid, Long var, RapidEventType tp) {
        HashSet<Long> vSet = this.variableToLockset.get(var);
        HashSet<Long> lockset = new HashSet<Long>();
        if (tp.isRead()) {
            lockset.add(this.readLock);
        }
        lockset.add(this.threadLock.get(tid));
        lockset.addAll(this.threadToLocksHeld.get(tid));
        if (vSet == null) {
            this.variableToLockset.put(var, lockset);
        } else {
            vSet.retainAll(lockset);
        }
        return this.variableToLockset.get(var).isEmpty();
    }

    public void updateViewAsWriterAtAcquire(Long lock, Long tid) {
        VectorClock C_t = this.clockThread.get(tid);
        int n = C_t.get(Math.toIntExact(tid));
        long m = this.numAcquires;
        acquireInfo.get(tid).get(lock).pushTop(new Triplet<Integer, Long, VectorClock>(n, m, null));
    }

    public void updateViewAsWriterAtRelease(Long lock, Long tid) {
        VectorClock C_t_copy = new VectorClock(this.clockThread.get(tid));
        Triplet<Integer, Long, VectorClock> info = acquireInfo.get(tid).get(lock).top();
        Triplet<Integer, Long, VectorClock> new_info =
                new Triplet<Integer, Long, VectorClock>(info.first, info.second, C_t_copy);
        acquireInfo.get(tid).get(lock).setTop(new_info);
    }

    public void flushAcquireViews() {
        for (HashMap<Long, EfficientLLView<
                                Quintet<Long, RapidEventType, Long, RapidEventType, Long>,
                                Triplet<Integer, Long, VectorClock>>>
                l_to_store : this.acquireInfo.values()) {
            for (EfficientLLView<
                            Quintet<Long, RapidEventType, Long, RapidEventType, Long>,
                            Triplet<Integer, Long, VectorClock>>
                    store : l_to_store.values()) {
                store.flush();
            }
        }
    }


    /**
     * Modifies I_old. If tid is not null, then advance acquireInfoKey to dummyKey(tid).
     * Otherwise use acquireKey as is.
     *
     * @param acquireInfoKey A quintet storing &lt;Thread, EventType, Thread, EventType, Variable&gt;
     * @param I_old          The vector clock to be modified.
     * @param tid            The thread ID (tid) if not null. If provided, advances the 'acquireInfoKey'
     *                       to dummyKey(tid) in the first iteration.
     * @return The modified vector clock.
     */
    public VectorClock fixPointIdeal(
            Quintet<Long, RapidEventType, Long, RapidEventType, Long> acquireInfoKey,
            VectorClock I_old,
            Long tid) {
        VectorClock I = new VectorClock(I_old);
        boolean first_iter = true;
        while (true) {
            HashSet<Long> threads_in_I = new HashSet<Long>();
            HashMap<Long, Triplet<Integer, Long, VectorClock>> base_triplets =
                    new HashMap<Long, Triplet<Integer, Long, VectorClock>>();
            for (Long v : this.threads) {
                int I_v = this.getIndex(I, v);
                if (I_v > 0) {
                    threads_in_I.add(v);
                    Triplet<Integer, Long, VectorClock> triplet_I_v =
                            new Triplet<Integer, Long, VectorClock>(I_v, 0L, null);
                    base_triplets.put(v, triplet_I_v);
                }
            }
            for (Long l : this.threadsAccessingLocks.keySet()) {
                long LA_l = -1;
                VectorClock maxVC_match_l = null;
                Long max_thread = null;
                Pair<EfficientNode<Triplet<Integer, Long, VectorClock>>, Integer> max_nextNode =
                        null;
                HashSet<Long> threads_accessing_l_and_in_I =
                        new HashSet<Long>(this.threadsAccessingLocks.get(l));
                threads_accessing_l_and_in_I.retainAll(threads_in_I);
                for (Long v : threads_accessing_l_and_in_I) {
                    EfficientLLView<
                                    Quintet<Long, RapidEventType, Long, RapidEventType, Long>,
                                    Triplet<Integer, Long, VectorClock>>
                            store = this.acquireInfo.get(v).get(l);
                    // if store is empty then skip
                    if (store.isEmpty()) {
                        this.threadsAccessingLocks.get(l).remove(v);
                        if (this.threadsAccessingLocks.get(l).isEmpty()) {
                            this.threadsAccessingLocks.remove(l);
                        }
                        continue;
                    }

                    // read the value of dummyKey(t)
                    if (first_iter) {
                        if (tid != null) {
                            Quintet<Long, RapidEventType, Long, RapidEventType, Long> dummyKey =
                                    this.threadToDummyKey.get(tid);
                            store.advanceKeyToTarget(acquireInfoKey, dummyKey);
                        }
                    }

                    if (store.isEmpty(acquireInfoKey)) {
                        continue;
                    }

                    Triplet<Integer, Long, VectorClock> bottomPointer =
                            store.bottom(acquireInfoKey);
                    if (bottomPointer.first > this.getIndex(I, v)) {
                        continue;
                    }

                    // change the following to With Update
                    Triplet<Boolean, Triplet<Integer, Long, VectorClock>,
                                    Pair<EfficientNode<Triplet<Integer, Long, VectorClock>>, Integer>>
                            found_lockTriplet_nextNodeIter =
                                    store.getMaxLowerBoundPenultimate(
                                            acquireInfoKey,
                                            base_triplets.get(v),
                                            this.firstComparatorAcquireInfo);
                    if (found_lockTriplet_nextNodeIter.first) {
                        Triplet<Integer, Long, VectorClock> lockTriplet = found_lockTriplet_nextNodeIter.second;
                        long GI_v_l = lockTriplet.second;
                        VectorClock C_match_v_l = lockTriplet.third;
                        if (LA_l == -1) {
                            LA_l = GI_v_l;
                            maxVC_match_l = C_match_v_l;
                            max_thread = v;
                            max_nextNode = found_lockTriplet_nextNodeIter.third;
                        } else {
                            if (GI_v_l > LA_l) {
                                I.update(maxVC_match_l);
                                this.acquireInfo
                                        .get(max_thread)
                                        .get(l)
                                        .setBottom(acquireInfoKey, max_nextNode);

                                LA_l = GI_v_l;
                                maxVC_match_l = C_match_v_l;
                                max_thread = v;
                                max_nextNode = found_lockTriplet_nextNodeIter.third;
                            } else {
                                I.update(C_match_v_l);
                                this.acquireInfo.get(v)
                                                .get(l)
                                                .setBottom(acquireInfoKey, found_lockTriplet_nextNodeIter.third);
                            }
                        }
                    }
                }
            }
            if (I.isEqual(I_old)) {
                break;
            }
            I_old.copyFrom(I);
            first_iter = false;
        }
        return I;
    }

    /**
     * Remove all events 'e' of thread 'v' with clock C and pred_clock P such that
     * lb_local_clock &lt;= P[u] and C[v] &lt;= I[v].
     *
     * @param t               The thread ID 't' representing the thread of the current event.
     * @param a               The event type 'a' of the current event.
     * @param x               The variable 'x' associated with the current event.
     * @param u               The thread ID 'u' representing the thread whose clock is used in the lower bound.
     * @param lb_local_clock The lower bound local clock value 'lb_local_clock' to compare with the predecessor clocks.
     * @param ub_clock       The upper bound vector clock 'ub_clock' representing the maximum observed clocks
     *                       for each thread. Events with clocks exceeding these bounds are removed.
     */
    private void clearViews(
            Long t, RapidEventType a, Long x, Long u, int lb_local_clock, VectorClock ub_clock) {
        VectorClock lb_clock = new VectorClock();
        this.setIndex(lb_clock, u, lb_local_clock);
        Pair<VectorClock, Integer> lb = new Pair<VectorClock, Integer>(lb_clock, -1);
        HashSet<Long> threadSet_x = this.variableToThreadSet.get(x);
        for (Long v : threadSet_x) {
            if (v.equals(t)) {
                continue;
            }
            int ub_local_clock = this.getIndex(ub_clock, v);
            Pair<VectorClock, Integer> ub = new Pair<VectorClock, Integer>(null, ub_local_clock);
            for (RapidEventType aprime : SyncPreservingRaceState.accessTypes) {
                if (RapidEventType.conflicting(a, aprime)) {
                    this.accessInfo
                            .get(v)
                            .get(aprime)
                            .get(x)
                            .removePrefixWithinReturnMin(
                                    t,
                                    lb,
                                    this.firstComparatorAccessInfo,
                                    ub,
                                    this.secondComparatorAccessInfo);
                }
            }
        }
    }

    /**
     * Removes all events from the specified 'store' that cannot be in a race with the given event 'e2'.
     *
     * @param store           The {@code EfficientLLView} representing the store of vector clocks associated
     *                        with the events.
     * @param t               The thread ID 't' representing the source thread of the event 'e2'.
     * @param a               The event type 'a' of the event 'e2'.
     * @param x               The variable 'x' associated with the event 'e2'.
     * @param u               The thread ID 'u' representing the thread whose clock is used in the lower bound.
     * @param lb_local_clock The lower bound local clock value 'lb_local_clock' to compare with the predecessor clocks.
     * @param ub_clock       The upper bound vector clock 'ub_clock' representing the maximum observed clocks
     *                       for each thread. Events with clocks exceeding these bounds are removed.
     */
    public void flushConflictingEventsEagerly(
            EfficientLLView<Long, Pair<VectorClock, Integer>> store,
            Long t,
            RapidEventType a,
            Long x,
            Long u,
            int lb_local_clock,
            VectorClock ub_clock) {
        store.advanceKeyByOne(t);
        this.clearViews(t, a, x, u, lb_local_clock, ub_clock);
    }

    public VectorClock updatePointersAtAccessAndGetFixPoint(Long t, VectorClock I) {
        Quintet<Long, RapidEventType, Long, RapidEventType, Long> t_key =
                this.threadToDummyKey.get(t);
        VectorClock new_I = fixPointIdeal(t_key, I, null);

        return new_I;
    }

    public void printThreadClock() {
        ArrayList<VectorClock> printVC = new ArrayList<VectorClock>();
        for (Long thread : this.threads) {
            VectorClock C_t = this.clockThread.get(thread);
            printVC.add(C_t);
        }
        this.logger.report(printVC.toString(), Logger.MSGTYPE.VERBOSE);
        this.logger.report("\n%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%", Logger.MSGTYPE.VERBOSE);
    }

    public void printMemory() {
        this.logger.report("Number of threads = " + Integer.toString(this.numThreads), Logger.MSGTYPE.VERBOSE);
        this.logger.report("Number of locks = " + Integer.toString(this.numLocks), Logger.MSGTYPE.VERBOSE);
        this.logger.report("Number of variables = " + Integer.toString(this.numVariables), Logger.MSGTYPE.VERBOSE);
    }
    //CHECKSTYLE.ON: LocalVariableName
    //CHECKSTYLE.ON: DeclarationOrder
    //CHECKSTYLE.ON: ParameterName

    @Override
    public void reset() {
        initInternalData();
        initData();
        lastThread = -1;
        lastDecor = -1;
        lastType = null;
        lastAnswer = false;
    }

}
