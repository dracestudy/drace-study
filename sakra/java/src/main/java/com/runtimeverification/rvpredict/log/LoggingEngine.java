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

import java.util.concurrent.atomic.AtomicLong;

import com.runtimeverification.rvpredict.config.Configuration;
import com.runtimeverification.rvpredict.metadata.Metadata;
import com.runtimeverification.rvpredict.util.Constants;

/** General Logging engine class. */
public abstract class LoggingEngine implements ILoggingEngine, Constants {
    protected final Configuration config;
    protected final Metadata metadata;

    /** Global ID of the next event. */
    private final AtomicLong globalEventID = new AtomicLong(0);

    public LoggingEngine(Configuration config, Metadata metadata) {
        this.config = config;
        this.metadata = metadata;
    }

    @Override
    public synchronized void log(
            EventType eventType,
            int locId,
            int addr1,
            int addr2,
            long value1,
            long value2,
            int extra) {
        long tid = Thread.currentThread().getId();
        long gid;
        int atomLock;
        switch (eventType) {
            case READ:
            case WRITE:
            case WRITE_LOCK:
            case WRITE_UNLOCK:
            case READ_LOCK:
            case READ_UNLOCK:
            case WAIT_ACQUIRE:
            case WAIT_RELEASE:
            case START_THREAD:
            case JOIN_THREAD:
            case CLINIT_ENTER:
            case CLINIT_EXIT:
                gid = globalEventID.getAndIncrement();
                log(eventType, gid, tid, locId, addr1, addr2, value1);
                break;
            case INVOKE_METHOD:
            case FINISH_METHOD:
                gid = globalEventID.get();
                log(eventType, gid, tid, locId, addr1, addr2, value1);
                break;
            case ATOMIC_READ:
                gid = globalEventID.getAndAdd(3);
                atomLock = extra > 0 ? extra : addr1;
                log(EventType.WRITE_LOCK, gid, tid, locId, ATOMIC_LOCK_C, atomLock, 0);
                log(EventType.READ, gid + 1, tid, locId, addr1, addr2, value1);
                log(EventType.WRITE_UNLOCK, gid + 2, tid, locId, ATOMIC_LOCK_C, atomLock, 0);
                break;
            case ATOMIC_WRITE:
                gid = globalEventID.getAndAdd(3);
                atomLock = extra > 0 ? extra : addr1;
                log(EventType.WRITE_LOCK, gid, tid, locId, ATOMIC_LOCK_C, atomLock, 0);
                log(EventType.WRITE, gid + 1, tid, locId, addr1, addr2, value1);
                log(EventType.WRITE_UNLOCK, gid + 2, tid, locId, ATOMIC_LOCK_C, atomLock, 0);
                break;
            case ATOMIC_READ_THEN_WRITE:
                gid = globalEventID.getAndAdd(4);
                atomLock = extra > 0 ? extra : addr1;
                log(EventType.WRITE_LOCK, gid, tid, locId, ATOMIC_LOCK_C, atomLock, 0);
                log(EventType.READ, gid + 1, tid, locId, addr1, addr2, value1);
                log(EventType.WRITE, gid + 2, tid, locId, addr1, addr2, value2);
                log(EventType.WRITE_UNLOCK, gid + 3, tid, locId, ATOMIC_LOCK_C, atomLock, 0);
                break;
            default:
                assert false;
        }
    }

    protected abstract void log(
            EventType eventType, long gid, long tid, int locId, int addr1, int addr2, long value);
}
