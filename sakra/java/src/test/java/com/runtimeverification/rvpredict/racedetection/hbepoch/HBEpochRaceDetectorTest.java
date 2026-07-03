package com.runtimeverification.rvpredict.racedetection.hbepoch;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.runtimeverification.rvpredict.config.Configuration;
import com.runtimeverification.rvpredict.metadata.Metadata;
import com.runtimeverification.rvpredict.racedetection.Traces;
import com.runtimeverification.rvpredict.racedetection.event.RapidEvent;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class HBEpochRaceDetectorTest {

    protected static HBEpochRaceDetector detector;
    protected static Traces traces;

    @BeforeClass
    public static void build() {
        traces = new Traces();
    }

    @Before
    public void setUp() {
        Configuration config = Configuration.instance(new String[0]);
        Metadata metadata = Metadata.singleton();
        detector = new HBEpochRaceDetector(config, metadata);
    }

    @Test
    public void testRapidRaceProcess1() {
        detector.analyzeRapidTrace(traces.rapidRace1);
        List<RapidEvent> racyEvents = detector.getRacyEvents();
        List<Long> expectedRacyEIDs = Arrays.asList(4L, 5L);

        matchRacyEIDs(expectedRacyEIDs, racyEvents);
    }

    @Test
    public void testRapidRaceProcess2() {
        detector.analyzeRapidTrace(traces.rapidRace2);
        List<RapidEvent> racyEvents = detector.getRacyEvents();
        List<Long> expectedRacyEIDs = Arrays.asList(4L, 5L);

        matchRacyEIDs(expectedRacyEIDs, racyEvents);
    }

    @Test
    public void testRapidRaceProcess3() {
        detector.analyzeRapidTrace(traces.rapidRace3);
        List<RapidEvent> racyEvents = detector.getRacyEvents();
        List<Long> expectedRacyEIDs = Arrays.asList(9L, 11L);

        matchRacyEIDs(expectedRacyEIDs, racyEvents);
    }

    @Test
    public void testRapidRaceProcess4() {
        detector.analyzeRapidTrace(traces.rapidRace4);
        List<RapidEvent> racyEvents = detector.getRacyEvents();
        List<Long> expectedRacyEIDs = Arrays.asList(6L, 8L, 9L, 13L, 14L, 15L, 16L);

        matchRacyEIDs(expectedRacyEIDs, racyEvents);
    }

    @Test
    public void testRVRaceProcess1() {
        detector.analyzeRVTrace(traces.rvRace1);
        List<RapidEvent> racyEvents = detector.getRacyEvents();
        List<Long> expectedRacyEIDs = Arrays.asList(4L, 5L);

        matchRacyEIDs(expectedRacyEIDs, racyEvents);
    }

    @Test
    public void testRVRaceProcess2() {
        detector.analyzeRVTrace(traces.rvRace2);
        List<RapidEvent> racyEvents = detector.getRacyEvents();
        List<Long> expectedRacyEIDs = Arrays.asList(4L, 5L);

        matchRacyEIDs(expectedRacyEIDs, racyEvents);
    }

    @Test
    public void testRVRaceProcess3() {
        detector.analyzeRVTrace(traces.rvRace3);
        List<RapidEvent> racyEvents = detector.getRacyEvents();
        List<Long> expectedRacyEIDs = Arrays.asList(9L, 11L);

        matchRacyEIDs(expectedRacyEIDs, racyEvents);
    }

    @Test
    public void testRVRaceProcess4() {
        detector.analyzeRVTrace(traces.rvRace4);
        List<RapidEvent> racyEvents = detector.getRacyEvents();
        List<Long> expectedRacyEIDs = Arrays.asList(6L, 8L, 9L, 13L, 14L, 15L, 16L);

        matchRacyEIDs(expectedRacyEIDs, racyEvents);
    }

    @Test
    public void testRapidNoRaceProcess1() {
        detector.analyzeRapidTrace(traces.rapidNoRace1);
        List<RapidEvent> racyEvents = detector.getRacyEvents();
        List<Long> expectedRacyEIDs = Arrays.asList();

        matchRacyEIDs(expectedRacyEIDs, racyEvents);
    }

    @Test
    public void testRapidNoRaceProcess2() {
        detector.analyzeRapidTrace(traces.rapidNoRace2);
        List<RapidEvent> racyEvents = detector.getRacyEvents();
        List<Long> expectedRacyEIDs = Arrays.asList();

        matchRacyEIDs(expectedRacyEIDs, racyEvents);
    }

    @Test
    public void testRapidNoRaceProcess3() {
        detector.analyzeRapidTrace(traces.rapidNoRace3);
        List<RapidEvent> racyEvents = detector.getRacyEvents();
        List<Long> expectedRacyEIDs = Arrays.asList();

        matchRacyEIDs(expectedRacyEIDs, racyEvents);
    }

    @Test
    public void testRVNoRaceProcess1() {
        detector.analyzeRVTrace(traces.rvNoRace1);
        List<RapidEvent> racyEvents = detector.getRacyEvents();
        List<Long> expectedRacyEIDs = Arrays.asList();

        matchRacyEIDs(expectedRacyEIDs, racyEvents);
    }

    @Test
    public void testRVNoRaceProcess2() {
        detector.analyzeRVTrace(traces.rvNoRace2);
        List<RapidEvent> racyEvents = detector.getRacyEvents();
        List<Long> expectedRacyEIDs = Arrays.asList();

        matchRacyEIDs(expectedRacyEIDs, racyEvents);
    }

    @Test
    public void testRVNoRaceProcess3() {
        detector.analyzeRVTrace(traces.rvNoRace3);
        List<RapidEvent> racyEvents = detector.getRacyEvents();
        List<Long> expectedRacyEIDs = Arrays.asList();

        matchRacyEIDs(expectedRacyEIDs, racyEvents);
    }

    @Test
    public void testRVReadWriteLockNoRace() {
        detector.analyzeRVTrace(traces.readWriteNoRace);
        List<RapidEvent> racyEvents = detector.getRacyEvents();
        List<Long> expectedRacyEIDs = Arrays.asList();

        matchRacyEIDs(expectedRacyEIDs, racyEvents);
    }

    @Test
    public void testRVClInitRace() {
        detector.analyzeRVTrace(traces.rvCLInitRace);
        List<RapidEvent> racyEvents = detector.getRacyEvents();
        List<Long> expectedRacyEIDs = Arrays.asList();

        matchRacyEIDs(expectedRacyEIDs, racyEvents);
    }

    @Test
    public void testRVRecursiveClInitRace() {
        detector.analyzeRVTrace(traces.rvRecursiveCLInitRace);
        List<RapidEvent> racyEvents = detector.getRacyEvents();
        List<Long> expectedRacyEIDs = Arrays.asList();

        matchRacyEIDs(expectedRacyEIDs, racyEvents);
    }

    @Test
    public void testRVRecursiveClInitRace1() {
        detector.analyzeRVTrace(traces.rvRecursiveCLInitRace1);
        List<RapidEvent> racyEvents = detector.getRacyEvents();
        List<Long> expectedRacyEIDs = Arrays.asList();

        matchRacyEIDs(expectedRacyEIDs, racyEvents);
    }

    @Test
    public void testRVRecursiveClInitRace2() {
        detector.analyzeRVTrace(traces.rvRecursiveCLInitRace2);
        List<RapidEvent> racyEvents = detector.getRacyEvents();
        List<Long> expectedRacyEIDs = Arrays.asList();

        matchRacyEIDs(expectedRacyEIDs, racyEvents);
    }

    @Test
    public void testRVRecursiveClInitRace3() {
        detector.analyzeRVTrace(traces.rvRecursiveCLInitRace3);
        List<RapidEvent> racyEvents = detector.getRacyEvents();
        List<Long> expectedRacyEIDs = Arrays.asList(13L);

        matchRacyEIDs(expectedRacyEIDs, racyEvents);
    }

    @Test
    public void testRVRecursiveClInitRace4() {
        detector.analyzeRVTrace(traces.rvRecursiveCLInitRace4);
        List<RapidEvent> racyEvents = detector.getRacyEvents();
        List<Long> expectedRacyEIDs = Arrays.asList(13L);

        matchRacyEIDs(expectedRacyEIDs, racyEvents);
    }

    @Test
    public void testRVRecursiveClInitRace5() {
        detector.analyzeRVTrace(traces.rvRecursiveCLInitRace5);
        List<RapidEvent> racyEvents = detector.getRacyEvents();
        List<Long> expectedRacyEIDs = Arrays.asList(13L);

        matchRacyEIDs(expectedRacyEIDs, racyEvents);
    }

    private static void matchRacyEIDs(List<Long> expectedRacyEIDs, List<RapidEvent> actualRacyEvents) {
        List<Long> actualRacyEIDs = actualRacyEvents.stream().map(RapidEvent::getId)
                                        .collect(Collectors.toList());
        assertTrue(
                "Expected EIDs: " + expectedRacyEIDs.toString() + " does not match\nActual EIDs: "
                        + actualRacyEIDs.toString(),
                actualRacyEIDs.equals(expectedRacyEIDs));
    }
}
