package com.runtimeverification.rvpredict.racedetection.goldilocks;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import com.runtimeverification.rvpredict.config.Configuration;
import com.runtimeverification.rvpredict.metadata.Metadata;
import com.runtimeverification.rvpredict.racedetection.Traces;
import com.runtimeverification.rvpredict.racedetection.event.RapidEvent;
import com.runtimeverification.rvpredict.racedetection.event.RapidEventType;

import org.junit.Before;
import org.junit.Test;

public class GoldilocksStateTest {

    protected static GoldilocksRaceDetector detector;
    protected static Traces traces;
    protected static ArrayList<RapidEvent> trace;

    @Before
    public void setUp() {
        Configuration config = Configuration.instance(new String[0]);
        Metadata metadata = Metadata.singleton();
        detector = new GoldilocksRaceDetector(config, metadata);
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
    public void testGoldilocksValidWrite1() {
        for (int i = 0; i < 2; i++) {
            detector.analyzeEvent(trace.get(i));
        }

        boolean expectedValue = true;
        boolean actualValue = detector.state.writeLockSet.get(13L).contains(1L);
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void testGoldilocksValidWrite2() {
        for (int i = 0; i < 8; i++) {
            detector.analyzeEvent(trace.get(i));
        }

        boolean expectedValue = true;
        boolean actualValue = detector.state.writeLockSet.get(13L).contains(0L);
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void testGoldilocksValidRead() {
        for (int i = 0; i < 9; i++) {
            detector.analyzeEvent(trace.get(i));
        }

        boolean expectedValue = true;
        boolean actualValue = detector.state.readLockSet.get(1L).containsKey(13L);
        assertEquals(expectedValue, actualValue);
    }

    @Test(expected = NullPointerException.class)
    public void testGoldilocksInvalidReadLockSet() {
        for (int i = 0; i < 10; i++) {
            detector.analyzeEvent(trace.get(i));
        }

        detector.state.readLockSet.get(13L).containsKey(1L);
    }
}
