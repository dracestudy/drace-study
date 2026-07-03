/*******************************************************************************
 * Copyright (c) 2013 University of Illinois
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/

package com.runtimeverification.rvpredict.log;

import java.io.IOException;

import com.runtimeverification.rvpredict.config.Configuration;
import com.runtimeverification.rvpredict.metadata.Metadata;
import com.runtimeverification.rvpredict.racedetection.engine.RaceDetectionEngine;
import com.runtimeverification.rvpredict.racedetection.event.RapidEvent;
import com.runtimeverification.rvpredict.racedetection.event.RapidEventType;
import com.runtimeverification.rvpredict.racedetection.fhb.FHBRaceDetector;
import com.runtimeverification.rvpredict.racedetection.goldilocks.GoldilocksRaceDetector;
import com.runtimeverification.rvpredict.racedetection.hb.HBRaceDetector;
import com.runtimeverification.rvpredict.racedetection.hbepoch.HBEpochRaceDetector;
import com.runtimeverification.rvpredict.racedetection.lockset.LockSetRaceDetector;
import com.runtimeverification.rvpredict.racedetection.shb.SHBRaceDetector;
import com.runtimeverification.rvpredict.racedetection.shbepoch.SHBEpochRaceDetector;
import com.runtimeverification.rvpredict.racedetection.syncpreserving.SyncPreservingRaceDetector;
import com.runtimeverification.rvpredict.racedetection.wcp.WCPRaceDetector;

/** Logging engine that processes events one by one. */
public class OnlineLoggingEngine extends LoggingEngine {
    private final RaceDetectionEngine<?, ?> detector;
    private final RapidEvent event;

    public OnlineLoggingEngine(Configuration config, Metadata metadata) {
        super(config, metadata);
        if (config.isSHB()) {
            this.detector = new SHBRaceDetector(config, metadata);
        } else if (config.isHappensBefore()) {
            this.detector = new HBRaceDetector(config, metadata);
        } else if (config.isFHB()) {
            this.detector = new FHBRaceDetector(config, metadata);
        } else if (config.isGoldilocks()) {
            this.detector = new GoldilocksRaceDetector(config, metadata);
        } else if (config.isLockset()) {
            this.detector = new LockSetRaceDetector(config, metadata);
        } else if (config.isSyncPreserving()) {
            this.detector = new SyncPreservingRaceDetector(config, metadata);
        } else if (config.isSHBEpoch()) {
            this.detector = new SHBEpochRaceDetector(config, metadata);
        } else if (config.isHBEpoch()) {
            this.detector = new HBEpochRaceDetector(config, metadata);
        } else {
            this.detector = new WCPRaceDetector(config, metadata);
        }
        this.event = new RapidEvent();
    }

    @Override
    public synchronized void finishLogging() throws IOException {
        this.detector.printRaceReport();
        try {
            this.detector.close();
        } catch (Exception err) {
            throw new RuntimeException(err);
        }
    }

    @Override
    public synchronized void resetAnalysis() {
        this.detector.reset();
    }

    @Override
    protected synchronized void log(
            EventType eventType, long gid, long tid, int locId, int addr1, int addr2, long value) {

        long address = ((long) addr1 << 32) | (addr2 & 0xFFFFFFFFL);

        RapidEventType rapidEventType = eventType.getRapidEventType();

        StackTraceElement[] elements = null;
        boolean isVolatile = false;
        if (rapidEventType.isAccessType()) {
            elements = Thread.currentThread().getStackTrace();
            if (metadata != null) {
                isVolatile = metadata.isVolatile(address);
            }
        }

        boolean toIgnore = detector.updateCLInit(tid, eventType);
        if (!toIgnore) {
            toIgnore = RaceDetectionEngine.isThreadSafeLocation(metadata, locId);
        }

        this.event.updateEvent(gid, locId, rapidEventType, tid, address, address, address, elements, isVolatile, toIgnore);
        this.detector.analyzeEvent(this.event);
    }

    RapidEvent getEvent() {
        return this.event;
    }
}
