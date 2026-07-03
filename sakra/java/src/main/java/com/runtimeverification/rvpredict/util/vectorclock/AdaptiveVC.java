package com.runtimeverification.rvpredict.util.vectorclock;

public abstract class AdaptiveVC {

    protected boolean isEpoch;
    protected Epoch epoch;
    protected VectorClock vc;

    public AdaptiveVC() {
        this.isEpoch = true;
        this.epoch = new Epoch();
        this.vc = null;
    }

    public Epoch getEpoch() {
        return this.epoch;
    }

    public VectorClock getVC() {
        return this.vc;
    }

    public boolean isEpoch() {
        return isEpoch;
    }

    public String toString() {
        if (isEpoch) {
            return this.epoch.toString();
        } else {
            return this.vc.toString();
        }
    }

    public boolean isLessThanOrEqual(VectorClock vc) {
        if (isEpoch) {
            return this.epoch.getClock() <= vc.get(this.epoch.getThreadIndex());
        } else {
            return this.vc.isLessThanOrEqual(vc);
        }
    }

    /**
     * Checks if the current state is epoch and if the current clock and thread index match
     * the specified clock and thread index.
     *
     * <p>The method returns true if the current state is epoch and if the
     * current clock is equal to the specified clock and the current thread index is equal to the specified
     * thread index. If isEpoch is false, indicating that the current state is not part of an epoch, the
     * method returns false.
     *
     * @param clock The clock value to compare with the current clock.
     * @param tid The thread index to compare with the current thread index.
     * @return {@code true} if the current state is epoch and the current clock and thread index
     *         match the specified values; {@code false} otherwise.
     * @see Epoch
     */
    public boolean isSameEpoch(int clock, int tid) {
        if (isEpoch) {
            return this.epoch.getClock() == clock && this.epoch.getThreadIndex() == tid;
        } else {
            return false;
        }
    }

    public int getClockIndex(int thIndex) {
        if (this.isEpoch) {
            if (this.epoch.getThreadIndex() == thIndex) {
                return this.epoch.getClock();
            }
            return 0;
        }
        return this.getVC().get(thIndex);
    }

    /**
     * Checks if the current vector clock is less than or equal to the specified vector clock (vc).
     * If the check is true, the method returns true. Otherwise, the vector clock is updated by taking
     * the component-wise maximum with the specified vector clock, and the method returns false.
     *
     * <p>The method compares each component of the current vector clock with the corresponding component
     * of the specified vector clock. If all components of the current vector clock are less than or equal
     * to the corresponding components of the specified vector clock, the method returns true. Otherwise,
     * the method updates the current vector clock by taking the component-wise maximum with the specified
     * vector clock and returns false.
     *
     * @param vc The vector clock to compare with the current vector clock.
     * @param tid The thread identifier associated with the vector clock.
     * @return {@code true} if the current vector clock is less than or equal to the specified vector clock;
     *         {@code false} otherwise.
     * @see VectorClock
     */
    public abstract boolean isLTEUpdateWithMax(VectorClock vc, int tid);

    public void setEpoch(int clock, int tid) {
        if (!this.isEpoch) {
            throw new IllegalArgumentException(
                    "setEpoch can only be invoked when the clock is an epoch");
        }
        this.epoch.setClock(clock);
        this.epoch.setThreadIndex(tid);
    }

    public void setClockIndex(int thIndex, int thValue) {
        if (this.isEpoch) {
            throw new IllegalArgumentException(
                    "setClockIndex can only be invoked when the clock is a VC, not an epoch");
        }
        this.vc.put(thIndex, thValue);
    }

    public void forceBottomEpoch() {
        this.isEpoch = true;
        this.epoch.setClock(0);
        this.epoch.setThreadIndex(0);
    }
}
