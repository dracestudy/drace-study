package com.runtimeverification.rvpredict.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.runtimeverification.rvpredict.util.PairFinder.Reader;

public class ReadPairInfo {
    private static Reader reader;

     public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.err.println("Usage " + PairInfo.class.getName() + " <log_file_name> <output_file_name>");
            System.exit(1);
        }
        Path path = Paths.get(args[0]).toAbsolutePath();
        Path output = Paths.get(args[1]).toAbsolutePath();
        BufferedWriter writer = new BufferedWriter(new FileWriter(output.toString(), false));
        reader = new Reader(path);
        reader.printEvents(writer);
        reader.close();
        writer.close();
     }
}

