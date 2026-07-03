package com.runtimeverification.rvpredict.racedetection.hbepoch;

import com.runtimeverification.rvpredict.config.Configuration;
import com.runtimeverification.rvpredict.metadata.MetadataInterface;
import com.runtimeverification.rvpredict.racedetection.engine.RaceDetectionEngine;

/**
 * Happens Before Race detector with Epoch Optimization. The first race reported is
 * guaranteed to be a true positive, however the other reported races may be false positives.
 * https://dl.acm.org/doi/10.1145/3276515
 */
public class HBEpochRaceDetector extends RaceDetectionEngine<HBEpochState, HBEpochEvent> {
    public HBEpochRaceDetector(Configuration config, MetadataInterface metadata) {
        super(config, metadata, new HBEpochState(), new HBEpochEvent());
    }
}
