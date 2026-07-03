package com.runtimeverification.rvpredict.racedetection.lockset;

import com.runtimeverification.rvpredict.config.Configuration;
import com.runtimeverification.rvpredict.metadata.MetadataInterface;
import com.runtimeverification.rvpredict.racedetection.engine.RaceDetectionEngine;

/**
 * Lockset Race detector. Lockset simply checks that all shared-memory accesses
 * follow a consistent locking discipline. A locking discipline is a programming
 * policy that ensures the absence of data races. For example, a simple locking
 * discipline is to require that every variable shared between threads is protected
 * by a mutual exclusion lock.
 * https://dl.acm.org/doi/10.1145/265924.265927
 */
public class LockSetRaceDetector extends RaceDetectionEngine<LockSetState, LockSetEvent> {
    public LockSetRaceDetector(Configuration config, MetadataInterface metadata) {
        super(config, metadata, new LockSetState(), new LockSetEvent());
    }
}
