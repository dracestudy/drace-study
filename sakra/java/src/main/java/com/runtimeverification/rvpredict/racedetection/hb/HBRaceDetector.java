package com.runtimeverification.rvpredict.racedetection.hb;

import com.runtimeverification.rvpredict.config.Configuration;
import com.runtimeverification.rvpredict.metadata.MetadataInterface;
import com.runtimeverification.rvpredict.racedetection.engine.RaceDetectionEngine;

/**
 * Happens Before Race detector. The first race reported is guaranteed to be a
 * true positive, however the other reported races may be false positives.
 */
public class HBRaceDetector extends RaceDetectionEngine<HBState, HBEvent> {
    public HBRaceDetector(Configuration config, MetadataInterface metadata) {
        super(config, metadata, new HBState(), new HBEvent());
    }
}
