package com.runtimeverification.rvpredict.racedetection.hbepoch;

import java.util.ArrayList;
import java.util.HashMap;

import com.runtimeverification.rvpredict.racedetection.engine.State;
import com.runtimeverification.rvpredict.util.Logger;
import com.runtimeverification.rvpredict.util.vectorclock.SemiAdaptiveVC;
import com.runtimeverification.rvpredict.util.vectorclock.VectorClock;

public class HBEpochState extends State {
    // Internal data
    private int numThreads;
    private int numLocks;
    private int numVariables;

    // Data used for algorithm
    private HashMap<Long, Long> clockThread;
    private HashMap<Long, VectorClock> hbPredecessorThread;
    private HashMap<Long, VectorClock> lastReleaseLock;
    private HashMap<Long, SemiAdaptiveVC> readVariable;
    private HashMap<Long, SemiAdaptiveVC> writeVariable;

    private final Logger logger;

    public HBEpochState() {
        this.logger = Logger.getGlobal();
        this.numThreads = 0;
        this.numLocks = 0;
        this.numVariables = 0;
        this.clockThread = new HashMap<>();
        this.hbPredecessorThread = new HashMap<>();
        this.lastReleaseLock = new HashMap<>();
        this.readVariable = new HashMap<>();
        this.writeVariable = new HashMap<>();
    }

    public void checkAndAddThread(long tid) {
        if (!clockThread.containsKey(tid)) {
            this.logger.report("New thread found " + this.numThreads, Logger.MSGTYPE.PROGRESS);
            this.numThreads++;
            clockThread.put(tid, 1L);
            hbPredecessorThread.put(tid, new VectorClock());
        }
    }

    public void checkAndAddLock(long lock) {
        if (!lastReleaseLock.containsKey(lock)) {
            this.logger.report("New lock found " + this.numLocks, Logger.MSGTYPE.PROGRESS);
            this.numLocks++;
            lastReleaseLock.put(lock, new VectorClock());
        }
    }

    public void checkAndAddVariable(long var) {
        if (!readVariable.containsKey(var)) {
            this.numVariables++;
            readVariable.put(var, new SemiAdaptiveVC());
            writeVariable.put(var, new SemiAdaptiveVC());
        }
    }

    public VectorClock generateVectorClockFromClockThread(long tid) {
        VectorClock pred = getHBPredecessorThreadVectorClock(tid);
        VectorClock hbClock = new VectorClock(pred);
        long thValue = this.clockThread.get(tid);

        hbClock.put(Math.toIntExact(tid), Math.toIntExact(thValue));
        return hbClock;
    }

    public void incrementClockThread(long tid) {
        this.clockThread.computeIfPresent(tid, (k, var) -> var + 1L);
    }

    public long getThreadClock(long tid) {
        return this.clockThread.get(tid);
    }

    public VectorClock getLockVectorClock(long lock) {
        return this.lastReleaseLock.get(lock);
    }

    public SemiAdaptiveVC getReadVectorClock(long var) {
        return this.readVariable.get(var);
    }

    public SemiAdaptiveVC getWriteVectorClock(long var) {
        return this.writeVariable.get(var);
    }

    public VectorClock getHBPredecessorThreadVectorClock(long tid) {
        return this.hbPredecessorThread.get(tid);
    }

    public void setIndex(VectorClock vc, long tid, int val) {
        vc.put(Math.toIntExact(tid), val);
    }

    public int getIndex(VectorClock vc, long tid) {
        return vc.get(Math.toIntExact(tid));
    }

    public void printThreadClock() {
        ArrayList<VectorClock> printVC = new ArrayList<VectorClock>();
        for (long thread : this.clockThread.keySet()) {
            VectorClock threadClock = generateVectorClockFromClockThread(thread);
            printVC.add(threadClock);
        }
        this.logger.report(printVC.toString(), Logger.MSGTYPE.VERBOSE);
        this.logger.report("\n%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%", Logger.MSGTYPE.VERBOSE);
    }

    public void printMemory() {
        this.logger.report("Number of threads = " + Integer.toString(this.numThreads), Logger.MSGTYPE.VERBOSE);
        this.logger.report("Number of locks = " + Integer.toString(this.numLocks), Logger.MSGTYPE.VERBOSE);
        this.logger.report("Number of variables = " + Integer.toString(this.numVariables), Logger.MSGTYPE.VERBOSE);

    }

    @Override
    public void reset() {
        numThreads = 0;
        numLocks = 0;
        numVariables = 0;
        clockThread.clear();
        hbPredecessorThread.clear();
        lastReleaseLock.clear();
        readVariable.clear();
        writeVariable.clear();
    }
}
