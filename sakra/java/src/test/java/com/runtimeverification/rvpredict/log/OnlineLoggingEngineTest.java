package com.runtimeverification.rvpredict.log;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import com.runtimeverification.TraceLog;
import com.runtimeverification.rvpredict.config.Configuration;
import com.runtimeverification.rvpredict.racedetection.event.RapidEvent;
import com.runtimeverification.rvpredict.racedetection.event.RapidEventType;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Enclosed.class)
public class OnlineLoggingEngineTest {

    /*
     * Things to test:
     * Legal addresses
     * Illegal addresses
     * All correct info is in each argument (gid, locId, tid, eventType)
     *
     * A little harder:
     * finishLogging() has correct data races
     */

    protected static Configuration config;
    protected static OnlineLoggingEngine engine;

    @BeforeClass
    public static void setUp() {
        config = Configuration.instance(new String[0]);
        engine = new OnlineLoggingEngine(config, null);
    }

    // test class for legal events with addresses
    @RunWith(Parameterized.class)
    public static class LegalAddressTest {

        private TraceLog line;
        private long expectedAddr;

        public LegalAddressTest(
                EventType eventType,
                long gid,
                long tid,
                int locId,
                int addr1,
                int addr2,
                long value,
                long expectedAddr) {
            this.line = new TraceLog(eventType, gid, tid, locId, addr1, addr2, value);
            this.expectedAddr = expectedAddr;
        }

        @Parameterized.Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(
                    new Object[][] {
                        {EventType.WRITE, 1, 1, 642, 1451270520, -208, 0, 6233159425343881008L},
                        {
                            EventType.WRITE,
                            2,
                            1,
                            643,
                            1451270520,
                            -209,
                            2036368507,
                            6233159425343881007L
                        },
                        {EventType.READ, 5, 1, 618, 1785210046, -205, 2, 7667418768355622707L},
                        {EventType.START_THREAD, 34, 1, 623, 0, 13, 0, 13L},
                        {
                            EventType.READ,
                            35,
                            12,
                            649,
                            2085857771,
                            -211,
                            248609774,
                            8958690914847424301L
                        },
                        {EventType.WRITE_LOCK, 62, 13, 654, 43, 708049632, 0, 185391643360L},
                        {EventType.WRITE_UNLOCK, 64, 13, 654, 43, 708049632, 0, 185391643360L},
                        // this is illegal, should return
                        {EventType.BEGIN_THREAD, 64, 13, 654, 43, 708049632, 0, 185391643360L}
                    });
        }

        @Test
        public void testLogAddressingValid() {
            engine.log(
                    line.eventType,
                    line.gid,
                    line.tid,
                    line.locId,
                    line.addr1,
                    line.addr2,
                    line.value);
            RapidEvent event = engine.getEvent();
            RapidEventType type = event.getType();

            // must be one of these three types in order to access its address that is input
            long addr;
            if (type.isExtremeType()) {
                addr = event.getTarget();
            } else if (type.isAccessType()) {
                addr = event.getVariable();
            } else if (type.isLockType()) {
                addr = event.getLock();
            } else {
                return;
            }
            assertEquals(addr, expectedAddr);
        }
    }

    // test class for illegal events with addresses
    public static class IllegalAddressTest {

        @Test(expected = IllegalArgumentException.class)
        public void testLogAddressingIllegalBegin() {
            engine.log(EventType.BEGIN_THREAD, 2, 1, 643, 1451270520, -209, 2036368507L);
            RapidEvent event = engine.getEvent();
            long addr = event.getTarget();
        }

        @Test(expected = IllegalArgumentException.class)
        public void testLogAddressingIllegalEnd() {
            engine.log(EventType.END_THREAD, 5, 1, 618, 1785210046, -205, 2L);
            RapidEvent event = engine.getEvent();
            long addr = event.getTarget();
        }
    }

    // test class for correct conversion of RV EventTypes to Rapid EventTypes
    @RunWith(Parameterized.class)
    public static class EventTypeTest {
        private TraceLog line;
        private RapidEventType expectedType;

        public EventTypeTest(
                EventType eventType,
                long gid,
                long tid,
                int locId,
                int addr1,
                int addr2,
                long value,
                RapidEventType expectedType) {
            this.line = new TraceLog(eventType, gid, tid, locId, addr1, addr2, value);
            this.expectedType = expectedType;
        }

        @Parameterized.Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(
                    new Object[][] {
                        {EventType.WRITE, 1, 1, 642, 1451270520, -208, 0, RapidEventType.WRITE},
                        {EventType.READ, 5, 1, 618, 1785210046, -205, 2, RapidEventType.READ},
                        {EventType.START_THREAD, 34, 1, 623, 0, 13, 0, RapidEventType.FORK},
                        {EventType.JOIN_THREAD, 34, 1, 623, 0, 13, 0, RapidEventType.JOIN},
                        // should reads and writes be treated differently?
                        {
                            EventType.READ_LOCK,
                            35,
                            12,
                            649,
                            2085857771,
                            -211,
                            248609774,
                            RapidEventType.ACQUIRE
                        },
                        {
                            EventType.READ_UNLOCK,
                            35,
                            12,
                            649,
                            2085857771,
                            -211,
                            248609774,
                            RapidEventType.RELEASE
                        },
                        {
                            EventType.WRITE_LOCK,
                            62,
                            13,
                            654,
                            43,
                            708049632,
                            0,
                            RapidEventType.ACQUIRE
                        },
                        {
                            EventType.WRITE_UNLOCK,
                            64,
                            13,
                            654,
                            43,
                            708049632,
                            0,
                            RapidEventType.RELEASE
                        },
                        {
                            EventType.BEGIN_THREAD,
                            64,
                            13,
                            654,
                            43,
                            708049632,
                            0,
                            RapidEventType.BEGIN
                        },
                        {
                            EventType.END_THREAD,
                            2,
                            1,
                            643,
                            1451270520,
                            -209,
                            2036368507,
                            RapidEventType.END
                        }
                    });
        }

        @Test
        public void testEventType() {
            engine.log(
                    line.eventType,
                    line.gid,
                    line.tid,
                    line.locId,
                    line.addr1,
                    line.addr2,
                    line.value);
            RapidEvent event = engine.getEvent();
            RapidEventType type = event.getType();
            assertEquals(type, expectedType);
        }
    }

    public static class FinishLoggingTest {
        @Test
        public void testFinishLogging() {}
    }
}
