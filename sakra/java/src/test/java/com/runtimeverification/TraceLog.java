package com.runtimeverification;

import com.runtimeverification.rvpredict.log.EventType;

// class to represent an event passed into log
public class TraceLog {
    public EventType eventType;
    public long gid;
    public long tid;
    public int locId;
    public int addr1;
    public int addr2;
    public long value;

    public TraceLog(
            EventType eventType, long gid, long tid, int locId, int addr1, int addr2, long value) {
        this.eventType = eventType;
        this.gid = gid;
        this.tid = tid;
        this.locId = locId;
        this.addr1 = addr1;
        this.addr2 = addr2;
        this.value = value;
    }
}
