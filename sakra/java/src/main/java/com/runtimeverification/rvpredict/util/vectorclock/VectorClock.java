package com.runtimeverification.rvpredict.util.vectorclock;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

/**
 * A Vector of clocks. Supports clock increment, update and comparison.
 *
 */
public class VectorClock implements Comparable<VectorClock> {
    /** The result of a vector clock comparison. */
    public enum Comparison {
        BEFORE,
        AFTER,
        EQUAL,
        NOT_COMPARABLE;

        public Comparison reverse() {
            switch (ordinal()) {
                case 0:
                    return AFTER;
                case 1:
                    return BEFORE;
                case 2:
                    return EQUAL;
                default:
                    return NOT_COMPARABLE;
            }
        }

        /** Combines the effect of this comparison with a given one. */
        public Comparison and(Comparison comp) {
            if (comp == EQUAL) {
                return this;
            } else if (this == EQUAL) {
                return comp;
            } else if (comp != this) {
                return NOT_COMPARABLE;
            }
            return this;
        }
    }

    public Map<Integer, Integer> clocks;

    public VectorClock() {
        clocks = new HashMap<>();
    }

    public VectorClock(VectorClock vc) {
        if (vc == null) {
            clocks = new HashMap<>();
        } else {
            clocks = new HashMap<>(vc.clocks);
        }
    }

    private VectorClock(Map<Integer, Integer> clocks) {
        this.clocks = clocks;
    }

    public void increment(int clock) {
        clocks.merge(clock, 1, (oldValue, newValue) -> oldValue + 1);
    }

    public void update(VectorClock vc) {
        if (vc != null) {
            vc.clocks.forEach((clock, value) -> clocks.merge(clock, value, Integer::max));
        }
    }

    public Comparison compareTo2(VectorClock to) {
        if (to == null) {
            return Comparison.NOT_COMPARABLE;
        }
        if (clocks.size() > to.clocks.size()) {
            return to.compareTo2(this).reverse();
        }
        Comparison comp = Comparison.EQUAL;
        for (Map.Entry<Integer, Integer> entry : clocks.entrySet()) {
            Integer toValue = to.clocks.get(entry.getKey());
            if (toValue == null) {
                /*
                 * If we got here, it must be that clocks.size() <= to.clocks.size(), thus we
                 * expect that this <= to;
                 * however, toValue == null implies that this has a clock that to doesn't which,
                 * given the above,
                 * could only happen if they are uncomparable.
                 */
                return Comparison.NOT_COMPARABLE;
            }
            switch (Long.signum(entry.getValue().compareTo(toValue))) {
                case -1:
                    comp = comp.and(Comparison.BEFORE);
                    break;
                case 1:
                    comp = comp.and(Comparison.AFTER);
                    break;
                default:
            }
            if (comp == Comparison.NOT_COMPARABLE) {
                return Comparison.NOT_COMPARABLE;
            }
        }
        if (clocks.size() < to.clocks.size()) {
            return comp.and(Comparison.BEFORE);
        }
        return comp;
    }

    public boolean isLessThanOrEqual(VectorClock to) {
        Comparison comp = compareTo2(to);
        return comp == Comparison.BEFORE || comp == Comparison.EQUAL;
    }

    public void copyFrom(VectorClock vc) {
        clocks.clear();
        clocks.putAll(vc.clocks);
    }

    public void setToZero() {
        clocks.clear();
    }

    public VectorClock put(Integer key, Integer value) {
        clocks.put(key, value);
        return this;
    }

    public Integer get(Integer key) {
        return clocks.getOrDefault(key, 0);
    }

    /**
     * Parses a vector clock string to create a new vector clock.
     *
     * <p>E.g. {1=2, 20=10, 21=10}
     *
     * @return The newly created vector clock.
     */
    public static VectorClock fromString(String str) {
        String[] keyValuePairs = str.replaceAll("[{}]", "").split(", ");
        Map<Integer, Integer> clockMap = new HashMap<>();

        for (String pair : keyValuePairs) {
            String[] entry = pair.split("=");
            int key = Integer.parseInt(entry[0]);
            int value = Integer.parseInt(entry[1]);
            clockMap.put(key, value);
        }

        return new VectorClock(clockMap);
    }

    @Override
    public String toString() {
        return clocks.toString();
    }

    public int size() {
        return clocks.size();
    }

    public boolean isEqual(VectorClock to) {
        return compareTo2(to) == Comparison.EQUAL;
    }

    @Override
    public int compareTo(VectorClock to) {
        Comparison comp = this.compareTo2(to);
        if (comp == Comparison.EQUAL) {
            return 0;
        } else if (comp == Comparison.BEFORE) {
            return -1;
        }
        return 1;
    }

    /**
    * Converts the VectorClock to a binary format as specified.
    * @return ByteBuffer containing the binary representation of the VectorClock
    */
    public ByteBuffer toBinaryFormat() {
        int elementCount = clocks.size();
        int bufferSize = 4 + (elementCount * 8); // 4 for count, 8 for each element (4 for TID, 4 for Clock Value)

        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
        buffer.order(ByteOrder.BIG_ENDIAN); // Ensure big-endian byte order

        buffer.putInt(elementCount);

        // Each element
        for (Map.Entry<Integer, Integer> entry : clocks.entrySet()) {
            buffer.putInt(entry.getKey());
            buffer.putInt(entry.getValue());
        }

        buffer.flip(); // Prepare buffer for reading
        return buffer;
    }
}
