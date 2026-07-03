package com.runtimeverification.rvpredict.util;

import java.util.Objects;

public class Quintet<A, B, C, D, E> {
    public A first;
    public B second;
    public C third;
    public D fourth;
    public E fifth;

    public Quintet(A first, B second, C third, D fourth, E fifth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
        this.fifth = fifth;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        Quintet<?, ?, ?, ?, ?> quintet = (Quintet<?, ?, ?, ?, ?>) other;

        return first.equals(quintet.first)
                && second.equals(quintet.second)
                && third.equals(quintet.third)
                && fourth.equals(quintet.fourth)
                && fifth.equals(quintet.fifth);
    }

    public int hashCode() {
        return Objects.hash(first, second, third, fourth, fifth);
    }

    public String toString() {
        StringBuilder str = new StringBuilder("<");
        str.append(first.toString());
        str.append(", ");
        str.append(second.toString());
        str.append(", ");
        str.append(third.toString());
        str.append(", ");
        str.append(fourth.toString());
        str.append(", ");
        str.append(fifth.toString());
        str.append(">");
        return str.toString();
    }
}
