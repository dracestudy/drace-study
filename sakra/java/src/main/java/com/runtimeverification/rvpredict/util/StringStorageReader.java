package com.runtimeverification.rvpredict.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StringStorageReader implements AutoCloseable {
    private final Path LOCATION_TABLE_FILE;
    private final Path RAW_DATA_FILE;

    private RandomAccessFile locationTableRAF;
    private RandomAccessFile rawDataRAF;

    public StringStorageReader(String logDir, String prefix) throws IOException {
        this.LOCATION_TABLE_FILE = Paths.get(logDir, prefix + ".table");
        this.RAW_DATA_FILE = Paths.get(logDir, prefix + ".dat");
        loadFiles();
    }

    private void loadFiles() throws IOException {
        File locationTableFile = LOCATION_TABLE_FILE.toFile();
        File rawDataFile = RAW_DATA_FILE.toFile();

        this.locationTableRAF = new RandomAccessFile(locationTableFile, "r");
        this.rawDataRAF = new RandomAccessFile(rawDataFile, "r");
    }

    public String readString(long index) throws IOException {
        // Read Location Table
        // skip first 8 bytes as that is the total number of entries
        // 8 bytes for long, 4 bytes for int -> 12 bytes per entry
        this.locationTableRAF.seek(8 + index * 12);
        long rawDataOffset = this.locationTableRAF.readLong();
        int strLength = this.locationTableRAF.readInt();

        // Read String from Raw Data file
        this.rawDataRAF.seek(rawDataOffset);
        byte[] strBytes = new byte[strLength];
        this.rawDataRAF.read(strBytes);
        return new String(strBytes);
    }

    @Override
    public void close() throws IOException {
        if (this.locationTableRAF != null) {
            this.locationTableRAF.close();
        }
        if (this.rawDataRAF != null) {
            this.rawDataRAF.close();
        }
    }
}
