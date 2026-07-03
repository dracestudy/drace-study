package com.runtimeverification.rvpredict.racedetection.lockset;

import java.util.HashMap;
import java.util.HashSet;

import com.runtimeverification.rvpredict.racedetection.engine.State;
import com.runtimeverification.rvpredict.util.Logger;

public class LockSetState extends State {
    // Data for the algorithm data
    public HashMap<Long, HashMap<Long, Integer>> locksHeldNesting;
    public HashMap<Long, HashSet<Long>> locksHeldSet;
    public HashMap<Long, HashSet<Long>> lockSet;
    public long dummyReadLock;
    public HashMap<Long, Long> threadLocks;

    private final Logger logger;

    public LockSetState() {
        this.logger = Logger.getGlobal();
        this.dummyReadLock = -1;
        this.threadLocks = new HashMap<Long, Long>();
        this.locksHeldNesting = new HashMap<Long, HashMap<Long, Integer>>();
        this.locksHeldSet = new HashMap<Long, HashSet<Long>>();
        lockSet = new HashMap<Long, HashSet<Long>>();
    }

    public void checkAndAddThread(long tid) {
        if (!threadLocks.containsKey(tid)) {
            long thLock = tid;
            this.threadLocks.put(tid, thLock);
            this.locksHeldNesting.put(tid, new HashMap<>());
            this.locksHeldNesting.get(tid).put(thLock, 1);
            this.locksHeldSet.put(tid, new HashSet<>());
            this.locksHeldSet.get(tid).add(thLock);
        }
    }

    @Override
    public void printMemory() {
        this.logger.report("Dummy method printMemory called", Logger.MSGTYPE.VERBOSE);
    }

    @Override
    public void reset() {
        dummyReadLock = -1;
        threadLocks.clear();
        locksHeldNesting.clear();
        locksHeldSet.clear();
        lockSet.clear();
    }

}
