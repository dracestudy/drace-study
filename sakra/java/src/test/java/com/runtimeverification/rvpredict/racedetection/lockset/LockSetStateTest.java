package com.runtimeverification.rvpredict.racedetection.lockset;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import com.runtimeverification.rvpredict.config.Configuration;
import com.runtimeverification.rvpredict.metadata.Metadata;
import com.runtimeverification.rvpredict.racedetection.Traces;
import com.runtimeverification.rvpredict.racedetection.event.RapidEvent;
import com.runtimeverification.rvpredict.racedetection.event.RapidEventType;

import org.junit.Before;
import org.junit.Test;

public class LockSetStateTest {

    protected static LockSetRaceDetector detector;
    protected static Traces traces;
    protected static ArrayList<RapidEvent> trace;

    @Before
    public void setUp() {
        Configuration config = Configuration.instance(new String[0]);
        Metadata metadata = Metadata.singleton();
        detector = new LockSetRaceDetector(config, metadata);
        trace = new ArrayList<>();
        trace.add(new RapidEvent(1L, 642, RapidEventType.ACQUIRE, 1, 0L, 12L, 0L, false, false));
        trace.add(new RapidEvent(2L, 643, RapidEventType.WRITE, 1, 1L, 13L, 1L, false, false));
        trace.add(new RapidEvent(3L, 644, RapidEventType.RELEASE, 1, 0L, 13L, 1L, false, false));
        trace.add(new RapidEvent(4L, 645, RapidEventType.ACQUIRE, 1, 0L, 12L, 0L, false, false));
        trace.add(new RapidEvent(5L, 645, RapidEventType.ACQUIRE, 1, 0L, 12L, 0L, false, false));
        trace.add(new RapidEvent(6L, 646, RapidEventType.WRITE, 1, 1L, 13L, 1L, false, false));
        trace.add(new RapidEvent(7L, 647, RapidEventType.RELEASE, 1, 0L, 13L, 1L, false, false));
        trace.add(new RapidEvent(8L, 648, RapidEventType.RELEASE, 1, 4L, 12L, 0L, false, false));
    }

    @Test
    public void testLockSetValidLock0() {
        for (int i = 0; i < 3; i++) {
            detector.analyzeEvent(trace.get(i));
        }

        int expectedValue = 0;
        int actualValue = detector.state.locksHeldNesting.get(1L).get(0L);
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void testLockSetValidLock2() {
        for (int i = 0; i < 6; i++) {
            detector.analyzeEvent(trace.get(i));
        }

        int expectedValue = 2;
        int actualValue = detector.state.locksHeldNesting.get(1L).get(0L);
        assertEquals(expectedValue, actualValue);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRapidRaceProcess1() {
        for (int i = 0; i < 8; i++) {
            detector.analyzeEvent(trace.get(i));
        }
    }
}
