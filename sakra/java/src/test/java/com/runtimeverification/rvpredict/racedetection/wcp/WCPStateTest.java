package com.runtimeverification.rvpredict.racedetection.wcp;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.EmptyStackException;

import com.runtimeverification.rvpredict.config.Configuration;
import com.runtimeverification.rvpredict.metadata.Metadata;
import com.runtimeverification.rvpredict.racedetection.Traces;
import com.runtimeverification.rvpredict.racedetection.event.RapidEvent;
import com.runtimeverification.rvpredict.racedetection.event.RapidEventType;

import org.junit.Before;
import org.junit.Test;

public class WCPStateTest {

    protected static WCPRaceDetector detector;
    protected static Traces traces;
    protected static ArrayList<RapidEvent> trace;

    @Before
    public void setUp() {
        Configuration config = Configuration.instance(new String[0]);
        Metadata metadata = Metadata.singleton();
        detector = new WCPRaceDetector(config, metadata);
        trace = new ArrayList<>();
        trace.add(new RapidEvent(1L, 642, RapidEventType.ACQUIRE, 1, 0L, 12L, 0L, false, false));
        trace.add(new RapidEvent(2L, 643, RapidEventType.WRITE, 1, 1L, 13L, 1L, false, false));
        trace.add(new RapidEvent(3L, 644, RapidEventType.RELEASE, 1, 0L, 13L, 1L, false, false));
        trace.add(new RapidEvent(4L, 645, RapidEventType.ACQUIRE, 1, 0L, 12L, 0L, false, false));
        trace.add(new RapidEvent(5L, 646, RapidEventType.WRITE, 1, 1L, 13L, 1L, false, false));
        trace.add(new RapidEvent(6L, 647, RapidEventType.RELEASE, 1, 0L, 13L, 1L, false, false));
        trace.add(new RapidEvent(7L, 648, RapidEventType.RELEASE, 1, 0L, 13L, 1L, false, false));
    }

    @Test
    public void testWCPValid1() {
        for (int i = 0; i < 2; i++) {
            detector.analyzeEvent(trace.get(i));
        }

        int expectedValue = 1;
        int actualValue = detector.state.WCPThread.get(1L).get(1);
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void testWCPValid2() {
        for (int i = 0; i < 6; i++) {
            detector.analyzeEvent(trace.get(i));
        }

        int expectedValue = 2;
        int actualValue = detector.state.WCPThread.get(1L).get(1);
        assertEquals(expectedValue, actualValue);
    }

    @Test(expected = EmptyStackException.class)
    public void testWCPInvalid() {
        for (int i = 0; i < 7; i++) {
            detector.analyzeEvent(trace.get(i));
        }
    }
}
