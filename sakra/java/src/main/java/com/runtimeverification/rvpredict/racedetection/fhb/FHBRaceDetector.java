package com.runtimeverification.rvpredict.racedetection.fhb;

import com.runtimeverification.rvpredict.config.Configuration;
import com.runtimeverification.rvpredict.metadata.MetadataInterface;
import com.runtimeverification.rvpredict.racedetection.engine.RaceDetectionEngine;

/**
 * Forced Happens Before (FHB) Race detector. Every time an HB-race is discovered,
 * the algorithm force orders the events in race, before analyzing subsequent
 * events in the trace. This algorithm is sound, but but it may fail to identify
 * some races that are schedulable.
 */
public class FHBRaceDetector extends RaceDetectionEngine<FHBState, FHBEvent> {
    public FHBRaceDetector(Configuration config, MetadataInterface metadata) {
        super(config, metadata, new FHBState(), new FHBEvent());
    }
}
