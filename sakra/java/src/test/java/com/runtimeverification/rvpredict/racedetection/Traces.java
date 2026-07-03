package com.runtimeverification.rvpredict.racedetection;

import java.util.ArrayList;

import com.runtimeverification.rvpredict.log.Event;
import com.runtimeverification.rvpredict.log.EventType;
import com.runtimeverification.rvpredict.log.ReadonlyEventInterface;
import com.runtimeverification.rvpredict.racedetection.event.RapidEvent;
import com.runtimeverification.rvpredict.racedetection.event.RapidEventType;

public class Traces {

    public ArrayList<RapidEvent> rapidRace1;
    public ArrayList<RapidEvent> rapidRace2;
    public ArrayList<RapidEvent> rapidRace3;
    public ArrayList<RapidEvent> rapidRace4;

    public ArrayList<ReadonlyEventInterface> rvRace1;
    public ArrayList<ReadonlyEventInterface> rvRace2;
    public ArrayList<ReadonlyEventInterface> rvRace3;
    public ArrayList<ReadonlyEventInterface> rvRace4;

    public ArrayList<RapidEvent> rapidNoRace1;
    public ArrayList<RapidEvent> rapidNoRace2;
    public ArrayList<RapidEvent> rapidNoRace3;

    public ArrayList<ReadonlyEventInterface> rvNoRace1;
    public ArrayList<ReadonlyEventInterface> rvNoRace2;
    public ArrayList<ReadonlyEventInterface> rvNoRace3;

    public ArrayList<ReadonlyEventInterface> readWriteNoRace;

    public ArrayList<ReadonlyEventInterface> rvCLInitRace;
    public ArrayList<ReadonlyEventInterface> rvRecursiveCLInitRace;
    public ArrayList<ReadonlyEventInterface> rvRecursiveCLInitRace1;
    public ArrayList<ReadonlyEventInterface> rvRecursiveCLInitRace2;
    public ArrayList<ReadonlyEventInterface> rvRecursiveCLInitRace3;
    public ArrayList<ReadonlyEventInterface> rvRecursiveCLInitRace4;
    public ArrayList<ReadonlyEventInterface> rvRecursiveCLInitRace5;

    public Traces() {
        setUpRapidRace1();
        setUpRapidRace2();
        setUpRapidRace3();
        setUpRapidRace4();

        setUpRVRace1();
        setUpRVRace2();
        setUpRVRace3();
        setUpRVRace4();

        setUpRapidNoRace1();
        setUpRapidNoRace2();
        setUpRapidNoRace3();

        setUpRVNoRace1();
        setUpRVNoRace2();
        setUpRVNoRace3();

        setUpReadWriteLockNoRace();

        setUpCLInitRace();
        setUpRecursiveCLInitRace();
        setUpRecursiveCLInitRace1();
        setUpRecursiveCLInitRace2();
        setUpRecursiveCLInitRace3();
        setUpRecursiveCLInitRace4();
        setUpRecursiveCLInitRace5();
    }

    private void setUpRapidRace1() {
        rapidRace1 = new ArrayList<>();
        rapidRace1.add(new RapidEvent(1L, 649, RapidEventType.FORK, 1, 0L, 0L, 2L, false, false));
        rapidRace1.add(new RapidEvent(2L, 642, RapidEventType.READ, 1, 0L, 12L, 0L, false, false));
        rapidRace1.add(new RapidEvent(3L, 643, RapidEventType.WRITE, 1, 1L, 13L, 1L, false, false));
        rapidRace1.add(new RapidEvent(4L, 644, RapidEventType.READ, 2, 1L, 13L, 1L, false, false));
        rapidRace1.add(new RapidEvent(5L, 645, RapidEventType.WRITE, 2, 0L, 12L, 0L, false, false));
    }

    private void setUpRapidRace2() {
        rapidRace2 = new ArrayList<>();
        rapidRace2.add(new RapidEvent(1L, 649, RapidEventType.FORK, 1, 0L, 0L, 2L, false, false));
        rapidRace2.add(new RapidEvent(2L, 642, RapidEventType.READ, 1, 0L, 12L, 0L, false, false));
        rapidRace2.add(new RapidEvent(3L, 643, RapidEventType.READ, 1, 1L, 13L, 0L, false, false));
        rapidRace2.add(new RapidEvent(4L, 644, RapidEventType.WRITE, 2, 1L, 13L, 0L, false, false));
        rapidRace2.add(new RapidEvent(5L, 645, RapidEventType.WRITE, 2, 0L, 12L, 0L, false, false));
    }

    private void setUpRapidRace3() {
        rapidRace3 = new ArrayList<>();
        rapidRace3.add(new RapidEvent(1L, 649, RapidEventType.FORK, 1, 0L, 0L, 2L, false, false));
        rapidRace3.add(new RapidEvent(2L, 649, RapidEventType.FORK, 1, 0L, 0L, 3L, false, false));
        rapidRace3.add(new RapidEvent(3L, 642, RapidEventType.ACQUIRE, 1, 1234L, 0L, 0L, false, false));
        rapidRace3.add(new RapidEvent(4L, 643, RapidEventType.WRITE, 1, 0L, 1L, 0L, false, false));
        rapidRace3.add(new RapidEvent(5L, 644, RapidEventType.RELEASE, 1, 1234L, 0L, 0L, false, false));
        rapidRace3.add(new RapidEvent(6L, 645, RapidEventType.ACQUIRE, 2, 1234L, 0L, 0L, false, false));
        rapidRace3.add(new RapidEvent(7L, 646, RapidEventType.WRITE, 2, 0L, 1L, 0L, false, false));
        rapidRace3.add(new RapidEvent(8L, 647, RapidEventType.RELEASE, 2, 1234L, 0L, 0L, false, false));
        rapidRace3.add(new RapidEvent(9L, 648, RapidEventType.READ, 3, 0L, 1L, 0L, false, false));
        rapidRace3.add(new RapidEvent(10L, 649, RapidEventType.FORK, 3, 0L, 0L, 4L, false, false));
        rapidRace3.add(new RapidEvent(11L, 650, RapidEventType.WRITE, 4, 0L, 1L, 0L, false, false));
        rapidRace3.add(new RapidEvent(12L, 651, RapidEventType.WRITE, 4, 0L, 1L, 0L, false, false));
        rapidRace3.add(new RapidEvent(13L, 652, RapidEventType.JOIN, 3, 0L, 0L, 4L, false, false));
        rapidRace3.add(new RapidEvent(14L, 653, RapidEventType.READ, 3, 0L, 1L, 0L, false, false));
    }

    private void setUpRapidRace4() {
        rapidRace4 = new ArrayList<>();
        rapidRace4.add(new RapidEvent(1L, 649, RapidEventType.FORK, 1, 0L, 0L, 2L, false, false));
        rapidRace4.add(new RapidEvent(2L, 649, RapidEventType.FORK, 1, 0L, 0L, 3L, false, false));
        rapidRace4.add(new RapidEvent(3L, 649, RapidEventType.FORK, 1, 0L, 0L, 4L, false, false));
        rapidRace4.add(new RapidEvent(4L, 642, RapidEventType.ACQUIRE, 1, 1234L, 0L, 0L, false, false));
        rapidRace4.add(new RapidEvent(5L, 643, RapidEventType.WRITE, 1, 0L, 1L, 0L, false, false));
        rapidRace4.add(new RapidEvent(6L, 644, RapidEventType.READ, 2, 0L, 1L, 0L, false, false));
        rapidRace4.add(new RapidEvent(7L, 645, RapidEventType.WRITE, 2, 0L, 2L, 0L, false, false));
        rapidRace4.add(new RapidEvent(8L, 646, RapidEventType.WRITE, 2, 0L, 1L, 0L, false, false));
        rapidRace4.add(new RapidEvent(9L, 647, RapidEventType.READ, 1, 0L, 1L, 0L, false, false));
        rapidRace4.add(new RapidEvent(10L, 648, RapidEventType.RELEASE, 1, 1234L, 0L, 0L, false, false));
        rapidRace4.add(new RapidEvent(11L, 649, RapidEventType.ACQUIRE, 4, 1234L, 0L, 0L, false, false));
        rapidRace4.add(new RapidEvent(12L, 650, RapidEventType.WRITE, 4, 0L, 3L, 0L, false, false));
        rapidRace4.add(new RapidEvent(13L, 651, RapidEventType.READ, 3, 0L, 3L, 0L, false, false));
        rapidRace4.add(new RapidEvent(14L, 652, RapidEventType.WRITE, 3, 0L, 2L, 0L, false, false));
        rapidRace4.add(new RapidEvent(15L, 653, RapidEventType.WRITE, 3, 0L, 3L, 0L, false, false));
        rapidRace4.add(new RapidEvent(16L, 654, RapidEventType.READ, 4, 0L, 3L, 0L, false, false));
        rapidRace4.add(new RapidEvent(17L, 655, RapidEventType.RELEASE, 4, 1234L, 0L, 0L, false, false));
    }

    private void setUpRVRace1() {
        rvRace1 = new ArrayList<>();
        rvRace1.add(new Event(1L, 1L, 643L, 2L, 0L, EventType.START_THREAD));
        rvRace1.add(new Event(2L, 1L, 642L, 12L, 1L, EventType.READ));
        rvRace1.add(new Event(3L, 1L, 643L, 13L, 100000L, EventType.WRITE));
        rvRace1.add(new Event(4L, 2L, 644L, 13L, 14551L, EventType.READ));
        rvRace1.add(new Event(5L, 2L, 645L, 12L, 12L, EventType.WRITE));
    }

    private void setUpRVRace2() {
        rvRace2 = new ArrayList<>();
        rvRace2.add(new Event(1L, 1L, 643L, 2L, 0L, EventType.START_THREAD));
        rvRace2.add(new Event(2L, 1L, 642L, 12L, 1L, EventType.READ));
        rvRace2.add(new Event(3L, 1L, 643L, 13L, 100000L, EventType.READ));
        rvRace2.add(new Event(4L, 2L, 644L, 13L, 14551L, EventType.WRITE));
        rvRace2.add(new Event(5L, 2L, 645L, 12L, 12L, EventType.WRITE));
    }

    private void setUpRVRace3() {
        rvRace3 = new ArrayList<>();
        rvRace3.add(new Event(1L, 1L, 643L, 2L, 0L, EventType.START_THREAD));
        rvRace3.add(new Event(2L, 1L, 643L, 3L, 0L, EventType.START_THREAD));
        rvRace3.add(new Event(3L, 1L, 642L, 1234L, 1L, EventType.WRITE_LOCK));
        rvRace3.add(new Event(4L, 1L, 643L, 1L, 100000L, EventType.WRITE));
        rvRace3.add(new Event(5L, 1L, 644L, 1234L, 14551L, EventType.WRITE_UNLOCK));
        rvRace3.add(new Event(6L, 2L, 645L, 1234L, 12L, EventType.WRITE_LOCK));
        rvRace3.add(new Event(7L, 2L, 646L, 1L, 12L, EventType.WRITE));
        rvRace3.add(new Event(8L, 2L, 647L, 1234L, 12L, EventType.WRITE_UNLOCK));
        rvRace3.add(new Event(9L, 3L, 648L, 1L, 12L, EventType.READ));
        rvRace3.add(new Event(10L, 3L, 649L, 4L, 12L, EventType.START_THREAD));
        rvRace3.add(new Event(11L, 4L, 650L, 1L, 12L, EventType.WRITE));
        rvRace3.add(new Event(12L, 4L, 651L, 1L, 12L, EventType.WRITE));
        rvRace3.add(new Event(13L, 3L, 652L, 4L, 12L, EventType.JOIN_THREAD));
        rvRace3.add(new Event(14L, 3L, 653L, 1L, 12L, EventType.READ));
    }

    private void setUpRVRace4() {
        rvRace4 = new ArrayList<>();
        rvRace4.add(new Event(1L, 1L, 643L, 2L, 0L, EventType.START_THREAD));
        rvRace4.add(new Event(2L, 1L, 643L, 3L, 0L, EventType.START_THREAD));
        rvRace4.add(new Event(3L, 1L, 643L, 4L, 0L, EventType.START_THREAD));
        rvRace4.add(new Event(4L, 1L, 642L, 1234L, 1L, EventType.WRITE_LOCK));
        rvRace4.add(new Event(5L, 1L, 643L, 1L, 100000L, EventType.WRITE));
        rvRace4.add(new Event(6L, 2L, 644L, 1L, 14551L, EventType.READ));
        rvRace4.add(new Event(7L, 2L, 645L, 2L, 12L, EventType.WRITE));
        rvRace4.add(new Event(8L, 2L, 646L, 1L, 12L, EventType.WRITE));
        rvRace4.add(new Event(9L, 1L, 647L, 1L, 12L, EventType.READ));
        rvRace4.add(new Event(10L, 1L, 648L, 1234L, 12L, EventType.WRITE_UNLOCK));
        rvRace4.add(new Event(11L, 4L, 649L, 1234L, 12L, EventType.WRITE_LOCK));
        rvRace4.add(new Event(12L, 4L, 650L, 3L, 12L, EventType.WRITE));
        rvRace4.add(new Event(13L, 3L, 651L, 3L, 12L, EventType.READ));
        rvRace4.add(new Event(14L, 3L, 652L, 2L, 12L, EventType.WRITE));
        rvRace4.add(new Event(15L, 3L, 653L, 3L, 12L, EventType.WRITE));
        rvRace4.add(new Event(16L, 4L, 654L, 3L, 12L, EventType.READ));
        rvRace4.add(new Event(17L, 4L, 655L, 1234L, 12L, EventType.WRITE_UNLOCK));
    }

    // no writes
    private void setUpRapidNoRace1() {
        rapidNoRace1 = new ArrayList<>();
        rapidNoRace1.add(new RapidEvent(1L, 649, RapidEventType.FORK, 1, 0L, 0L, 2L, false, false));
        rapidNoRace1.add(new RapidEvent(2L, 642, RapidEventType.READ, 1, 0L, 12L, 0L, false, false));
        rapidNoRace1.add(new RapidEvent(3L, 643, RapidEventType.READ, 1, 1L, 13L, 1L, false, false));
        rapidNoRace1.add(new RapidEvent(4L, 644, RapidEventType.READ, 2, 1L, 13L, 1L, false, false));
        rapidNoRace1.add(new RapidEvent(5L, 645, RapidEventType.READ, 2, 0L, 12L, 0L, false, false));
    }

    // variables all protected by locks
    private void setUpRapidNoRace2() {
        rapidNoRace2 = new ArrayList<>();
        rapidNoRace2.add(new RapidEvent(1L, 649, RapidEventType.FORK, 1, 0L, 0L, 2L, false, false));
        rapidNoRace2.add(new RapidEvent(2L, 649, RapidEventType.FORK, 1, 0L, 0L, 3L, false, false));
        rapidNoRace2.add(new RapidEvent(3L, 642, RapidEventType.ACQUIRE, 1, 1234L, 0L, 0L, false, false));
        rapidNoRace2.add(new RapidEvent(4L, 643, RapidEventType.WRITE, 1, 1L, 13L, 1L, false, false));
        rapidNoRace2.add(new RapidEvent(5L, 644, RapidEventType.RELEASE, 1, 1234L, 0L, 1L, false, false));

        rapidNoRace2.add(new RapidEvent(6L, 645, RapidEventType.ACQUIRE, 2, 1234L, 0L, 0L, false, false));
        rapidNoRace2.add(new RapidEvent(7L, 646, RapidEventType.WRITE, 2, 1L, 13L, 1L, false, false));
        rapidNoRace2.add(new RapidEvent(8L, 647, RapidEventType.RELEASE, 2, 1234L, 0L, 1L, false, false));

        rapidNoRace2.add(new RapidEvent(9L, 648, RapidEventType.ACQUIRE, 3, 1234L, 0L, 0L, false, false));
        rapidNoRace2.add(new RapidEvent(10L, 649, RapidEventType.WRITE, 3, 1L, 13L, 1L, false, false));
        rapidNoRace2.add(new RapidEvent(11L, 650, RapidEventType.RELEASE, 3, 1234L, 0L, 1L, false, false));
    }

    // thread 2 read must come before the write, so no race
    private void setUpRapidNoRace3() {
        rapidNoRace3 = new ArrayList<>();
        rapidNoRace3.add(new RapidEvent(1L, 649, RapidEventType.FORK, 1, 0L, 0L, 2L, false, false));
        rapidNoRace3.add(new RapidEvent(2L, 642, RapidEventType.ACQUIRE, 1, 1234L, 0L, 0L, false, false));
        rapidNoRace3.add(new RapidEvent(3L, 643, RapidEventType.WRITE, 1, 1L, 1L, 1L, false, false));
        rapidNoRace3.add(new RapidEvent(4L, 646, RapidEventType.RELEASE, 1, 1234L, 0L, 1L, false, false));

        rapidNoRace3.add(new RapidEvent(5L, 647, RapidEventType.ACQUIRE, 2, 1234L, 0L, 0L, false, false));
        rapidNoRace3.add(new RapidEvent(6L, 648, RapidEventType.READ, 2, 1L, 1L, 1L, false, false));
        rapidNoRace3.add(new RapidEvent(7L, 646, RapidEventType.RELEASE, 2, 1234L, 0L, 1L, false, false));
        rapidNoRace3.add(new RapidEvent(8L, 650, RapidEventType.WRITE, 2, 1L, 1L, 1L, false, false));
    }

    private void setUpRVNoRace1() {
        rvNoRace1 = new ArrayList<>();
        rvNoRace1.add(new Event(1L, 1L, 643L, 2L, 0L, EventType.START_THREAD));
        rvNoRace1.add(new Event(2L, 1L, 642L, 12L, 1L, EventType.READ));
        rvNoRace1.add(new Event(3L, 1L, 643L, 13L, 100000L, EventType.READ));
        rvNoRace1.add(new Event(4L, 2L, 644L, 13L, 14551L, EventType.READ));
        rvNoRace1.add(new Event(5L, 2L, 645L, 12L, 12L, EventType.READ));
    }

    private void setUpRVNoRace2() {
        rvNoRace2 = new ArrayList<>();
        rvNoRace2.add(new Event(1L, 1L, 643L, 2L, 0L, EventType.START_THREAD));
        rvNoRace2.add(new Event(2L, 2L, 643L, 3L, 0L, EventType.START_THREAD));
        rvNoRace2.add(new Event(3L, 1L, 642L, 1234L, 1L, EventType.WRITE_LOCK));
        rvNoRace2.add(new Event(4L, 1L, 643L, 13L, 100000L, EventType.WRITE));
        rvNoRace2.add(new Event(5L, 1L, 644L, 1234L, 14551L, EventType.WRITE_UNLOCK));

        rvNoRace2.add(new Event(6L, 2L, 645L, 1234L, 1L, EventType.WRITE_LOCK));
        rvNoRace2.add(new Event(7L, 2L, 646L, 13L, 100000L, EventType.WRITE));
        rvNoRace2.add(new Event(8L, 2L, 647L, 1234L, 14551L, EventType.WRITE_UNLOCK));

        rvNoRace2.add(new Event(9L, 3L, 648L, 1234L, 1L, EventType.WRITE_LOCK));
        rvNoRace2.add(new Event(10L, 3L, 649L, 13L, 100000L, EventType.WRITE));
        rvNoRace2.add(new Event(11L, 3L, 650L, 1234L, 14551L, EventType.WRITE_UNLOCK));
    }

    private void setUpRVNoRace3() {
        rvNoRace3 = new ArrayList<>();
        rvNoRace3.add(new Event(1L, 1L, 643L, 2L, 0L, EventType.START_THREAD));
        rvNoRace3.add(new Event(2L, 1L, 642L, 1234L, 1L, EventType.WRITE_LOCK));
        rvNoRace3.add(new Event(3L, 1L, 643L, 1L, 100000L, EventType.WRITE));
        rvNoRace3.add(new Event(4L, 1L, 644L, 1234L, 14551L, EventType.WRITE_UNLOCK));

        rvNoRace3.add(new Event(5L, 2L, 645L, 1234L, 1L, EventType.WRITE_LOCK));
        rvNoRace3.add(new Event(6L, 2L, 646L, 1L, 100000L, EventType.READ));
        rvNoRace3.add(new Event(7L, 2L, 647L, 1234L, 14551L, EventType.WRITE_UNLOCK));
        rvNoRace3.add(new Event(8L, 2L, 8, 1L, 100000L, EventType.WRITE));
    }

    private void setUpReadWriteLockNoRace() {
        readWriteNoRace = new ArrayList<>();
        readWriteNoRace.add(new Event(1L, 1L, 643L, 2L, 0L, EventType.START_THREAD));
        readWriteNoRace.add(new Event(2L, 1L, 643L, 3L, 0L, EventType.START_THREAD));
        readWriteNoRace.add(new Event(3L, 1L, 642L, 1234L, 1L, EventType.READ_LOCK));
        readWriteNoRace.add(new Event(4L, 1L, 643L, 1L, 100000L, EventType.READ));

        readWriteNoRace.add(new Event(5L, 2L, 644L, 1234L, 14551L, EventType.READ_LOCK));
        readWriteNoRace.add(new Event(6L, 2L, 645L, 1L, 14551L, EventType.READ));

        readWriteNoRace.add(new Event(7L, 1L, 646L, 1234L, 1L, EventType.READ_UNLOCK));

        readWriteNoRace.add(new Event(8L, 2L, 647L, 1234L, 1L, EventType.READ_UNLOCK));

        readWriteNoRace.add(new Event(9L, 3L, 648L, 1234L, 1L, EventType.WRITE_LOCK));
        readWriteNoRace.add(new Event(10L, 3L, 649L, 1234L, 1L, EventType.WRITE_LOCK));
        readWriteNoRace.add(new Event(11L, 3L, 650L, 1L, 100000L, EventType.WRITE));
    }

    private void setUpCLInitRace() {
        rvCLInitRace = new ArrayList<>();
        rvCLInitRace.add(new Event(1L, 1L, 643L, 0L, 0L, EventType.START_THREAD));
        rvCLInitRace.add(new Event(2L, 2L, 644L, 0L, 0L, EventType.START_THREAD));
        rvCLInitRace.add(new Event(3L, 3L, 645L, 0L, 0L, EventType.START_THREAD));

        rvCLInitRace.add(new Event(4L, 2L, 646L, 3L, 0L, EventType.WRITE));

        rvCLInitRace.add(new Event(5L, 1L, 647L, 3L, 0L, EventType.CLINIT_ENTER));
        rvCLInitRace.add(new Event(6L, 1L, 648L, 3L, 0L, EventType.WRITE));
        rvCLInitRace.add(new Event(7L, 1L, 649L, 3L, 0L, EventType.READ));

        // testing interleaving events in CLINIT
        rvCLInitRace.add(new Event(8L, 2L, 650L, 3L, 0L, EventType.READ));

        rvCLInitRace.add(new Event(9L, 1L, 651L, 3L, 0L, EventType.READ));
        rvCLInitRace.add(new Event(10L, 1L, 652L, 3L, 0L, EventType.CLINIT_EXIT));

        rvCLInitRace.add(new Event(11L, 3L, 653L, 3L, 0L, EventType.CLINIT_ENTER));
        rvCLInitRace.add(new Event(12L, 3L, 654L, 3L, 0L, EventType.WRITE));
        rvCLInitRace.add(new Event(13L, 3L, 655L, 3L, 0L, EventType.READ));
        rvCLInitRace.add(new Event(14L, 3L, 656L, 3L, 0L, EventType.CLINIT_EXIT));

        rvCLInitRace.add(new Event(15L, 2L, 657L, 3L, 0L, EventType.WRITE));
    }

    private void setUpRecursiveCLInitRace() {
        rvRecursiveCLInitRace = new ArrayList<>();
        rvRecursiveCLInitRace.add(new Event(1L, 1L, 643L, 0L, 0L, EventType.START_THREAD));
        rvRecursiveCLInitRace.add(new Event(2L, 2L, 644L, 0L, 0L, EventType.START_THREAD));

        rvRecursiveCLInitRace.add(new Event(4L, 1L, 647L, 3L, 0L, EventType.CLINIT_ENTER));
        rvRecursiveCLInitRace.add(new Event(5L, 1L, 648L, 3L, 0L, EventType.WRITE));
        rvRecursiveCLInitRace.add(new Event(6L, 1L, 647L, 3L, 0L, EventType.CLINIT_ENTER));
        rvRecursiveCLInitRace.add(new Event(7L, 1L, 652L, 3L, 0L, EventType.CLINIT_EXIT));
        rvRecursiveCLInitRace.add(new Event(8L, 1L, 648L, 3L, 0L, EventType.WRITE));
        rvRecursiveCLInitRace.add(new Event(9L, 1L, 652L, 3L, 0L, EventType.CLINIT_EXIT));

        rvRecursiveCLInitRace.add(new Event(10L, 2L, 654L, 3L, 0L, EventType.WRITE));
    }

    private void setUpRecursiveCLInitRace1() {
        rvRecursiveCLInitRace1 = new ArrayList<>();
        rvRecursiveCLInitRace1.add(new Event(1L, 1L, 643L, 0L, 0L, EventType.START_THREAD));
        rvRecursiveCLInitRace1.add(new Event(2L, 2L, 644L, 0L, 0L, EventType.START_THREAD));

        rvRecursiveCLInitRace1.add(new Event(4L, 1L, 647L, 3L, 0L, EventType.CLINIT_ENTER));
        rvRecursiveCLInitRace1.add(new Event(5L, 1L, 648L, 3L, 0L, EventType.WRITE));
        rvRecursiveCLInitRace1.add(new Event(6L, 1L, 647L, 3L, 0L, EventType.CLINIT_ENTER));
        rvRecursiveCLInitRace1.add(new Event(7L, 1L, 652L, 3L, 0L, EventType.CLINIT_EXIT));
        rvRecursiveCLInitRace1.add(new Event(8L, 1L, 648L, 3L, 0L, EventType.WRITE));
        rvRecursiveCLInitRace1.add(new Event(9L, 1L, 652L, 3L, 0L, EventType.CLINIT_EXIT));

        rvRecursiveCLInitRace1.add(new Event(10L, 2L, 654L, 3L, 0L, EventType.WRITE));
        rvRecursiveCLInitRace1.add(new Event(11L, 2L, 654L, 3L, 0L, EventType.READ));
    }

    private void setUpRecursiveCLInitRace2() {
        rvRecursiveCLInitRace2 = new ArrayList<>();
        rvRecursiveCLInitRace2.add(new Event(1L, 1L, 643L, 0L, 0L, EventType.START_THREAD));
        rvRecursiveCLInitRace2.add(new Event(2L, 2L, 644L, 0L, 0L, EventType.START_THREAD));

        rvRecursiveCLInitRace2.add(new Event(4L, 1L, 647L, 3L, 0L, EventType.CLINIT_ENTER));
        rvRecursiveCLInitRace2.add(new Event(5L, 1L, 648L, 3L, 0L, EventType.WRITE));
        rvRecursiveCLInitRace2.add(new Event(6L, 1L, 648L, 3L, 0L, EventType.READ));
        rvRecursiveCLInitRace2.add(new Event(7L, 1L, 647L, 3L, 0L, EventType.CLINIT_ENTER));
        rvRecursiveCLInitRace2.add(new Event(8L, 1L, 652L, 3L, 0L, EventType.CLINIT_EXIT));
        rvRecursiveCLInitRace2.add(new Event(9L, 1L, 648L, 3L, 0L, EventType.WRITE));
        rvRecursiveCLInitRace2.add(new Event(10L, 1L, 648L, 3L, 0L, EventType.READ));
        rvRecursiveCLInitRace2.add(new Event(11L, 1L, 652L, 3L, 0L, EventType.CLINIT_EXIT));

        rvRecursiveCLInitRace2.add(new Event(11L, 2L, 654L, 3L, 0L, EventType.WRITE));
    }

    private void setUpRecursiveCLInitRace3() {
        rvRecursiveCLInitRace3 = new ArrayList<>();
        rvRecursiveCLInitRace3.add(new Event(1L, 1L, 643L, 0L, 0L, EventType.START_THREAD));
        rvRecursiveCLInitRace3.add(new Event(2L, 2L, 644L, 0L, 0L, EventType.START_THREAD));
        rvRecursiveCLInitRace3.add(new Event(3L, 3L, 644L, 0L, 0L, EventType.START_THREAD));

        rvRecursiveCLInitRace3.add(new Event(4L, 3L, 654L, 3L, 0L, EventType.WRITE));

        rvRecursiveCLInitRace3.add(new Event(5L, 1L, 647L, 3L, 0L, EventType.CLINIT_ENTER));
        rvRecursiveCLInitRace3.add(new Event(6L, 1L, 648L, 3L, 0L, EventType.WRITE));
        rvRecursiveCLInitRace3.add(new Event(7L, 1L, 648L, 3L, 0L, EventType.READ));
        rvRecursiveCLInitRace3.add(new Event(8L, 1L, 647L, 3L, 0L, EventType.CLINIT_ENTER));
        rvRecursiveCLInitRace3.add(new Event(9L, 1L, 652L, 3L, 0L, EventType.CLINIT_EXIT));
        rvRecursiveCLInitRace3.add(new Event(10L, 1L, 648L, 3L, 0L, EventType.WRITE));
        rvRecursiveCLInitRace3.add(new Event(11L, 1L, 648L, 3L, 0L, EventType.READ));
        rvRecursiveCLInitRace3.add(new Event(12L, 1L, 652L, 3L, 0L, EventType.CLINIT_EXIT));

        rvRecursiveCLInitRace3.add(new Event(13L, 2L, 654L, 3L, 0L, EventType.WRITE));
    }

    private void setUpRecursiveCLInitRace4() {
        rvRecursiveCLInitRace4 = new ArrayList<>();
        rvRecursiveCLInitRace4.add(new Event(1L, 1L, 643L, 0L, 0L, EventType.START_THREAD));
        rvRecursiveCLInitRace4.add(new Event(2L, 2L, 644L, 0L, 0L, EventType.START_THREAD));
        rvRecursiveCLInitRace4.add(new Event(3L, 3L, 644L, 0L, 0L, EventType.START_THREAD));

        rvRecursiveCLInitRace4.add(new Event(4L, 3L, 654L, 3L, 0L, EventType.READ));

        rvRecursiveCLInitRace4.add(new Event(5L, 1L, 647L, 3L, 0L, EventType.CLINIT_ENTER));
        rvRecursiveCLInitRace4.add(new Event(6L, 1L, 648L, 3L, 0L, EventType.WRITE));
        rvRecursiveCLInitRace4.add(new Event(7L, 1L, 648L, 3L, 0L, EventType.READ));
        rvRecursiveCLInitRace4.add(new Event(8L, 1L, 647L, 3L, 0L, EventType.CLINIT_ENTER));
        rvRecursiveCLInitRace4.add(new Event(9L, 1L, 652L, 3L, 0L, EventType.CLINIT_EXIT));
        rvRecursiveCLInitRace4.add(new Event(10L, 1L, 648L, 3L, 0L, EventType.WRITE));
        rvRecursiveCLInitRace4.add(new Event(11L, 1L, 648L, 3L, 0L, EventType.READ));
        rvRecursiveCLInitRace4.add(new Event(12L, 1L, 652L, 3L, 0L, EventType.CLINIT_EXIT));

        rvRecursiveCLInitRace4.add(new Event(13L, 2L, 654L, 3L, 0L, EventType.WRITE));
    }

    private void setUpRecursiveCLInitRace5() {
        rvRecursiveCLInitRace5 = new ArrayList<>();
        rvRecursiveCLInitRace5.add(new Event(1L, 1L, 643L, 0L, 0L, EventType.START_THREAD));
        rvRecursiveCLInitRace5.add(new Event(2L, 2L, 644L, 0L, 0L, EventType.START_THREAD));
        rvRecursiveCLInitRace5.add(new Event(3L, 3L, 644L, 0L, 0L, EventType.START_THREAD));

        rvRecursiveCLInitRace5.add(new Event(4L, 3L, 654L, 3L, 0L, EventType.WRITE));

        rvRecursiveCLInitRace5.add(new Event(5L, 1L, 647L, 3L, 0L, EventType.CLINIT_ENTER));
        rvRecursiveCLInitRace5.add(new Event(6L, 1L, 648L, 3L, 0L, EventType.WRITE));
        rvRecursiveCLInitRace5.add(new Event(7L, 1L, 648L, 3L, 0L, EventType.READ));
        rvRecursiveCLInitRace5.add(new Event(8L, 1L, 647L, 3L, 0L, EventType.CLINIT_ENTER));
        rvRecursiveCLInitRace5.add(new Event(9L, 1L, 652L, 3L, 0L, EventType.CLINIT_EXIT));
        rvRecursiveCLInitRace5.add(new Event(10L, 1L, 648L, 3L, 0L, EventType.WRITE));
        rvRecursiveCLInitRace5.add(new Event(11L, 1L, 648L, 3L, 0L, EventType.READ));
        rvRecursiveCLInitRace5.add(new Event(12L, 1L, 652L, 3L, 0L, EventType.CLINIT_EXIT));

        rvRecursiveCLInitRace5.add(new Event(13L, 2L, 654L, 3L, 0L, EventType.READ));
    }
}
