package com.runtimeverification.rvpredict.racedetection.shbepoch;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import com.runtimeverification.rvpredict.config.Configuration;
import com.runtimeverification.rvpredict.metadata.Metadata;
import com.runtimeverification.rvpredict.racedetection.Traces;
import com.runtimeverification.rvpredict.racedetection.event.RapidEvent;
import com.runtimeverification.rvpredict.racedetection.event.RapidEventType;
import com.runtimeverification.rvpredict.util.vectorclock.Epoch;

import org.junit.Before;
import org.junit.Test;

public class SHBEpochStateTest {

    protected static SHBEpochRaceDetector detector;
    protected static Traces traces;
    protected static ArrayList<RapidEvent> trace;

    @Before
    public void setUp() {
        Configuration config = Configuration.instance(new String[0]);
        Metadata metadata = Metadata.singleton();
        detector = new SHBEpochRaceDetector(config, metadata);
        trace = new ArrayList<>();
        trace.add(new RapidEvent(1L, 642, RapidEventType.ACQUIRE, 1, 0L, 12L, 0L, false, false));
        trace.add(new RapidEvent(2L, 643, RapidEventType.WRITE, 1, 1L, 13L, 1L, false, false));
        trace.add(new RapidEvent(3L, 644, RapidEventType.RELEASE, 1, 0L, 13L, 1L, false, false));
        trace.add(new RapidEvent(4L, 645, RapidEventType.ACQUIRE, 1, 0L, 12L, 0L, false, false));
        trace.add(new RapidEvent(5L, 646, RapidEventType.READ, 1, 1L, 13L, 1L, false, false));
        trace.add(new RapidEvent(6L, 647, RapidEventType.WRITE, 1, 1L, 13L, 1L, false, false));
        trace.add(new RapidEvent(7L, 648, RapidEventType.RELEASE, 1, 0L, 13L, 1L, false, false));
        trace.add(new RapidEvent(8L, 649, RapidEventType.READ, 1, 0L, 13L, 1L, false, false));
        trace.add(new RapidEvent(9L, 650, RapidEventType.FORK, 1, 0L, 0L, 2L, false, false));
        trace.add(new RapidEvent(10L, 651, RapidEventType.READ, 2, 0L, 1L, 2L, false, false));
    }

    @Test
    public void testSHBEpochLock1() {
        for (int i = 0; i < 2; i++) {
            detector.analyzeEvent(trace.get(i));
        }

        int expectedValue = 0;
        int actualValue = detector.state.getLockVectorClock(0L).get(1);
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void testSHBEpochLock2() {
        for (int i = 0; i < 4; i++) {
            detector.analyzeEvent(trace.get(i));
        }

        int expectedValue = 2;
        int actualValue = detector.state.getLockVectorClock(0L).get(1);
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void testSHBEpochLock3() {
        for (int i = 0; i < 7; i++) {
            detector.analyzeEvent(trace.get(i));
        }

        int expectedValue = 4;
        int actualValue = detector.state.getLockVectorClock(0L).get(1);
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void testSHBEpochLWLocId1() {
        for (int i = 0; i < 5; i++) {
            detector.analyzeEvent(trace.get(i));
        }

        int expectedValue = 643;
        int actualValue = detector.state.getLWLocId(13L);
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void testSHBEpochLWLocId2() {
        for (int i = 0; i < 10; i++) {
            detector.analyzeEvent(trace.get(i));
        }

        int expectedValue = 647;
        int actualValue = detector.state.getLWLocId(13L);
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void testHBValidRead1() {
        for (int i = 0; i < 10; i++) {
            detector.analyzeEvent(trace.get(i));
        }

        int expectedValue = 2;
        int actualValue = detector.state.getReadVectorClock(1L).getEpoch().getThreadIndex();
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void testHBEpochEpochTest1() {
        for (int i = 0; i < 10; i++) {
            detector.analyzeEvent(trace.get(i));
        }

        boolean expectedValue = false;
        Epoch epoch1 = detector.state.getReadVectorClock(13L).getEpoch();
        Epoch epoch2 = detector.state.getReadVectorClock(1L).getEpoch();
        boolean isSameEpoch = epoch1.isEqual(epoch2);
        assertEquals(expectedValue, isSameEpoch);
    }

    @Test
    public void testHBEpochEpochTest2() {
        for (int i = 0; i < 10; i++) {
            detector.analyzeEvent(trace.get(i));
        }

        boolean expectedValue = true;
        Epoch epoch1 = detector.state.getReadVectorClock(13L).getEpoch();
        boolean isSameEpoch = epoch1.isEqual(epoch1);
        assertEquals(expectedValue, isSameEpoch);
    }
}
