package com.runtimeverification.rvpredict.racedetection.fhb;

import java.util.HashMap;
import java.util.HashSet;

import com.runtimeverification.rvpredict.racedetection.engine.State;
import com.runtimeverification.rvpredict.util.IntegerPair;
import com.runtimeverification.rvpredict.util.Logger;
import com.runtimeverification.rvpredict.util.vectorclock.VectorClock;

public class FHBState extends State {
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

    private int eventId;

    // Book-keeping the last-write's location
    private HashMap<Long, Integer> lastWriteVariableLocId;

    // Book-keeping the location of the last-read by every thread
    private HashMap<Long, HashMap<Long, Integer>> lastReadVariableLocId;
    private HashMap<Long, HashMap<Long, VectorClock>> lastReadVariableClock;
    private HashMap<Long, HashMap<Long, Integer>> lastReadVariableEventId;

    // Tracking the pc-pairs in race
    private HashSet<IntegerPair> raceLocPairs;

    public FHBState() {
        logger = Logger.getGlobal();
        this.numThreads = 0;
        this.numLocks = 0;
        this.numVariables = 0;
        // initialize threadClocks
        this.threadClocks = new HashMap<>();

        // initialize lastReleaseLock
        this.lastReleaseLock = new HashMap<>();

        // initialize readVariable
        this.readVariable = new HashMap<>();

        // initialize writeVariable
        this.writeVariable = new HashMap<>();

        this.eventId = 0;

        // initialize locationIds
        this.lastWriteVariableLocId = new HashMap<>();
        this.lastReadVariableLocId = new HashMap<>();
        this.lastReadVariableClock = new HashMap<>();
        this.lastReadVariableEventId = new HashMap<>();

        this.raceLocPairs = new HashSet<IntegerPair>();
    }

    public void checkAndAddThread(long tid) {
        if (!this.threadClocks.containsKey(tid)) {
            this.logger.report("New thread found " + this.numThreads, Logger.MSGTYPE.PROGRESS);
            this.numThreads++;
            VectorClock vc = new VectorClock();
            vc.put(Math.toIntExact(tid), 1);
            this.threadClocks.put(tid, vc);

            for (long var : this.readVariable.keySet()) {
                this.lastReadVariableLocId.get(var).put(tid, -1);
                this.lastReadVariableClock.get(var).put(tid, new VectorClock());
                this.lastReadVariableEventId.get(var).put(tid, -1);
            }
        }
    }

    public void checkAndAddLock(long lock) {
        if (!this.lastReleaseLock.containsKey(lock)) {
            this.logger.report("New lock found " + this.numLocks, Logger.MSGTYPE.PROGRESS);
            this.numLocks++;
            this.lastReleaseLock.put(lock, new VectorClock());
        }
    }

    public void checkAndAddVariable(long var, boolean inCLInit) {
        if (!this.readVariable.containsKey(var)) {
            this.readVariable.put(var, new VectorClock());
            this.writeVariable.put(var, new VectorClock());
            this.lastWriteVariableLocId.put(var, -1); // Initialize loc id's to be -1
            this.lastReadVariableLocId.put(var, new HashMap<>());
            this.lastReadVariableClock.put(var, new HashMap<>());
            this.lastReadVariableEventId.put(var, new HashMap<>());

            for (long tid : this.threadClocks.keySet()) {
                this.lastReadVariableLocId.get(var).put(tid, -1);
                this.lastReadVariableClock.get(var).put(tid, new VectorClock());
                this.lastReadVariableEventId.get(var).put(tid, -1);
            }
            this.numVariables++;
        }
    }

    public void incrementThreadClock(long tid) {
        getThreadVectorClock(tid).increment(Math.toIntExact(tid));
    }

    public VectorClock getThreadVectorClock(long tid) {
        return this.threadClocks.get(tid);
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

    public int getLWLocId(long var) {
        return this.lastWriteVariableLocId.get(var);
    }

    public void setLWLocId(long var, int loc) {
        this.lastWriteVariableLocId.put(var, loc);
    }

    public int getLRLocId(long var, long tid) {
        return this.lastReadVariableLocId.get(var).get(tid);
    }

    public void setLastReadData(long var, long tid, int loc, VectorClock threadClock, boolean inCLInit) {
        this.lastReadVariableLocId.get(var).put(tid, loc);
        this.eventId = this.eventId + 1;
        this.lastReadVariableEventId.get(var).put(tid, this.eventId);
        this.lastReadVariableClock.get(var).get(tid).copyFrom(threadClock);
    }

    public boolean checkRaceWithReadsAndAddLocPairs(long tid, long var, VectorClock threadClock, int locId) {
        boolean raceDetected = false;
        int maxEventId = -1;
        int readLocId = -1;
        for (long threadId : this.threadClocks.keySet()) {
            VectorClock readClock = this.lastReadVariableClock.get(var).get(threadId);
            if (!readClock.isLessThanOrEqual(threadClock)) {
                raceDetected = true;
                int eventId = this.lastReadVariableEventId.get(var).get(threadId);
                if (eventId > maxEventId) {
                    maxEventId = eventId;
                    readLocId = this.lastReadVariableLocId.get(var).get(threadId);
                }
            }
        }
        if (raceDetected) {
            this.addLocPair(readLocId, locId);
        }
        return raceDetected;
    }

    public void addLocPair(int i1, int i2) {
        if (i1 < 0 || i2 < 0) {
            throw new IllegalArgumentException("Negative Location ID is invalid.");
        }
        IntegerPair intpr = new IntegerPair(i1, i2);
        this.raceLocPairs.add(intpr);
    }

    public HashSet<IntegerPair> getLocPairs() {
        return this.raceLocPairs;
    }

    public void setIndex(VectorClock vc, long tid, int val) {
        vc.put(Math.toIntExact(tid), val);
    }

    public int getIndex(VectorClock vc, long tid) {
        return vc.get(Math.toIntExact(tid));
    }

    public void printThreadClock() {
        for (long thread : this.threadClocks.keySet()) {
            VectorClock threadClock = getThreadVectorClock(thread);
            this.logger.report(threadClock.toString(), Logger.MSGTYPE.VERBOSE);
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
        threadClocks.clear();
        lastReleaseLock.clear();
        readVariable.clear();
        writeVariable.clear();
        eventId = 0;
        lastWriteVariableLocId.clear();
        lastReadVariableLocId.clear();
        lastReadVariableClock.clear();
        lastReadVariableEventId.clear();
        raceLocPairs.clear();
    }

}
