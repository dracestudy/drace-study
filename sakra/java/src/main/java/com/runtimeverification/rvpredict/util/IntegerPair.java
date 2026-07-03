package com.runtimeverification.rvpredict.util;

public class IntegerPair {

    private final int first;
    private final int second;

    public IntegerPair(int first, int second) {
        if (first <= second) {
            this.first = first;
            this.second = second;
        } else {
            this.first = second;
            this.second = first;
        }
    }

    public int getFirst() {
        return this.first;
    }

    public int getSecond() {
        return this.second;
    }

    @Override
    public String toString() {
        return "<" + Integer.toString(this.first) + "|" + Integer.toString(this.second) + ">";
    }

    @Override
    public boolean equals(Object other) {
        boolean result = false;
        if (other instanceof IntegerPair) {
            IntegerPair that = (IntegerPair) other;
            result = (this.getFirst() == that.getFirst() && this.getSecond() == that.getSecond());
        }
        return result;
    }

    @Override
    public int hashCode() {
        return (41 * (41 + getFirst()) + getSecond());
    }
}

