package com.runtimeverification.rvpredict.racedetection.shbepoch;

import com.runtimeverification.rvpredict.config.Configuration;
import com.runtimeverification.rvpredict.metadata.MetadataInterface;
import com.runtimeverification.rvpredict.racedetection.engine.RaceDetectionEngine;

/**
 * Schedulable Happens Before Race detector with Epoch Optimization.
 * A linear time, sound vector clock algorithm to detect schedulable races.
 * https://dl.acm.org/doi/10.1145/3276515
 */
public class SHBEpochRaceDetector extends RaceDetectionEngine<SHBEpochState, SHBEpochEvent> {
    public SHBEpochRaceDetector(Configuration config, MetadataInterface metadata) {
        super(config, metadata, new SHBEpochState(), new SHBEpochEvent());
    }
}
