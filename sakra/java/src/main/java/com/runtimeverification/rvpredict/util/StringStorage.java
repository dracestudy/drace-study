package com.runtimeverification.rvpredict.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StringStorage implements AutoCloseable {
    private final Path LOCATION_TABLE_FILE;
    private final Path RAW_DATA_FILE;
    private long currentRawDataOffset;
    private long numberOfEntries;

    private RandomAccessFile locationTableRAF;
    private FileOutputStream rawDataFOS;
    private FileChannel rawDataChannel;

    public StringStorage(String logDir, String prefix) {
        this.LOCATION_TABLE_FILE = Paths.get(logDir, prefix + ".table");
        this.RAW_DATA_FILE = Paths.get(logDir, prefix + ".dat");
        this.currentRawDataOffset = 0;
        this.numberOfEntries = 0;
        try {
            initializeFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeFiles() throws IOException {
        File locationTableFile = LOCATION_TABLE_FILE.toFile();
        File rawDataFile = RAW_DATA_FILE.toFile();

        // Initialize or overwrite Files
        this.locationTableRAF = new RandomAccessFile(locationTableFile, "rw");
        this.locationTableRAF.setLength(0);
        this.locationTableRAF.writeLong(0); // Initialize number of entries to 0

        this.rawDataFOS = new FileOutputStream(rawDataFile);
        this.rawDataChannel = this.rawDataFOS.getChannel();
    }

    public long addString(String str) throws IOException {
        // Write string to Raw Data file
        ByteBuffer buffer = ByteBuffer.wrap(str.getBytes());
        this.rawDataChannel.write(buffer);

        // Update Location Table
        this.locationTableRAF.seek(0);
        this.locationTableRAF.writeLong(++numberOfEntries);
        this.locationTableRAF.seek(this.locationTableRAF.length());
        this.locationTableRAF.writeLong(currentRawDataOffset);
        this.locationTableRAF.writeInt(str.length());

        currentRawDataOffset += str.length();
        return numberOfEntries - 1;
    }

    @Override
    public void close() throws IOException {
        if (this.locationTableRAF != null) {
            this.locationTableRAF.close();
        }
        if (this.rawDataChannel != null) {
            this.rawDataChannel.close();
        }
        if (this.rawDataFOS != null) {
            this.rawDataFOS.close();
        }
    }
}
