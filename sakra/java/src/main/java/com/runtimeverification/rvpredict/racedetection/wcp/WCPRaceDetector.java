package com.runtimeverification.rvpredict.racedetection.wcp;

import com.runtimeverification.rvpredict.config.Configuration;
import com.runtimeverification.rvpredict.metadata.MetadataInterface;
import com.runtimeverification.rvpredict.racedetection.engine.RaceDetectionEngine;

/**
 * WCP Race detector.
 * https://dl.acm.org/doi/10.1145/3062341.3062374
 */
public class WCPRaceDetector extends RaceDetectionEngine<WCPState, WCPEvent> {
    public WCPRaceDetector(Configuration config, MetadataInterface metadata) {
        super(config, metadata, new WCPState(), new WCPEvent());
    }
}
