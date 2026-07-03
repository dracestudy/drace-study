package com.runtimeverification.rvpredict.racedetection.goldilocks;

import java.util.HashMap;
import java.util.HashSet;

import com.runtimeverification.rvpredict.racedetection.engine.State;
import com.runtimeverification.rvpredict.util.Logger;

public class GoldilocksState extends State {
    // Data for the algorithm data
    public HashMap<Long, HashSet<Long>> writeLockSet;
    public HashMap<Long, HashMap<Long, HashSet<Long>>> readLockSet;
    public HashMap<Long, Long> threadLocks;

    private final Logger logger;
    private int numThreads;

    public GoldilocksState() {
        logger = Logger.getGlobal();
        this.numThreads = 0;
        this.writeLockSet = new HashMap<Long, HashSet<Long>>();
        this.readLockSet = new HashMap<Long, HashMap<Long, HashSet<Long>>>();
        this.threadLocks = new HashMap<Long, Long>();
    }

    public void checkAndAddThread(long tid) {
        if (!threadLocks.containsKey(tid)) {
            this.logger.report("New thread found " + this.numThreads, Logger.MSGTYPE.PROGRESS);
            this.numThreads++;
            long thLock = tid;
            this.threadLocks.put(tid, thLock);
        }
    }

    @Override
    public void printMemory() {
        this.logger.report("Dummy method printMemory called", Logger.MSGTYPE.VERBOSE);
    }

    @Override
    public void reset() {
        numThreads = 0;
        writeLockSet.clear();
        readLockSet.clear();
        threadLocks.clear();
    }

}
