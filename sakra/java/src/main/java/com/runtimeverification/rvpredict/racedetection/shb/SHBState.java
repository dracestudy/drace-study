package com.runtimeverification.rvpredict.racedetection.shb;

import java.util.HashMap;

import com.runtimeverification.rvpredict.racedetection.engine.State;
import com.runtimeverification.rvpredict.util.Logger;
import com.runtimeverification.rvpredict.util.vectorclock.VectorClock;

public class SHBState extends State {
    // Internal data
    private int numThreads;
    private int numLocks;
    private int numVariables;

    // Data used for algorithm
    private HashMap<Long, VectorClock> clockThread;
    private HashMap<Long, VectorClock> lastReleaseLock;
    private HashMap<Long, VectorClock> readVariable;
    private HashMap<Long, VectorClock> writeVariable;
    private HashMap<Long, VectorClock> lastWriteVariable;

    // Book-keeping the last-write's location
    private HashMap<Long, Integer> lastWriteVariableLocId;

    private final Logger logger;

    public SHBState() {
        this.logger = Logger.getGlobal();
        this.numThreads = 0;
        this.numLocks = 0;
        this.numVariables = 0;
        this.clockThread = new HashMap<>();
        this.lastReleaseLock = new HashMap<>();
        this.readVariable = new HashMap<>();
        this.writeVariable = new HashMap<>();
        this.lastWriteVariable = new HashMap<>();
        this.lastWriteVariableLocId = new HashMap<>();
    }

    public void checkAndAddThread(long tid) {
        if (!clockThread.containsKey(tid)) {
            this.logger.report("New thread found " + this.numThreads, Logger.MSGTYPE.PROGRESS);
            this.numThreads++;
            VectorClock vc = new VectorClock();
            vc.put(Math.toIntExact(tid), 1);
            clockThread.put(tid, vc);
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
            lastWriteVariable.put(var, new VectorClock());
            lastWriteVariableLocId.put(var, -1); // Initialize loc id's to be -1
        }
    }

    public void incClockThread(long tid) {
        getThreadVectorClock(tid).increment(Math.toIntExact(tid));
    }

    public VectorClock getThreadVectorClock(long tid) {
        return this.clockThread.get(tid);
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

    public VectorClock getLastWriteVectorClock(long var) {
        return this.lastWriteVariable.get(var);
    }

    public int getLWLocId(long var) {
        return this.lastWriteVariableLocId.get(var);
    }

    public void setLWLocId(long var, int loc) {
        this.lastWriteVariableLocId.put(var, loc);
    }

    public void setIndex(VectorClock vc, long tid, int val) {
        vc.put(Math.toIntExact(tid), val);
    }

    public int getIndex(VectorClock vc, long tid) {
        return vc.get(Math.toIntExact(tid));
    }

    public void printThreadClock() {
        for (long tid : clockThread.keySet()) {
            VectorClock threadClock = getThreadVectorClock(tid);
            this.logger.report(tid + ":" + threadClock.toString() + " ", Logger.MSGTYPE.VERBOSE);
        }
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
        lastReleaseLock.clear();
        readVariable.clear();
        writeVariable.clear();
        lastWriteVariable.clear();
        lastWriteVariableLocId.clear();
    }
}
