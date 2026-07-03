package com.runtimeverification.rvpredict.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import com.runtimeverification.rvpredict.util.PairFinder.Reader;
import com.runtimeverification.rvpredict.util.PairFinder.EventInfo;

public class PairInfo {
    private static Reader reader;

     public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.err.println("Usage " + PairInfo.class.getName()
                    + " <log_file_name> <output_file_name> [mode]");
            System.err.println("  mode: vc (default), lockset, goldilocks, novc");
            System.exit(1);
        }
        Path path = Paths.get(args[0]).toAbsolutePath();
        Path output = Paths.get(args[1]).toAbsolutePath();
        String mode = args.length >= 3 ? args[2].trim().toLowerCase() : "vc";
        boolean useVectorClock = !("lockset".equals(mode) || "goldilocks".equals(mode) || "novc".equals(mode));
        reader = new Reader(path);
        if (reader.isTruncated()) {
            System.err.println("[Warning] The dat file is truncated.");
        }

        BufferedWriter outputWriter = new BufferedWriter(new FileWriter(output.toString(), false));

        Map<Long, List<EventInfo>> racyEvents = reader.getRacyEvents();
        Map<EventInfo, List<EventInfo>> racyMappings = reader.findRacyPair(racyEvents, useVectorClock);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<EventInfo, List<EventInfo>> entry : racyMappings.entrySet()) {
            EventInfo racyEvent = entry.getKey();
            sb.append(racyEvent.toCompactString(0, reader.stackReader));

            List<EventInfo> prevEvents = entry.getValue();
            for (EventInfo prevEvent : prevEvents) {
                sb.append("\t")
                .append(prevEvent.toCompactString(1, reader.stackReader));
                outputWriter.write(sb.toString());
                sb.setLength(0); // Clear the StringBuilder for the next iteration
            }
        }

        outputWriter.close();
        reader.close();
    }
}
