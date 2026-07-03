package com.runtimeverification.rvpredict.racedetection.wcp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

import com.runtimeverification.rvpredict.racedetection.engine.State;
import com.runtimeverification.rvpredict.util.Logger;
import com.runtimeverification.rvpredict.util.vectorclock.ClockPair;
import com.runtimeverification.rvpredict.util.vectorclock.VectorClock;

/**
 * Manages the clocks and other data structures used by the WCP algorithm.
 */
public class WCPState extends State {
    // CHECKSTYLE.OFF: LocalVariableName - more readable with these variable names from the paper
    // CHECKSTYLE.OFF: MemberName - more readable
    // https://dl.acm.org/doi/10.1145/3062341.3062374

    // Data used for algorithm
    public HashMap<Long, Long> clockThread;
    public HashMap<Long, VectorClock> WCPThread;
    public HashMap<Long, VectorClock> HBPredecessorThread;

    public HashMap<Long, VectorClock> HBPredecessorLock;
    public HashMap<Long, VectorClock> WCPPredecessorThread;
    public HashMap<Long, VectorClock> WCPPredecessorLock;

    public HashMap<Long, HashMap<Long, HashMap<Long, VectorClock>>> lastReleaseLockReadVariableThread;
    public HashMap<Long, HashMap<Long, HashMap<Long, VectorClock>>> lastReleaseLockWriteVariableThread;

    public HashMap<Long, VectorClock> readVariable;
    public HashMap<Long, VectorClock> writeVariable;

    public WCPView view;

    // Data used for online tracking of locks and variables
    public HashMap<Long, Stack<HashSet<Long>>> mapThreadReadVarSetStack;
    public HashMap<Long, Stack<HashSet<Long>>> mapThreadWriteVarSetStack;
    public HashMap<Long, Stack<Long>> mapThreadLockStack;

    // Internal data
    private HashSet<Long> lockSet;
    private HashSet<Long> variableSet;

    private int numThreads;

    // Space saving
    private VectorClock local_vc_imax;

    private final Logger logger;

    public WCPState() {
        this.logger = Logger.getGlobal();
        initInternalData();
        initData();
        initOnlineData();
    }

    private void initInternalData() {
        this.numThreads = 0;
        this.lockSet = new HashSet<>();
        this.variableSet = new HashSet<>();
        local_vc_imax = new VectorClock();
    }

    public void initData() {
        this.clockThread = new HashMap<>();
        this.WCPThread = new HashMap<>();
        this.HBPredecessorThread = new HashMap<>();
        this.HBPredecessorLock = new HashMap<>();
        this.WCPPredecessorThread = new HashMap<>();
        this.WCPPredecessorLock = new HashMap<>();
        this.lastReleaseLockReadVariableThread = new HashMap<>();
        this.lastReleaseLockWriteVariableThread = new HashMap<>();
        this.readVariable = new HashMap<>();
        this.writeVariable = new HashMap<>();
        this.view = new WCPView();
    }

    public void initOnlineData() {
        mapThreadReadVarSetStack = new HashMap<>();
        mapThreadWriteVarSetStack = new HashMap<>();
        mapThreadLockStack = new HashMap<>();
    }

    /**
     * Initialize data for a newly seen thread.
     *
     * @param thread The identifier of the potentialy new thread to be initialized.
     */
    public void checkAndAddThread(long tid) {
        if (!clockThread.containsKey(tid)) {
            this.logger.report("New thread found " + this.numThreads, Logger.MSGTYPE.PROGRESS);
            this.numThreads++;
            clockThread.put(tid, 1L);
            WCPThread.put(tid, new VectorClock());
            HBPredecessorThread.put(tid, new VectorClock());
            WCPPredecessorThread.put(tid, new VectorClock());
            view.checkAndAddThread(tid);

            for (Long lock : this.lockSet) {
                for (Long var : this.variableSet) {
                    this.lastReleaseLockReadVariableThread
                            .get(lock)
                            .get(var)
                            .put(tid, new VectorClock());
                    this.lastReleaseLockWriteVariableThread
                            .get(lock)
                            .get(var)
                            .put(tid, new VectorClock());
                }
            }
        }
    }

    /**
     * Initialize data for a newly seen lock.
     *
     * @param lock The identifier of the potentialy new lock to be initialized.
     */
    public void checkAndAddLock(Long lock) {
        if (!lockSet.contains(lock)) {
            lockSet.add(lock);

            HBPredecessorLock.put(lock, new VectorClock());
            WCPPredecessorLock.put(lock, new VectorClock());

            this.lastReleaseLockReadVariableThread.put(
                    lock, new HashMap<Long, HashMap<Long, VectorClock>>());
            for (long var : variableSet) {
                this.lastReleaseLockReadVariableThread
                        .get(lock)
                        .put(var, new HashMap<Long, VectorClock>());
                for (Long tid : this.clockThread.keySet()) {
                    this.lastReleaseLockReadVariableThread
                            .get(lock)
                            .get(var)
                            .put(tid, new VectorClock());
                }
            }

            this.lastReleaseLockWriteVariableThread.put(
                    lock, new HashMap<Long, HashMap<Long, VectorClock>>());
            for (Long var : variableSet) {
                this.lastReleaseLockWriteVariableThread
                        .get(lock)
                        .put(var, new HashMap<Long, VectorClock>());
                for (Long tid : this.clockThread.keySet()) {
                    this.lastReleaseLockWriteVariableThread
                            .get(lock)
                            .get(var)
                            .put(tid, new VectorClock());
                }
            }

            view.checkAndAddLock(lock);
        }
    }

    /**
     * Initialize data for a newly seen variable.
     *
     * @param var The identifier of the potentialy new variable to be initialized.
     */
    public void checkAndAddVariable(Long var, boolean inCLInit) {
        if (!variableSet.contains(var)) {
            variableSet.add(var);

            for (Long lock : lockSet) {
                this.lastReleaseLockReadVariableThread
                        .get(lock)
                        .put(var, new HashMap<Long, VectorClock>());
                for (Long tid : this.clockThread.keySet()) {
                    this.lastReleaseLockReadVariableThread
                            .get(lock)
                            .get(var)
                            .put(tid, new VectorClock());
                }

                this.lastReleaseLockWriteVariableThread
                        .get(lock)
                        .put(var, new HashMap<Long, VectorClock>());
                for (Long tid : this.clockThread.keySet()) {
                    this.lastReleaseLockWriteVariableThread
                            .get(lock)
                            .get(var)
                            .put(tid, new VectorClock());
                }
            }
            readVariable.put(var, new VectorClock());
            writeVariable.put(var, new VectorClock());
        }
    }

    /**
     * Generates a Vector Clock for the specified thread 'tid' using the stored integer clock 'c_t'
     * and the predecessor clock 'P_t'. The Vector Clock C_t is not stored explicitly, instead it
     * is generated using c_t and P_t. This is an optimization to save space.
     *
     * @param tid The thread ID 'tid' for which the Vector Clock is generated.
     * @return A Vector Clock representing the current state of the specified thread 'tid'.
     */
    public VectorClock generateVectorClockFromClockThread(long tid) {
        VectorClock wcpClock = this.WCPThread.get(tid);
        VectorClock pred = this.WCPPredecessorThread.get(tid);
        long tValue = this.clockThread.get(tid);

        wcpClock.copyFrom(pred);
        wcpClock.put(Math.toIntExact(tid), Math.toIntExact(tValue));
        return wcpClock;
    }

    public void incrementClockThread(long tid) {
        this.clockThread.computeIfPresent(tid, (k, var) -> var + 1L);
    }

    public VectorClock getLastReleaseLockReadVarThreadVectorClock(long lock, long var, long tid) {
        if (!this.lastReleaseLockReadVariableThread.containsKey(lock)) {
            throw new IllegalArgumentException("No lock found");
        }
        if (!this.lastReleaseLockReadVariableThread.get(lock).containsKey(var)) {
            throw new IllegalArgumentException("No variable found");
        }
        if (!this.lastReleaseLockReadVariableThread
                .get(lock)
                .get(var)
                .containsKey(tid)) {
            throw new IllegalArgumentException("No tid found");
        }

        return this.lastReleaseLockReadVariableThread
                .get(lock)
                .get(var)
                .get(tid);
    }

    public VectorClock getLastReleaseLockWriteVarThreadVectorClock(long lock, long var, long tid) {
        if (!this.lastReleaseLockWriteVariableThread.containsKey(lock)) {
            throw new IllegalArgumentException("No lock found");
        }
        if (!this.lastReleaseLockWriteVariableThread.get(lock).containsKey(var)) {
            throw new IllegalArgumentException("No variable found");
        }
        if (!this.lastReleaseLockWriteVariableThread
                .get(lock)
                .get(var)
                .containsKey(tid)) {
            throw new IllegalArgumentException("No tid found");
        }

        return this.lastReleaseLockWriteVariableThread
                .get(lock)
                .get(var)
                .get(tid);
    }

    public void updateViewAsWriterAtAcquire(long lock, long tid) {
        VectorClock C_t = generateVectorClockFromClockThread(tid);
        view.pushClockPair(lock, new ClockPair(C_t));
    }

    public void readViewOfWriters(long lock, long tid) {
        local_vc_imax.setToZero();
        VectorClock P_t = this.WCPPredecessorThread.get(tid);
        VectorClock C_t = generateVectorClockFromClockThread(tid);
        view.getMaxLowerBound(tid, lock, C_t, local_vc_imax);
        P_t.update(local_vc_imax);
    }

    public void updateViewAsWriterAtRelease(long lock, long tid) {
        VectorClock C_t = generateVectorClockFromClockThread(tid);
        VectorClock H_t = this.HBPredecessorThread.get(tid);

        view.updateTopRelease(lock, C_t, H_t);
    }

    public void printThreadClock() {
        ArrayList<VectorClock> printVC = new ArrayList<VectorClock>();
        for (long thread : this.clockThread.keySet()) {
            VectorClock C_t = generateVectorClockFromClockThread(thread);
            printVC.add(C_t);
        }
        this.logger.report(printVC.toString(), Logger.MSGTYPE.REPORT);
        this.logger.report("\n%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%", Logger.MSGTYPE.REPORT);
    }

    public <T> HashSet<T> stackToSet(Stack<T> stack) {
        HashSet<T> set = new HashSet<T>();
        for (T tid : stack) {
            set.add(tid);
        }
        return set;
    }

    public HashSet<Long> getSetFromStack(long tid) {
        return stackToSet(this.mapThreadLockStack.get(tid));
    }

    public boolean isLockAcquired(long tid, long lock) {
        return this.getSetFromStack(tid).contains(lock);
    }

    public void printViewSize() {
        this.view.printSize();
    }

    public void printMemory() {
        this.logger.report("Number of threads = " + Integer.toString(this.numThreads), Logger.MSGTYPE.REPORT);
        this.logger.report("Number of locks = " + Integer.toString(this.lockSet.size()), Logger.MSGTYPE.REPORT);
        this.logger.report("Number of variables = " + Integer.toString(this.variableSet.size()), Logger.MSGTYPE.REPORT);
        this.view.printSize();
    }

    public void destroyLock(long lock) {
        if (!lockSet.contains(lock)) {
            throw new IllegalArgumentException(
                    "Cannot delete non-existent lock " + Long.toString(lock));
        } else {
            lockSet.remove(lock);

            HBPredecessorLock.remove(lock);
            WCPPredecessorLock.remove(lock);

            lastReleaseLockReadVariableThread.remove(lock);
            lastReleaseLockWriteVariableThread.remove(lock);

            view.destroyLock(lock);
        }
    }

    public void destroyLockThreadStack(long lock, long tid) {
        if (!lockSet.contains(lock)) {
            throw new IllegalArgumentException(
                    "Cannot delete stacks for non-existent lock " + Long.toString(lock));
        } else if (!clockThread.containsKey(tid)) {
            throw new IllegalArgumentException(
                    "Cannot delete stacks for non-existent thread " + Long.toString(tid));
        } else {
            view.destroyLockThreadStack(lock, tid);
        }
    }
    //CHECKSTYLE.ON: LocalVariableName
    //CHECKSTYLE.ON: MemberName

    @Override
    public void reset() {
        initInternalData();
        initData();
        initOnlineData();
    }

}
