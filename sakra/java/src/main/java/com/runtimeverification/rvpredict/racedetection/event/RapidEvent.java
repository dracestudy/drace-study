package com.runtimeverification.rvpredict.racedetection.event;

import java.util.HashSet;
import java.util.regex.Pattern;

import com.runtimeverification.rvpredict.config.Configuration;
import com.runtimeverification.rvpredict.metadata.MetadataInterface;

public class RapidEvent {
    // Data for Event
    protected Long id; // RV event identifier
    protected int locId; // location Id given by RV
    protected RapidEventType type;
    protected long thread; // thread id
    protected boolean isVolatile;
    protected boolean toIgnore;

    // Data for Acquire/Release
    protected long lock; // lock address/hash code
    protected HashSet<Long> readVarSet;
    protected HashSet<Long> writeVarSet;

    // Data for Read/Write
    protected long variable;
    protected HashSet<Long> lockSet;

    // Data for Fork/Join
    protected long target;

    // Data for stack trace
    protected StackTraceElement[] stackTrace;

    public RapidEvent() {
        this.updateEvent();
    }

    public RapidEvent(
            Long auxId, int locId, RapidEventType tp, long th, Long lock, Long var, long tar, boolean vol, boolean cl) {
        this.updateEvent(auxId, locId, tp, th, lock, var, tar, vol, cl);
    }

    public void updateEventLockType(long lock) {
        if (this.getType().isLockType()) {
            this.readVarSet = new HashSet<Long>();
            this.writeVarSet = new HashSet<Long>();
            this.lock = lock;
        }
    }

    public void updateEventAccessType(long var) {
        if (this.getType().isAccessType()) {
            lockSet = new HashSet<Long>();
            this.variable = var;
        }
    }

    public void updateEventExtremeType(long tar) {
        if (this.getType().isExtremeType()) {
            this.target = tar;
        }
    }

    public void updateEvent(long auxId, int locId, RapidEventType tp, long th, boolean vol, boolean ti) {
        this.id = auxId;
        this.locId = locId;
        this.type = tp;
        this.thread = th;
        this.isVolatile = vol;
        this.toIgnore = ti;
    }

    public void updateEvent(long auxId, int locId, RapidEventType tp, long th, StackTraceElement[] stackTrace,
            boolean vol, boolean cl) {
        this.updateEvent(auxId, locId, tp, th, vol, cl);
        this.stackTrace = stackTrace;
    }

    public void updateEvent(
            Long auxId, int locId, RapidEventType tp, long th, Long lock, Long var, long tar, boolean vol, boolean cl) {
        this.updateEvent(auxId, locId, tp, th, vol, cl);

        // Acquire/Release
        this.updateEventLockType(lock);

        // Read/Write
        this.updateEventAccessType(var);

        // Fork/Join
        this.updateEventExtremeType(tar);
    }

    public void updateEvent(
            Long auxId, int locId, RapidEventType tp, long th, Long lock, Long var, long tar,
            StackTraceElement[] stackTrace, boolean vol, boolean cl) {
        this.updateEvent(auxId, locId, tp, th, lock, var, tar, vol, cl);
        this.stackTrace = stackTrace;
    }

    public void updateEvent() {
        this.updateEvent(0L, 0, RapidEventType.DUMMY, 0L, null, false, false);
    }

    public void copyFrom(RapidEvent fromEvent) {
        this.id = fromEvent.getId();
        this.locId = fromEvent.getLocId();
        this.type = fromEvent.getType();
        this.thread = fromEvent.getThread();
        this.stackTrace = fromEvent.getStackTrace();
        this.isVolatile = fromEvent.isVolatile();
        this.toIgnore = fromEvent.toIgnore();

        // Data for Acquire/Release
        if (this.getType().isLockType()) {
            this.lock = fromEvent.getLock();
            this.setReadVarSet(fromEvent.getReadVarSet());
            this.setWriteVarSet(fromEvent.getWriteVarSet());
        }

        // Data for Read/Write
        if (this.getType().isAccessType()) {
            this.variable = fromEvent.getVariable();
            this.setLockSet(fromEvent.getLockSet());
        }

        // Data for Fork/Join
        if (this.getType().isExtremeType()) {
            this.target = fromEvent.getTarget();
        }
    }

    public String getStackTraceString(Configuration config) {
        if (stackTrace == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        if (config != null && config.patternExcludeList != null) {
            // first 6 elements are from rvpredict instrumentation
            for (int i = 6; i < stackTrace.length; i++) {
                boolean exclude = false;
                String stackStr = stackTrace[i].toString();
                for (Pattern pack : config.patternExcludeList) {
                    exclude = pack.matcher(stackStr).matches();
                    if (exclude) {
                        break;
                    }
                }
                if (exclude) {
                    continue;
                }
                sb.append(stackStr + " ");
            }
        }
        return sb.toString();
    }

    public StackTraceElement[] getStackTrace() {
        return stackTrace;
    }

    public boolean isVolatile() {
        return isVolatile;
    }

    public boolean toIgnore() {
        return toIgnore;
    }

    public Long getId() {
        return this.id;
    }

    public int getLocId() {
        return this.locId;
    }

    public RapidEventType getType() {
        return type;
    }

    public void setType(RapidEventType tp) {
        this.type = tp;
    }

    public long getThread() {
        return thread;
    }

    public String toString() {
        return "(Event"
                + "-"
                + Long.toString(this.id)
                + "-L"
                + Integer.toString(this.locId)
                + "-"
                + this.type.toString()
                + " -T"
                + Long.toString(this.thread)
                + ")";
    }

    public String toFullStringForChildren() {
        return "(Event"
                + "-"
                + Long.toString(this.id)
                + "-L"
                + Integer.toString(this.locId)
                + "-"
                + this.type.toString()
                + " -T"
                + Long.toString(this.thread);
    }

    public String toFullString() {
        String str = "";

        if (this.getType().isLockType()) {
            str = this.toFullStringLockType();
        }
        if (this.getType().isAccessType()) {
            str = this.toFullStringAccessType();
        }
        if (this.getType().isExtremeType()) {
            str = this.toFullStringExtremeType();
        }

        return str;
    }

    public String toPrototypeString() {
        String str = "";

        if (this.getType().isLockType()) {
            str = this.toPrototypeStringLockType();
        }
        if (this.getType().isAccessType()) {
            str = this.toPrototypeStringAccessType();
        }
        if (this.getType().isExtremeType()) {
            str = this.toPrototypeStringExtremeType();
        }

        return str;
    }

    public String toCompactString() {
        String str = "";
        str = str + String.valueOf(this.getThread());
        str = str + ",";

        if (this.getType().isAccessType()) {
            if (this.getType().isRead()) {
                str = str + "R";
            } else if (this.getType().isWrite()) {
                str = str + "W";
            }
            str = str + ",";
            str = str + String.valueOf(this.getVariable());
        } else if (this.getType().isLockType()) {
            if (this.getType().isAcquire()) {
                str = str + "L";
            } else if (this.getType().isRelease()) {
                str = str + "U";
            }
            str = str + ",";
            str = str + String.valueOf(this.getLock());
        } else if (this.getType().isExtremeType()) {
            if (this.getType().isFork()) {
                str = str + "F";
            } else if (this.getType().isJoin()) {
                str = str + "J";
            }
            str = str + ",";
            str = str + String.valueOf(this.getTarget());
        }

        return str;
    }

    public String toRaceReportString(MetadataInterface metadata) {
        StringBuilder output = new StringBuilder();
        String sep = "|";
        output.append(String.format("E%d", this.id));
        output.append(sep);
        output.append(String.format("T%d", this.getThread()));
        output.append(sep);

        if (this.getType().isAccessType()) {
            if (this.getType().isRead()) {
                output.append("R");
            } else if (this.getType().isWrite()) {
                output.append("W");
            }
            output.append("(");
            output.append(String.format("%016X", this.getVariable()));
            output.append(")");
        } else if (this.getType().isLockType()) {
            if (this.getType().isAcquire()) {
                output.append("L");
            } else if (this.getType().isRelease()) {
                output.append("U");
            }
            output.append("(");
            output.append(String.format("%016X", this.getLock()));
            output.append(")");
        } else if (this.getType().isExtremeType()) {
            if (this.getType().isFork()) {
                output.append("F");
            } else if (this.getType().isJoin()) {
                output.append("J");
            }
            output.append("(");
            output.append(String.format("%d", this.getTarget()));
            output.append(")");
        }
        output.append(sep);
        output.append(metadata.getLocationSig(this.getLocId()));

        return output.toString();
    }

    // ************** Acquire/Release *******************
    public Long getLock() {
        if (!this.getType().isLockType()) {
            throw new IllegalArgumentException(
                    "Illegal operation getLock() for EventType " + this.getType().toString());
        }
        return this.lock;
    }

    public HashSet<Long> getReadVarSet() {
        if (!this.getType().isLockType()) {
            throw new IllegalArgumentException(
                    "Illegal operation getReadVarSet() for EventType " + this.getType().toString());
        }
        return readVarSet;
    }

    public HashSet<Long> getWriteVarSet() {
        if (!this.getType().isLockType()) {
            throw new IllegalArgumentException(
                    "Illegal operation getWriteVarSet() for EventType "
                            + this.getType().toString());
        }
        return writeVarSet;
    }

    public void setReadVarSet(HashSet<Long> readSet) {
        if (!this.getType().isLockType()) {
            throw new IllegalArgumentException(
                    "Illegal operation setReadVarSet() for EventType " + this.getType().toString());
        }
        // You have to do a deep copy here
        this.readVarSet = new HashSet<Long>(readSet);
    }

    public void setWriteVarSet(HashSet<Long> writeSet) {
        if (!this.getType().isLockType()) {
            throw new IllegalArgumentException(
                    "Illegal operation setWriteVarSet() for EventType "
                            + this.getType().toString());
        }
        // You have to do a deep copy here
        this.writeVarSet = new HashSet<Long>(writeSet);
    }

    public void addReadVariable(Long var) {
        if (!this.getType().isLockType()) {
            throw new IllegalArgumentException(
                    "Illegal operation addReadVariable() for EventType "
                            + this.getType().toString());
        }
        readVarSet.add(var);
    }

    public void addWriteVariable(Long var) {
        if (!this.getType().isLockType()) {
            throw new IllegalArgumentException(
                    "Illegal operation addWriteVariable() for EventType "
                            + this.getType().toString());
        }
        writeVarSet.add(var);
    }

    public String toFullStringLockType() {
        return toFullStringForChildren()
                + "-"
                + this.getLock().toString()
                + "-"
                + "readVars="
                + this.getReadVarSet().toString()
                + "-"
                + "writeVars="
                + this.getWriteVarSet().toString()
                + ")";
    }

    public String toPrototypeStringLockType() {
        return "@  Acquire(T"
                + String.valueOf(this.getThread())
                + ",L"
                + String.valueOf(this.getLock())
                + ")";
    }

    public String toStandardFormat() {
        String sensibleStr = String.valueOf(this.getThread());
        sensibleStr = sensibleStr + "|" + this.getType().toStandardFormat();
        if (this.getType().isAccessType()) {
            sensibleStr = sensibleStr + "(" + String.valueOf(this.getVariable()) + ")";
        } else if (this.getType().isLockType()) {
            sensibleStr = sensibleStr + "(" + String.valueOf(this.getLock()) + ")";
        } else if (this.getType().isExtremeType()) {
            sensibleStr = sensibleStr + "(" + String.valueOf(this.getTarget()) + ")";
        }
        sensibleStr = sensibleStr + "|" + this.getLocId();
        return sensibleStr;
    }
    // **************************************************

    // **************** Read/Write **********************
    public Long getVariable() {
        if (!this.getType().isAccessType()) {
            throw new IllegalArgumentException(
                    "Illegal operation getVariable() for EventType " + this.getType().toString());
        }
        return this.variable;
    }

    public HashSet<Long> getLockSet() {
        if (!this.getType().isAccessType()) {
            throw new IllegalArgumentException(
                    "Illegal operation getLockSet() for EventType " + this.getType().toString());
        }
        return this.lockSet;
    }

    public void setLockSet(HashSet<Long> lockSet) {
        if (!this.getType().isAccessType()) {
            throw new IllegalArgumentException(
                    "Illegal operation setLockSet() for EventType " + this.getType().toString());
        }
        // Do a deep copy here
        this.lockSet = new HashSet<Long>(lockSet);
    }

    public String toFullStringAccessType() {
        return toFullStringForChildren()
                + "-"
                + this.getVariable().toString()
                + "-"
                + "lockSet="
                + this.getLockSet()
                + ")";
    }

    public String toPrototypeStringAccessType() {
        return "@  Rd(T"
                + String.valueOf(this.getThread())
                + ",V"
                + String.valueOf(this.getVariable())
                + ")";
    }

    // **************************************************

    // ***************** Fork/Join **********************
    public long getTarget() {
        if (!this.getType().isExtremeType()) {
            throw new IllegalArgumentException(
                    "Illegal operation getTarget() for EventType " + this.getType().toString());
        }
        return this.target;
    }

    public String toFullStringExtremeType() {
        return toFullStringForChildren() + "-" + String.valueOf(this.getTarget()) + ")";
    }

    public String toPrototypeStringExtremeType() {
        return "@  Start(T"
                + String.valueOf(this.getThread())
                + ",T"
                + String.valueOf(this.getTarget())
                + ")";
    }
}
