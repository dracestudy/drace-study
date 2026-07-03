package com.runtimeverification.rvpredict.util.PairFinder;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.BufferUnderflowException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.runtimeverification.rvpredict.util.StringStorageReader;
import com.runtimeverification.rvpredict.util.vectorclock.VectorClock;

public class Reader implements AutoCloseable {
    public FileChannel channel;
    public StringStorageReader stackReader;
    private ByteBuffer longBuffer;
    private ByteBuffer intBuffer;
    private ByteBuffer byteBuffer;
    private long lastValidPosition;
    private long fileSize;
    private boolean isTruncated = false;

     public Reader(Path path) throws IOException {
        Path directory = path.getParent();

        this.stackReader = new StringStorageReader(directory.toString(), "stack");
        this.channel = FileChannel.open(path, StandardOpenOption.READ);
        this.longBuffer = ByteBuffer.allocate(Long.BYTES);
        this.intBuffer = ByteBuffer.allocate(Integer.BYTES);
        this.byteBuffer = ByteBuffer.allocate(Byte.BYTES);
        this.fileSize = this.channel.size();

        // check if the file is truncated
        this.lastValidPosition = findLastValidPosition();
    }

    /**
     * Find the last valid position in the file. If the file is not truncated, the
     * last valid position will be the end of the file.
     * 
     * @return the last valid position in the file
     * @throws IOException
     */
    private long findLastValidPosition() throws IOException {
        long lastValidPosition = 0;
        this.channel.position(0);
        while (this.channel.position() < this.fileSize) {
            try {
                // skip the fixed sized section of the event
                // 1 + 8 + 8 + 4 + 8 + 8
                seekForward(37);
                skipVectorClocks();
                lastValidPosition = this.channel.position();
            } catch (IOException | BufferUnderflowException e) {
                this.isTruncated = true;
                break;
            }
        }
        this.channel.position(0);
        return lastValidPosition;
    }

    public long getLastValidPosition() {
        return this.lastValidPosition;
    }

    public long getFileSize() {
        return this.fileSize;
    }

    public boolean isTruncated() {
        return this.isTruncated;
    }

    public long readLong() throws IOException {
        while (this.longBuffer.hasRemaining()) {
            int bytesRead = this.channel.read(this.longBuffer);
            if (bytesRead == -1) {
                throw new IOException("Unexpected end of stream while reading a long.");
            }
        }
        this.longBuffer.flip();
        long value = this.longBuffer.getLong();
        this.longBuffer.clear();
        return value;
    }

    public int readInt() throws IOException {
        while (this.intBuffer.hasRemaining()) {
            int bytesRead = this.channel.read(this.intBuffer);
            if (bytesRead == -1) {
                throw new IOException("Unexpected end of stream while reading an integer.");
            }
        }
        this.intBuffer.flip();
        int value = this.intBuffer.getInt();
        this.intBuffer.clear();
        return value;
    }

    public byte readByte() throws IOException {
        while (this.byteBuffer.hasRemaining()) {
            int bytesRead = this.channel.read(this.byteBuffer);
            if (bytesRead == -1) {
                throw new IOException("Unexpected end of stream while reading a byte.");
            }
        }
        this.byteBuffer.flip();
        byte value = this.byteBuffer.get();
        this.byteBuffer.clear();
        return value;
    }

    public void seekForward(long offset) throws IOException {
        long currentPosition = this.channel.position();
        long newPosition = currentPosition + offset;
        this.channel.position(newPosition);
    }

    /**
     * Read a vector clock from the channel.
     * 
     * @return the vector clock read from the channel
     * @throws IOException
     */
    public VectorClock readVectorClock() throws IOException {
        int size = readInt();
        VectorClock vc = new VectorClock();
        for (int i = 0; i < size; i++) {
            int tid = readInt();
            int clock = readInt();
            vc.put(tid, clock);
        }
        return vc;
    }

    /**
     * Skip the two vector clocks in the event.
     * 
     * @throws IOException
     */
    public void skipVectorClocks() throws IOException {
        int preRdVcSize = readInt();
        seekForward(preRdVcSize * 8);
        int postRdVcSize = readInt();
        seekForward(postRdVcSize * 8);
    }

    /**
     * Generate a map of variables to the list of corresponding racy events.
     * Only variables with racy events are included in the map.
     * 
     * @return a map of variables to the list of corresponding racy events.
     * @throws IOException
     */
    public Map<Long, List<EventInfo>> getRacyEvents() throws IOException {
        Map<Long, List<EventInfo>> racyEventsMap = new HashMap<>();
        this.channel.position(0);
        while (this.channel.position() < this.lastValidPosition) {
            byte eventType = readByte();
            // check if 2nd bit is set
            boolean racy = (eventType & 0b10) != 0;
            // skip non-racy events
            if (!racy) {
                // 8 + 8 + 4 + 8 + 8
                seekForward(36);
                skipVectorClocks();
                continue;
            }
            long variable = readLong();
            long eid = readLong();
            int tid = readInt();
            long locIndex = readLong();
            long stackTraceIndex = readLong();
            VectorClock preRaceDetectionVc = readVectorClock();
            VectorClock postRaceDetectionVc = readVectorClock();

            EventInfo racyEvent = new EventInfo(eid, tid, eventType, variable, locIndex,
                    preRaceDetectionVc, postRaceDetectionVc, racy, stackTraceIndex);
            List<EventInfo> eventList = racyEventsMap.computeIfAbsent(variable, k -> new ArrayList<>());
            eventList.add(racyEvent);
        }
        return racyEventsMap;
    }

    /**
     * Find the racy pairs in the log file. Takes a map of variables to the list of
     * racy events for each variable, which can be generated using getRacyEvents().
     * 
     * @param racyEventsMap a map of variables to the list of corresponding racy events.
     * @return a map of racy events to the events they are potentially racy with.
     * @throws IOException
     */
    public Map<EventInfo, List<EventInfo>> findRacyPair(Map<Long, List<EventInfo>> racyEventsMap)
            throws IOException {
        return findRacyPair(racyEventsMap, true);
    }

    public Map<EventInfo, List<EventInfo>> findRacyPair(
            Map<Long, List<EventInfo>> racyEventsMap,
            boolean useVectorClock) throws IOException {
        Map<EventInfo, List<EventInfo>> racyMappings = new HashMap<>();
        for (List<EventInfo> racyEvents : racyEventsMap.values()) {
            for (EventInfo racyEvent : racyEvents) {
                racyMappings.put(racyEvent, new ArrayList<>());
            }
        }
        this.channel.position(0);
        while (this.channel.position() < this.lastValidPosition) {
            byte eventType = readByte();
            long variable = readLong();

            if (!racyEventsMap.containsKey(variable)) {
                // 8 + 4 + 8 + 8
                seekForward(28);
                skipVectorClocks();
                continue;
            }

            long eid = readLong();
            int tid = readInt();
            long locIndex = readLong();
            long stackTraceIndex = readLong();
            VectorClock preRaceDetectionVc = readVectorClock();
            VectorClock postRaceDetectionVc = readVectorClock();

            // We will lazily initialize this event
            EventInfo prevEvent = null;
            for (EventInfo racyEvent : racyEventsMap.get(variable)) {
                // Must be a previous event
                // We can break here since the events are added in order
                if (eid >= racyEvent.eid) {
                    break;
                }

                // Check if the two events are from different threads
                boolean fromDifferentThread = tid != racyEvent.tid;
                // Check at least one of the events is a write event
                boolean readWriteCompatibility = ((eventType | racyEvent.eventType) & 0b1) == 0b1;

                if (!(fromDifferentThread && readWriteCompatibility)) {
                    continue;
                }

                if (!useVectorClock || !postRaceDetectionVc.isLessThanOrEqual(racyEvent.preRaceDetectionVc)) {
                    if (prevEvent == null) {
                        boolean racy = (eventType & 0b10) != 0;
                        prevEvent = new EventInfo(eid, tid, eventType, variable, locIndex,
                            preRaceDetectionVc, postRaceDetectionVc, racy, stackTraceIndex);
                    }
                    racyMappings.get(racyEvent).add(prevEvent);
                }
            }
        }
        return racyMappings;
    }

    /**
     * Print the events in the dat file to the writer.
     * 
     * @param writer the writer to write the events to
     * @throws IOException
     */
    public void printEvents(BufferedWriter writer) throws IOException {
        long numEvents = 0;
        writer.write("Total Bytes: " + this.fileSize);
        writer.newLine();
        writer.write("Last Valid Position: " + this.lastValidPosition);
        writer.newLine();
        this.channel.position();
        while (this.channel.position() < this.lastValidPosition) {
            byte eventType = readByte();
            // check if 2nd bit is set
            boolean racy = (eventType & 0b10) != 0;
            long variable = readLong();
            long eid = readLong();
            int tid = readInt();
            long locIndex = readLong();
            long stackTraceIndex = readLong();
            VectorClock preRaceDetectionVc = readVectorClock();
            VectorClock postRaceDetectionVc = readVectorClock();

            EventInfo racyEvent = new EventInfo(eid, tid, eventType, variable, locIndex,
                    preRaceDetectionVc, postRaceDetectionVc, racy, stackTraceIndex);
            writer.write(racyEvent.toString());
            writer.write("|" + this.channel.position());
            writer.newLine();
            numEvents++;
        }
        writer.write("Number of events: " + numEvents);
        writer.newLine();
        writer.flush();
    }

    @Override
    public void close() throws IOException {
        this.channel.close();
    }
}
