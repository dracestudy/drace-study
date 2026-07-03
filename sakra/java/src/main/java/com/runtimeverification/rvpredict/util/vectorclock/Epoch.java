package com.runtimeverification.rvpredict.util.vectorclock;

import com.runtimeverification.rvpredict.order.VectorClock;

public class Epoch {

    private int clock;
    private int threadIdx;

    public Epoch() {
        this.clock = 0;
        this.threadIdx = 0;
    }

    public Epoch(int clock, int tid) {
        this.clock = clock;
        this.threadIdx = tid;
    }

    public Epoch(Epoch fromEpoch) {
        this.clock = fromEpoch.clock;
        this.threadIdx = fromEpoch.threadIdx;
    }

    public int getClock() {
        return this.clock;
    }

    public int getThreadIndex() {
        return this.threadIdx;
    }

    public void setClock(int clock) {
        this.clock = clock;
    }

    public void setThreadIndex(int tid) {
        this.threadIdx = tid;
    }

    public String toString() {
        return Integer.toString(this.clock) + "@" + Integer.toString(this.threadIdx);
    }

    public boolean isZero() {
        return this.clock == 0;
    }

    public boolean isEqual(Epoch epoch) {
        return (this.clock == epoch.clock) && (this.threadIdx == epoch.threadIdx);
    }

    public boolean isLessThanOrEqual(VectorClock vc) {
        return this.clock <= vc.get(this.threadIdx);
    }
}
