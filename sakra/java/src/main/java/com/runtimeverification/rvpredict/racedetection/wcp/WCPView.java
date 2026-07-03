package com.runtimeverification.rvpredict.racedetection.wcp;

import java.util.HashMap;
import java.util.HashSet;

import com.runtimeverification.rvpredict.util.Logger;
import com.runtimeverification.rvpredict.util.ll.EfficientLinkedList;
import com.runtimeverification.rvpredict.util.ll.EfficientNode;
import com.runtimeverification.rvpredict.util.vectorclock.ClockPair;
import com.runtimeverification.rvpredict.util.vectorclock.VectorClock;

public class WCPView {

    // LOCK -> Store
    private HashMap<Long, EfficientLinkedList<ClockPair>> view;
    // LOCK -> (READER -> pointer_in_stack)
    private HashMap<Long, HashMap<Long, EfficientNode<ClockPair>>> stackBottomPointerForReader;
    // LOCK -> (READER -> 0_based_index__of_reader's_view_from_bottom_of_the_actual_stack)
    private HashMap<Long, HashMap<Long, Integer>> stackBottomPointerIndexForReader;
    // LOCK -> (READER -> Boolean)
    private HashMap<Long, HashMap<Long, Boolean>> stackEmptyForReader;
    private HashSet<Long> threadSet;
    private HashSet<Long> lockSet;
    private final Logger logger;

    WCPView() {
        this.logger = Logger.getGlobal();
        this.threadSet = new HashSet<Long>();
        this.lockSet = new HashSet<Long>();
        this.view = new HashMap<Long, EfficientLinkedList<ClockPair>>();
        this.stackBottomPointerForReader =
                new HashMap<Long, HashMap<Long, EfficientNode<ClockPair>>>();
        this.stackBottomPointerIndexForReader = new HashMap<Long, HashMap<Long, Integer>>();
        this.stackEmptyForReader = new HashMap<Long, HashMap<Long, Boolean>>();
    }

    public void checkAndAddThread(long tid) {
        if (!threadSet.contains(tid)) {
            threadSet.add(tid);
            for (long lock : this.lockSet) {
                stackBottomPointerForReader.get(lock).put(tid, null);
                stackBottomPointerIndexForReader.get(lock).put(tid, -1);
                stackEmptyForReader.get(lock).put(tid, (Boolean) true);
            }
        }
    }

    public void checkAndAddLock(long lock) {
        if (!lockSet.contains(lock)) {
            lockSet.add(lock);
            view.put(lock, new EfficientLinkedList<ClockPair>());
            stackBottomPointerForReader.put(lock, new HashMap<Long, EfficientNode<ClockPair>>());
            stackBottomPointerIndexForReader.put(lock, new HashMap<Long, Integer>());
            stackEmptyForReader.put(lock, new HashMap<Long, Boolean>());

            for (long thReader : this.threadSet) {
                stackBottomPointerForReader.get(lock).put(thReader, null);
                stackBottomPointerIndexForReader.get(lock).put(thReader, -1);
                stackEmptyForReader.get(lock).put(thReader, (Boolean) true);
            }
        }
    }

    public void pushClockPair(long lock, ClockPair clockPair) {
        this.view.get(lock).pushTop(clockPair);
        for (long thReader : this.threadSet) {
            if (this.stackEmptyForReader.get(lock).get(thReader)) {
                this.stackEmptyForReader.get(lock).put(thReader, (Boolean) false);
                this.stackBottomPointerForReader.get(lock).put(thReader, view.get(lock).getHeadNode());
                this.stackBottomPointerIndexForReader.get(lock).put(thReader, 0);
            }
        }
    }

    /**
     * Gets the minimum index of readers with non-empty stacks for a given lock.
     * The method checks each reader's stack associated with the specified lock and
     * determines the minimum bottom pointer index among non-empty stacks. If all
     * readers have empty stacks, the method returns -1.
     *
     * <p><strong>Precondition:</strong> The stack at (lock, tWriter) is non-empty.
     *
     * @param lock The identifier of the lock for which the minimum index is determined.
     * @return The minimum index of readers' stacks for the specified lock, or -1 if all
     *         readers have empty stacks.
     */
    private int getMinIndexOfReaders(long lock) {
        int minIndex = -1;
        boolean atLeastOne = false;
        for (long thReader : this.threadSet) {
            if (!this.stackEmptyForReader.get(lock).get(thReader)) {
                int bottomReaderIndex = this.stackBottomPointerIndexForReader.get(lock).get(thReader);
                if (!atLeastOne) {
                    atLeastOne = true;
                    minIndex = bottomReaderIndex;
                } else {
                    if (minIndex > bottomReaderIndex) {
                        minIndex = bottomReaderIndex;
                    }
                }
            }
        }
        return minIndex;
    }

    private void removeViewPrefixOfLength(long lock, int prefixLength) {
        if (this.view.get(lock).getLength() < prefixLength) {
            throw new IllegalArgumentException(
                    "Invalid operation removeViewPrefixOfLength : Size of stack at ("
                            + Long.toString(lock)
                            + ") is "
                            + this.view.get(lock).getLength()
                            + ", asked to remove : "
                            + prefixLength);
        }
        this.view.get(lock).removeBottomPrefixOfLength(prefixLength);
        for (long thReader : this.threadSet) {
            if (!this.stackEmptyForReader.get(lock).get(thReader)) {
                int minPtr = this.stackBottomPointerIndexForReader.get(lock).get(thReader);
                minPtr = minPtr - prefixLength;
                if (minPtr >= 0) {
                    this.stackBottomPointerIndexForReader.get(lock).put(thReader, minPtr);
                } else {
                    this.stackEmptyForReader.get(lock).put(thReader, true);
                    this.stackBottomPointerForReader.get(lock).put(thReader, null);
                    this.stackBottomPointerIndexForReader.get(lock).put(thReader, -1);
                }
            }
        }
    }

    private void updateStoreToMatchBottomWithMin(long lock) {
        EfficientLinkedList<ClockPair> st = this.view.get(lock);
        int sz = st.getLength();
        if (sz > 0) {
            int minIndex = this.getMinIndexOfReaders(lock);
            if (minIndex >= 0) {
                removeViewPrefixOfLength(lock, minIndex);
            } else {
                removeViewPrefixOfLength(lock, st.getLength());
            }
        }
    }

    // result is going to be overwritten with the release of the largest acq <= ct
    public void getMaxLowerBound(long thReader, long lock, VectorClock ct, VectorClock result) {
        result.setToZero();

        ClockPair clockPair = null;
        boolean pairFound = false;
        EfficientNode<ClockPair> lockIter = null;
        int lockIterIndex = -1;

        if (this.stackEmptyForReader.get(lock).get(thReader)) {
            lockIter = stackBottomPointerForReader.get(lock).get(thReader);
            lockIterIndex = stackBottomPointerIndexForReader.get(lock).get(thReader);
            int totalSize = this.view.get(lock).getLength();
            while (lockIter != null && lockIterIndex < totalSize - 1) {
                clockPair = lockIter.getData();
                VectorClock acquireClock = clockPair.getAcquire();
                if (acquireClock.isLessThanOrEqual(ct)) {
                    pairFound = true;
                } else {
                    break;
                }
                result.update(clockPair.getRelease());
                lockIter = lockIter.getNext();
                lockIterIndex = lockIterIndex + 1;
            }
        }

        if (pairFound) {
            if (lockIter != null) {
                this.stackBottomPointerForReader.get(lock).put(thReader, lockIter);
                this.stackBottomPointerIndexForReader.get(lock).put(thReader, lockIterIndex);
            } else {
                this.stackEmptyForReader.get(lock).put(thReader, true);
                this.stackBottomPointerForReader.get(lock).put(thReader, null);
                this.stackBottomPointerIndexForReader.get(lock).put(thReader, -1);
            }
            this.updateStoreToMatchBottomWithMin(lock);
        }
    }

    public void updateTopRelease(long lock, VectorClock ct, VectorClock ht) {
        ClockPair clockPair = this.view.get(lock).top();
        VectorClock relaseVC = clockPair.getRelease();
        relaseVC.copyFrom(ct);
        relaseVC.update(ht);
    }

    public String toString() {
        StringBuilder output = new StringBuilder();
        for (long lock : lockSet) {
            output.append("[" + Long.toString(lock) + "]");
            output.append(" : " + view.get(lock).toString() + "\n");
        }
        output.append("\n");
        return output.toString();
    }

    public int getSize() {
        int sz = 0;
        for (long lock : lockSet) {
            sz += view.get(lock).getLength();
        }
        return sz;
    }

    public void printSize() {
        this.logger.report("Stack size = " + Integer.toString(this.getSize()), Logger.MSGTYPE.INFO);
    }

    public void destroyLock(long lock) {
        if (!lockSet.contains(lock)) {
            throw new IllegalArgumentException(
                    "Cannot delete non-existent lock " + Long.toString(lock));
        } else {
            lockSet.remove(lock);
            view.remove(lock);
            this.stackBottomPointerForReader.remove(lock);
            this.stackBottomPointerIndexForReader.remove(lock);
            this.stackEmptyForReader.remove(lock);
        }
    }

    public void destroyLockThreadStack(long lock, long tid) {
        if (!lockSet.contains(lock)) {
            throw new IllegalArgumentException(
                    "Cannot delete stack for non-existent lock " + Long.toString(lock));
        } else if (!threadSet.contains(tid)) {
            throw new IllegalArgumentException(
                    "Cannot delete stack for non-existent thread " + Long.toString(tid));
        } else {
            this.stackBottomPointerForReader.get(lock).remove(tid);
            this.stackEmptyForReader.get(lock).remove(tid);
            this.stackBottomPointerIndexForReader.get(lock).remove(tid);
            updateStoreToMatchBottomWithMin(lock);
        }
    }
}
