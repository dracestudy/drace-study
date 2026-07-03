package com.runtimeverification.rvpredict.racedetection.hb;

import java.util.ArrayList;
import java.util.HashMap;

import com.runtimeverification.rvpredict.racedetection.engine.State;
import com.runtimeverification.rvpredict.util.Logger;
import com.runtimeverification.rvpredict.util.vectorclock.VectorClock;

public class HBState extends State {
    private final Logger logger;
    // Internal data
    private int numThreads;
    private int numLocks;
    private int numVariables;

    // Data used for algorithm
    private HashMap<Long, VectorClock> threadClocks;
    private HashMap<Long, VectorClock> lastReleaseLock;
    private HashMap<Long, VectorClock> readVariable;
    private HashMap<Long, VectorClock> writeVariable;

    public HBState() {
        logger = Logger.getGlobal();
        this.numThreads = 0;
        this.numLocks = 0;
        this.numVariables = 0;
        this.threadClocks = new HashMap<>();
        this.lastReleaseLock = new HashMap<>();
        this.readVariable = new HashMap<>();
        this.writeVariable = new HashMap<>();
    }

    public void checkAndAddThread(long tid) {
        if (!this.threadClocks.containsKey(tid)) {
            this.logger.report("New thread found " + this.numThreads, Logger.MSGTYPE.PROGRESS);
            this.numThreads++;
            VectorClock vc = new VectorClock();
            vc.put(Math.toIntExact(tid), 1);
            this.threadClocks.put(tid, vc);
        }
    }

    public void checkAndAddLock(long lock) {
        if (!lastReleaseLock.containsKey(lock)) {
            this.logger.report("New lock found " + this.numLocks, Logger.MSGTYPE.PROGRESS);
            this.numLocks++;
            lastReleaseLock.put(lock, new VectorClock());
        }
    }

    public void checkAndAddVariable(long var, boolean inCLInit) {
        if (!readVariable.containsKey(var)) {
            this.numVariables++;
            readVariable.put(var, new VectorClock());
            writeVariable.put(var, new VectorClock());
        }
    }

    public void incrementThreadClock(long tid) {
        this.threadClocks.get(tid).increment(Math.toIntExact(tid));
    }

    public VectorClock getLockVectorClock(long lock) {
        return this.lastReleaseLock.get(lock);
    }

    public VectorClock getReadVectorClock(long var) {
        return this.readVariable.get(var);
    }

    public VectorClock getWriteVectorClock(long var) {
        return this.writeVariable.get(var);
    }

    public VectorClock getThreadClock(long tid) {
        return this.threadClocks.get(tid);
    }

    public void printThreadClock() {
        ArrayList<VectorClock> printVC = new ArrayList<VectorClock>();
        for (long thread : this.threadClocks.keySet()) {
            VectorClock threadClock = getThreadClock(thread);
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
        threadClocks.clear();
        lastReleaseLock.clear();
        readVariable.clear();
        writeVariable.clear();
    }
}
