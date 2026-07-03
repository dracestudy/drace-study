package com.runtimeverification.rvpredict.racedetection.syncpreserving;

import com.runtimeverification.rvpredict.config.Configuration;
import com.runtimeverification.rvpredict.metadata.MetadataInterface;
import com.runtimeverification.rvpredict.racedetection.engine.RaceDetectionEngine;

/**
 * Sync-Preserving Race detector. A sound and complete algorithm for detecting
 * sync-preserving data races.
 * https://dl.acm.org/doi/10.1145/3434317
 */
public class SyncPreservingRaceDetector extends RaceDetectionEngine<SyncPreservingRaceState, SyncPreservingRaceEvent> {
    public SyncPreservingRaceDetector(Configuration config, MetadataInterface metadata) {
        super(config, metadata, new SyncPreservingRaceState(), new SyncPreservingRaceEvent());
    }
}
