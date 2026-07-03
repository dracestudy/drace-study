package com.runtimeverification.rvpredict.racedetection.event;

/** This class represents a lock event. */
public class Lock extends Decoration {

    public static int lockCountTracker = 0;

    /** Construct a lock event with default name. */
    public Lock() {
        this.id = lockCountTracker;
        lockCountTracker++;
        this.name = "__lock::" + Integer.toString(this.id) + "__";
    }

    /** Construct a lock event with given name. */
    public Lock(String sname) {
        this.id = lockCountTracker;
        lockCountTracker++;
        this.name = sname;
    }
}
