package com.runtimeverification.rvpredict.util.vectorclock;

import java.util.Vector;

public class VectorClockOpt {

    private int dim;
    private Vector<Integer> clock;

    public VectorClockOpt(int dim) {
        this.dim = dim;
        this.clock = new Vector<Integer>(dim);
        for (int ind = 0; ind < this.dim; ind++) {
            this.clock.addElement(0);
        }
    }

    public VectorClockOpt(VectorClockOpt fromVectorClock) {
        this.dim = fromVectorClock.getDim();
        this.clock = new Vector<Integer>(dim);
        Vector<Integer> fromClock = fromVectorClock.getClock();
        for (int ind = 0; ind < fromVectorClock.getDim(); ind++) {
            this.clock.addElement((Integer) fromClock.get(ind));
        }
    }

    public int getDim() {
        return this.dim;
    }

    public Vector<Integer> getClock() {
        return this.clock;
    }

    public String toString() {
        return this.clock.toString();
    }

    public boolean isZero() {
        boolean itIsZero = true;
        for (int ind = 0; ind < this.dim; ind++) {
            int thisVal = this.clock.get(ind).intValue();
            if (thisVal != 0) {
                itIsZero = false;
                break;
            }
        }
        return itIsZero;
    }

    public boolean isEqual(VectorClockOpt vc) {
        boolean itIsEqual = true;
        Vector<Integer> vcClock = vc.getClock();
        for (int ind = 0; ind < this.dim; ind++) {
            int thisVal = this.clock.get(ind).intValue();
            int vcVal = vcClock.get(ind).intValue();
            if (thisVal != vcVal) {
                itIsEqual = false;
                break;
            }
        }
        return itIsEqual;
    }

    public boolean isLessThan(VectorClockOpt vc) {
        boolean oneComponentIsLess = false;
        boolean isLessThanOrEqual = true;
        Vector<Integer> vcClock = vc.getClock();
        for (int ind = 0; ind < this.dim; ind++) {
            int thisVal = this.clock.get(ind).intValue();
            int vcVal = vcClock.get(ind).intValue();
            if (thisVal > vcVal) {
                isLessThanOrEqual = false;
                break;
            } else if (thisVal < vcVal) {
                oneComponentIsLess = true;
            }
        }
        return oneComponentIsLess && isLessThanOrEqual;
    }

    public boolean isLessThanOrEqual(VectorClockOpt vc) {
        boolean itIsLessThanOrEqual = true;
        Vector<Integer> vcClock = vc.getClock();
        for (int ind = 0; ind < this.dim; ind++) {
            int thisVal = this.clock.get(ind).intValue();
            int vcVal = vcClock.get(ind).intValue();
            if (!(thisVal <= vcVal)) {
                itIsLessThanOrEqual = false;
                break;
            }
        }
        return itIsLessThanOrEqual;
    }

    public boolean isLessThanOrEqual(VectorClockOpt vc, int ind) {
        return this.clock.get(ind).intValue() <= vc.getClock().get(ind).intValue();
    }

    public void setToZero() {
        for (int ind = 0; ind < this.dim; ind++) {
            this.clock.set(ind, (Integer) 0);
        }
    }

    public void copyFrom(VectorClockOpt vc) {
        for (int ind = 0; ind < this.dim; ind++) {
            this.clock.set(ind, (Integer) vc.clock.get(ind));
        }
    }

    private void updateMax2(VectorClockOpt vc) {
        for (int ind = 0; ind < this.dim; ind++) {
            int thisClock = this.clock.get(ind);
            int vcClock = vc.clock.get(ind);
            int maxClock = thisClock > vcClock ? thisClock : vcClock;
            this.clock.set(ind, (Integer) maxClock);
        }
    }

    /**
     * Updates the current vector clock by taking the component-wise maximum with the specified vector clock (vc),
     * excluding the local entry at the given thread index (thIndex).
     *
     * <p>The method iterates through each component of the vector clocks, excluding the local entry at the
     * specified thread index. For each component, it compares the corresponding components of the current
     * vector clock and the specified vector clock. The component-wise maximum is taken, and the current vector
     * clock is updated with the result.
     *
     * <p>The local entry at the specified thread index is not updated, and its value remains unchanged.
     *
     * @param vc The vector clock to merge with the current vector clock.
     * @param thIndex The thread index for which the local entry should not be updated.
     */
    public void updateMax2WithoutLocal(VectorClockOpt vc, int thIndex) {
        for (int ind = 0; ind < this.dim; ind++) {
            if (ind != thIndex) {
                int thisClock = this.clock.get(ind);
                int vcClock = vc.clock.get(ind);
                int maxClock = thisClock > vcClock ? thisClock : vcClock;
                this.clock.set(ind, (Integer) maxClock);
            }
        }
    }

    public void updateWithMax(VectorClockOpt... vcList) {
        this.copyFrom(vcList[0]);
        for (int i = 1; i < vcList.length; i++) {
            VectorClockOpt vc = vcList[i];
            this.updateMax2(vc);
        }
    }

    private void updateMin2(VectorClockOpt vc) {
        for (int ind = 0; ind < this.dim; ind++) {
            int thisClock = this.clock.get(ind);
            int vcClock = vc.clock.get(ind);
            int maxClock = thisClock < vcClock ? thisClock : vcClock;
            this.clock.set(ind, (Integer) maxClock);
        }
    }

    public void updateWithMin(VectorClockOpt... vcList) {
        this.copyFrom(vcList[0]);
        for (int i = 1; i < vcList.length; i++) {
            VectorClockOpt vc = vcList[i];
            this.updateMin2(vc);
        }
    }

    public int getClockIndex(int thIndex) {
        return this.clock.get(thIndex);
    }

    public void setClockIndex(int thIndex, int thValue) {
        this.clock.set(thIndex, (Integer) thValue);
    }
}
