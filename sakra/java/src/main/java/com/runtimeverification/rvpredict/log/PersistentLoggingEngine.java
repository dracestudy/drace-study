/*******************************************************************************
 * Copyright (c) 2013 University of Illinois
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package com.runtimeverification.rvpredict.log;

import com.runtimeverification.rvpredict.config.Configuration;
import com.runtimeverification.rvpredict.metadata.Metadata;
import com.runtimeverification.rvpredict.util.Constants;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Logging engine that saves events and metadata to disk.
 *
 *
 */
public class PersistentLoggingEngine extends LoggingEngine implements ILoggingEngine, Constants {

    private volatile boolean shutdown = false;

    private final List<EventWriter> eventWriters = new ArrayList<>();

    private final ThreadLocalEventWriter threadLocalEventWriter = new ThreadLocalEventWriter();

    public PersistentLoggingEngine(Configuration config, Metadata metadata) {
        super(config, metadata);
    }

    @Override
    public void finishLogging() throws IOException {
	System.out.println("metadata");
        shutdown = true;

        synchronized (eventWriters) {
            for (EventWriter writer : eventWriters) {
                writer.close();
            }
        }

        try (ObjectOutputStream os = getMetadataOS()) {
            os.writeObject(metadata);
        }
    }

    private ObjectOutputStream getMetadataOS() throws IOException {
        return new ObjectOutputStream(LZ4Utils.createCompressionStream(super.config.getMetadataPath()));
    }

    @Override
    public void resetAnalysis() {}

    @Override
    protected void log(EventType eventType, long gid, long tid, int locId, int addr1, int addr2,
            long value) {
        EventWriter writer = threadLocalEventWriter.get();
        if (writer != null) {
            try {
                writer.write(gid, tid, locId, (long) addr1 << 32 | addr2 & 0xFFFFFFFFL, value, eventType);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private class ThreadLocalEventWriter extends ThreadLocal<EventWriter> {
        @Override
        protected EventWriter initialValue() {
            synchronized (eventWriters) {
                if (shutdown) {
                    System.err.printf("[Warning] JVM exits before %s finishes;"
                            + " no trace from this thread is logged.%n",
                            Thread.currentThread().getName());
                    return null;
                } else {
                    try {
                        Path path = config.getTraceFilePath(eventWriters.size());
                        EventWriter eventWriter = new EventWriter(path);
                        eventWriters.add(eventWriter);
                        return eventWriter;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
       }
    }

}
