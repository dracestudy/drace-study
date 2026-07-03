package com.runtimeverification.rvpredict.racedetection.shb;

import com.runtimeverification.rvpredict.config.Configuration;
import com.runtimeverification.rvpredict.metadata.MetadataInterface;
import com.runtimeverification.rvpredict.racedetection.engine.RaceDetectionEngine;

/**
 * SHB Race detector. A linear time, sound vector clock algorithm to detect schedulable races.
 * https://dl.acm.org/doi/10.1145/3276515
 */
public class SHBRaceDetector extends RaceDetectionEngine<SHBState, SHBEvent> {
    public SHBRaceDetector(Configuration config, MetadataInterface metadata) {
        super(config, metadata, new SHBState(), new SHBEvent());
    }
}
