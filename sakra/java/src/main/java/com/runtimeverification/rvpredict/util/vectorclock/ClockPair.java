package com.runtimeverification.rvpredict.util.vectorclock;

public class ClockPair {

    private VectorClock acquireClock;
    private VectorClock releaseClock;

    ClockPair() {
        this.acquireClock = new VectorClock();
        this.releaseClock = new VectorClock();
    }

    public ClockPair(VectorClock acquire) {
        this.acquireClock = new VectorClock(acquire);
        this.releaseClock = new VectorClock();
    }

    ClockPair(VectorClock acquire, VectorClock release) {
        this.acquireClock = new VectorClock(acquire);
        this.releaseClock = new VectorClock(release);
    }

    public VectorClock getAcquire() {
        return this.acquireClock;
    }

    public VectorClock getRelease() {
        return this.releaseClock;
    }

    public void setAcquire(VectorClock acquire) {
        this.acquireClock.copyFrom(acquire);
    }

    public void setRelease(VectorClock release) {
        this.releaseClock.copyFrom(release);
    }

    public String toString() {
        String str =
                "(" + this.acquireClock.toString() + " , " + this.releaseClock.toString() + ")";
        return str;
    }
}
