package com.runtimeverification.rvpredict.util.PairFinder;

import java.io.IOException;
import java.util.Objects;

import com.runtimeverification.rvpredict.util.StringStorageReader;
import com.runtimeverification.rvpredict.util.vectorclock.VectorClock;

public class EventInfo {
    public long eid;
    public int tid;
    public byte eventType;
    public long variable;
    public long locationIndex;
    public VectorClock preRaceDetectionVc;
    public VectorClock postRaceDetectionVc;
    public boolean racy;
    public long stackTraceIndex;

    private static final String STRACE_HEADER_STR = "==================Stack Trace==================";

    public EventInfo(long eid, int tid, byte eventType, long variable, long locationIndex,
            VectorClock preRaceDetectionVc, VectorClock postRaceDetectionVc, boolean racy, long stackTraceIndex) {
        this.eid = eid;
        this.tid = tid;
        this.eventType = eventType;
        this.variable = variable;
        this.locationIndex = locationIndex;
        this.preRaceDetectionVc = preRaceDetectionVc;
        this.postRaceDetectionVc = postRaceDetectionVc;
        this.racy = racy;
        this.stackTraceIndex = stackTraceIndex;
    }

    public String getStackTraceString(int indent, StringStorageReader stackReader) throws IOException {
        StringBuilder sb = new StringBuilder();
        String indentStr = "";
        for (int i = 0; i < indent; i++) {
            indentStr += "\t";
        }
        sb.append(indentStr).append(STRACE_HEADER_STR).append("\n");
        if (this.stackTraceIndex == -1) {
            return sb.toString();
        }
        String stackTrace = stackReader.readString(this.stackTraceIndex);
        String[] stackTraceStrings = stackTrace.split(" ");
        for (String s : stackTraceStrings) {
            sb.append(indentStr).append(s).append(")\n");
        }
        return sb.toString();
    }

    public String getEventTypeString() {
        return (eventType & 1) == 0 ? "R" : "W";
    }

    public String toCompactString(int indent, StringStorageReader stackReader) throws IOException {
        return String.format("E%d|T%d|%s|%s|%s|%b\n%s", eid, tid, eventType, preRaceDetectionVc.toString(),
                postRaceDetectionVc.toString(), racy, getStackTraceString(indent, stackReader));
    }

    @Override
    public String toString() {
        return "EventInfo{" +
                "eid=" + eid +
                ", tid=" + tid +
                ", eventType='" + eventType + '\'' +
                ", variable='" + variable + '\'' +
                ", location='" + locationIndex + '\'' +
                ", preVc=" + preRaceDetectionVc +
                ", postVc=" + postRaceDetectionVc +
                ", racy=" + racy +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.eid);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        EventInfo person = (EventInfo) obj;
        return eid == person.eid;
    }
}
