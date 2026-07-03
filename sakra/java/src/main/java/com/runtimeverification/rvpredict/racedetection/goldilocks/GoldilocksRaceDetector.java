package com.runtimeverification.rvpredict.racedetection.goldilocks;

import com.runtimeverification.rvpredict.config.Configuration;
import com.runtimeverification.rvpredict.metadata.MetadataInterface;
import com.runtimeverification.rvpredict.racedetection.engine.RaceDetectionEngine;

/**
 * Goldilocks Race detector. A variant of the lockset race detection algorithm that adds to it two
 * key capabilities: explicitly handling software transactions as a high-level synchronization idiom,
 * and distinguishing between read and write accesses.
 * https://dl.acm.org/doi/10.1145/1273442.1250762
 */
public class GoldilocksRaceDetector extends RaceDetectionEngine<GoldilocksState, GoldilocksEvent> {
    public GoldilocksRaceDetector(Configuration config, MetadataInterface metadata) {
        super(config, metadata, new GoldilocksState(), new GoldilocksEvent());
    }
}
