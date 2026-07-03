package com.runtimeverification.rvpredict.racedetection.hbepoch;

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

public class HBEpochStateTest {

    protected static HBEpochRaceDetector detector;
    protected static Traces traces;
    protected static ArrayList<RapidEvent> trace;

    @Before
    public void setUp() {
        Configuration config = Configuration.instance(new String[0]);
        Metadata metadata = Metadata.singleton();
        detector = new HBEpochRaceDetector(config, metadata);
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
    public void testHBValidWrite1() {
        for (int i = 0; i < 4; i++) {
            detector.analyzeEvent(trace.get(i));
        }

        int expectedValue = 1;
        int actualValue = detector.state.getHBPredecessorThreadVectorClock(1L).get(1);
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void testHBValidWrite2() {
        for (int i = 0; i < 10; i++) {
            detector.analyzeEvent(trace.get(i));
        }

        int expectedValue = 3;
        int actualValue = detector.state.getHBPredecessorThreadVectorClock(2L).get(1);
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
