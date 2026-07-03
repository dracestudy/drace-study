package com.runtimeverification.rvpredict.racedetection.engine;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.runtimeverification.rvpredict.config.Configuration;
import com.runtimeverification.rvpredict.log.EventType;
import com.runtimeverification.rvpredict.log.ReadonlyEventInterface;
import com.runtimeverification.rvpredict.metadata.MetadataInterface;
import com.runtimeverification.rvpredict.racedetection.event.RaceDetectionEvent;
import com.runtimeverification.rvpredict.racedetection.event.RapidEvent;
import com.runtimeverification.rvpredict.racedetection.event.RapidEventType;
import com.runtimeverification.rvpredict.util.Logger;

public abstract class RaceDetectionEngine<S extends State, E extends RaceDetectionEvent<S>>
        extends RapidEngine<E> implements AutoCloseable {

    public S state;
    protected long eventCount;
    protected long totalSkippedEvents;
    protected HashSet<Integer> locIdsOfRacyEvents;
    protected List<RapidEvent> racyEvents;
    protected Configuration config;
    protected MetadataInterface metadata;
    protected Logger logger;
    protected List<String> reports;
    protected E handlerEvent;
    private HashMap<Long, Integer> clInitMap;

    public RaceDetectionEngine(Configuration config, MetadataInterface metadata, S state, E handlerEvent) {
        this.config = config;
        this.metadata = metadata;
        this.state = state;
        this.handlerEvent = handlerEvent;
        this.logger = Logger.getGlobal();
        this.reports = new ArrayList<>();
        this.locIdsOfRacyEvents = new HashSet<>();
        this.racyEvents = new ArrayList<>();
        this.clInitMap = new HashMap<>();
    }

    public List<String> getRaceReports() {
        return this.reports;
    }

    public List<RapidEvent> getRacyEvents() {
        return this.racyEvents;
    }

    protected boolean skipEvent(E handlerEvent) {
        return false;
    }

    protected void postHandleEvent(E handlerEvent) {}

    /**
     * Process an RVPredict event by converting it to a RapidEvent.
     *
     * @param event The event to process.
     */
    public boolean analyzeEvent(ReadonlyEventInterface event) {
        // convert eventType to rapidEventType
        // Conversion using enum field is the fastest based on benchmark
        // https://gist.github.com/Dethada/bedb4b534808263ed6020d0b10e47e6d
        // Enum to Enum Conversion Benchmark
        // Enum    duration: 5012154527 nanoseconds
        // DAT     duration: 5631655121 nanoseconds
        // Switch  duration: 10065094400 nanoseconds
        // If-Else duration: 11651560055 nanoseconds
        // HashMap duration: 21050060192 nanoseconds
        EventType eventType = event.getType();
        RapidEventType rapidEventType = eventType.getRapidEventType();

        long gid = event.getOriginalId();
        long tid = event.getOriginalThreadId();
        int locId = (int) event.getLocationId();
        long address = event.unsafeGetDataInternalIdentifier();

        boolean isVolatile = false;
        if (event.isReadOrWrite()) {
            isVolatile = metadata.isVolatile(event.getDataInternalIdentifier());
        }

        boolean toIgnore = updateCLInit(tid, eventType);
        if (!toIgnore) {
            toIgnore = isThreadSafeLocation(metadata, locId);
        }

        this.handlerEvent.updateEvent(gid, locId, rapidEventType, tid, address, address, address, isVolatile, toIgnore);
        return this.analyzeEvent();
    }

    /**
     * Process a RapidEvent.
     *
     * @param event The event to process.
     */
    public boolean analyzeEvent(RapidEvent event) {
        // update the handlerEvent with our new event info
        handlerEvent.copyFrom(event);
        return this.analyzeEvent();
    }

    protected boolean analyzeEvent() {
        this.eventCount++;
        // EventTypes that do not have a matching rapidEventType will be mapped to
        // the DUMMY type these events should not be processed.
        if (this.handlerEvent.getType().equals(RapidEventType.DUMMY)) {
            this.totalSkippedEvents++;
            return false;
        }

        boolean raceDetected = false;
        handlerEvent.preProcess(state);

        ByteBuffer preRaceDetectionVcBytes = null;
        if (config.report_pair) {
            preRaceDetectionVcBytes = handlerEvent.toThreadVectorClockBytes(state);
        }

        try {
            raceDetected = this.handlerEvent.handle(state);
        } catch (OutOfMemoryError oome) {
            this.logger.debug(oome);
            this.logger.report("Number of events = " + Long.toString(this.eventCount), Logger.MSGTYPE.ERROR);
            state.printMemory();
        }

        if (this.config.report_progress) {
            this.handlerEvent.printRaceInfo(state);
        }

        RapidEventType type = handlerEvent.getType();
        if (config.report_pair && type.isAccessType() && !handlerEvent.toIgnore() && !handlerEvent.isVolatile()) {
            // 1 + 8 + 8 + 4 + 8 + 8 bytes
            ByteBuffer buffer = ByteBuffer.allocate(37);
            buffer.order(ByteOrder.BIG_ENDIAN); // Ensure big-endian byte order

            // Event Detail: 1 Byte
            byte eventDetail = 0;
            if (handlerEvent.getType().isWrite()) {
                eventDetail |= 1; // Set LSB to 1 for Write Event
            }
            if (raceDetected) {
                eventDetail |= 2; // Set 2nd LSB to 1 for Racy Event
            }
            buffer.put(eventDetail);
            buffer.putLong(handlerEvent.getVariable());
            buffer.putLong(handlerEvent.getId());
            buffer.putInt(Math.toIntExact(handlerEvent.getThread()));

            try {
                String locString = metadata.getLocationSig(handlerEvent.getLocId());
                long index = this.logger.addLocationString(locString);
                buffer.putLong(index);
            } catch (NullPointerException | IOException exception) {
                buffer.putLong(-1); // Empty Location Signature
            }

            String stackString = handlerEvent.getStackTraceString(config);
            if (stackString.length() == 0) {
                // Empty Stack Trace
                buffer.putLong(-1);
            } else {
                try {
                    long index = this.logger.addStackTraceString(stackString);
                    buffer.putLong(index);
                } catch (IOException exception) {
                    buffer.putLong(-1);
                }
            }
            // Reset the position to the beginning of the buffer
            buffer.flip();

            this.logger.writePairInfo(buffer.array());
            this.logger.writePairInfo(preRaceDetectionVcBytes.array());
            this.logger.writePairInfo(handlerEvent.toThreadVectorClockBytes(state).array());
        }

        if (raceDetected) {
            RapidEvent racyEvent = new RapidEvent();
            racyEvent.copyFrom(this.handlerEvent);
            this.racyEvents.add(racyEvent);
            this.locIdsOfRacyEvents.add(this.handlerEvent.getLocId());
            String output = this.handlerEvent.toRaceReportString(this.metadata);
            this.reports.add(output);
            this.logger.report(output, Logger.MSGTYPE.PROGRESS);
        }
        return raceDetected;
    }

    public void analyzeRapidTrace(List<RapidEvent> trace) {
        for (RapidEvent event : trace) {
            this.analyzeEvent(event);
        }
    }

    public void analyzeRVTrace(List<ReadonlyEventInterface> trace) {
        for (ReadonlyEventInterface event : trace) {
            this.analyzeEvent(event);
        }
    }

    protected void postAnalysis() {}

    @Override
    protected void printCompletionStatus() {
        this.logger.reportAndLogResults("No. of lines of code involved in race(s)\t= " + this.locIdsOfRacyEvents.size(),
                Logger.MSGTYPE.REPORT);
        this.logger.reportAndLogResults("No. of race-triggering events\t\t\t= " + this.racyEvents.size(),
                Logger.MSGTYPE.VERBOSE);
        this.logger.reportAndLogResults("No. of events (trace length)\t\t\t= " + this.eventCount,
                Logger.MSGTYPE.VERBOSE);
        this.logger.reportAndLogResults("No. of events skipped by algorithm\t\t= " + this.totalSkippedEvents,
                Logger.MSGTYPE.VERBOSE);
    }

    public void printRaceReport() {
        this.logger.reportAndLogResults("--------------------Analysis complete---------------------------",
                Logger.MSGTYPE.REPORT);
        this.printCompletionStatus();
        if (!this.racyEvents.isEmpty()) {
            this.logger.reportAndLogResults("------------------Race Triggering Events------------------------",
                    Logger.MSGTYPE.VERBOSE);
            reports.forEach(r -> this.logger.reportAndLogResults(r, Logger.MSGTYPE.VERBOSE));
            this.logger.reportAndLogResults("--------------Lines of Code involved in Race(s)-----------------",
                    Logger.MSGTYPE.REPORT);
            for (int locId : locIdsOfRacyEvents) {
                String location = metadata.getLocationSig(locId);
                this.logger.reportAndLogResults(location, Logger.MSGTYPE.REPORT);
            }
        }
    }

    /**
     * Updates the clinit tracking of each thread, and
     * returns true if the event is in clinit, false otherwise.
    */
    public boolean updateCLInit(Long tid, EventType eventType) {
        int depth = clInitMap.getOrDefault(tid, 0);
        if (eventType == EventType.CLINIT_ENTER) {
            depth = depth + 1;
            clInitMap.put(tid, depth);
        } else if (eventType == EventType.CLINIT_EXIT) {
            // This should only be reached if CLINIT_ENTER has been encountered
            // If we are given a valid trace, the value should never go below 0
            depth = depth - 1;
            clInitMap.put(tid, depth);
        }
        // If depth is not 0, then the thread is in clinit
        return depth != 0;
    }

    public static boolean isThreadSafeLocation(MetadataInterface metadata, long locId) {
        if (metadata == null) {
            return false;
        }
        String locationSig = metadata.getLocationSig(locId);
        if (locationSig == null) {
            return false;
        }
        if (locationSig.startsWith("java.util.concurrent")
            || locationSig.startsWith("java.util.stream")) {
            return true;
        } else {
            int index = locationSig.lastIndexOf('.');
            if (index != -1) {
                return locationSig.substring(index).startsWith(".class$");
            }
        }
        return false;
    }

    @Override
    public void close() {}

    public void reset() {
        this.state.reset();
        this.eventCount = 0;
        this.totalSkippedEvents = 0;
        this.locIdsOfRacyEvents.clear();
        this.racyEvents.clear();
        this.reports.clear();
        this.clInitMap.clear();
    }

}
