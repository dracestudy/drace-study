package com.runtimeverification.rvpredict.order;

import com.google.common.annotations.VisibleForTesting;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

/**
 * A Vector of clocks. Supports clock increment, update and comparison.
 *
 */
public class VectorClock {
  /**
   * The result of a vector clock comparison.
   */
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
      }
      return NOT_COMPARABLE;
    }

    /**
     * Combines the effect of this comparison with a given one.
     */
    public Comparison and(Comparison c) {
      if (c == EQUAL)
        return this;
      if (this == EQUAL)
        return c;
      if (c != this)
        return NOT_COMPARABLE;
      return this;
    }
  }

  @VisibleForTesting
  public Map<Integer, Integer> clocks;

  public VectorClock() {
    clocks = new HashMap<>();
  }

  public VectorClock(VectorClock c) {
    if (c == null) {
      clocks = new HashMap<>();
    } else {
      clocks = new HashMap<>(c.clocks);
    }
  }

  public void increment(int clock) {
    clocks.merge(clock, 1, (oldValue, newValue) -> oldValue + 1);
  }

  public void update(VectorClock c) {
    if (c != null) {
      c.clocks.forEach((clock, value) -> clocks.merge(clock, value, Integer::max));
    }
  }

  public Comparison compareTo(VectorClock to) {
    if (to == null)
      return Comparison.NOT_COMPARABLE;
    if (clocks.size() > to.clocks.size())
      return to.compareTo(this).reverse();
    Comparison c = Comparison.EQUAL;
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
          c = c.and(Comparison.BEFORE);
          break;
        case 1:
          c = c.and(Comparison.AFTER);
          break;
      }
      if (c == Comparison.NOT_COMPARABLE)
        return Comparison.NOT_COMPARABLE;
    }
    if (clocks.size() < to.clocks.size())
      return c.and(Comparison.BEFORE);
    return c;
  }

  public boolean isLessThanOrEqual(VectorClock to) {
    Comparison comp = compareTo(to);
    return comp == Comparison.BEFORE || comp == Comparison.EQUAL;
  }

  public void copyFrom(VectorClock vc) {
    clocks.clear();
    clocks.putAll(vc.clocks);
  }

  @VisibleForTesting
  public VectorClock put(Integer k, Integer v) {
    clocks.put(k, v);
    return this;
  }

  @VisibleForTesting
  public Integer get(Integer k) {
    // return clocks.get(k);
    return clocks.getOrDefault(k, 0);
  }

  @Override
  public String toString() {
    return clocks.toString();
  }

  public int size() {
    return clocks.size();
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
