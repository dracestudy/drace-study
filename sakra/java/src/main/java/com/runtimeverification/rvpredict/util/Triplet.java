package com.runtimeverification.rvpredict.util;

import java.util.Objects;

public class Triplet<A, B, C> {
    public A first;
    public B second;
    public C third;

    public Triplet(A first, B second, C third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        Triplet<?, ?, ?> triplet = (Triplet<?, ?, ?>) other;

        return first.equals(triplet.first)
                && second.equals(triplet.second)
                && third.equals(triplet.third);
    }

    public int hashCode() {
        return Objects.hash(first, second, third);
    }

    public String toString() {
        StringBuilder str = new StringBuilder("<");
        str.append(first.toString());
        str.append(", ");
        str.append(second.toString());
        str.append(", ");
        str.append(third.toString());
        str.append(">");
        return str.toString();
    }
}
