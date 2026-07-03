package com.runtimeverification.rvpredict.util.vectorclock;

public class SemiAdaptiveVC extends AdaptiveVC {

    public SemiAdaptiveVC() {
        super();
    }

    //  public SemiAdaptiveVC(int dim) {
    //      super(dim);
    //  }

    @Override
    public boolean isLTEUpdateWithMax(VectorClock vc, int tid) {
        boolean isLTE = isLessThanOrEqual(vc);
        if (isEpoch) {
            if (isLTE) {
                this.epoch.setClock(vc.get(tid));
                this.epoch.setThreadIndex(tid);
            } else {
                isEpoch = false;
                this.vc = new VectorClock(vc);
                // this.vc = new VectorClock();

                int oldTid = this.epoch.getThreadIndex();
                int oldClock = this.epoch.getClock();
                if (oldClock > this.vc.get(oldTid)) {
                    this.vc.put(oldTid, oldClock);
                }
                // this.vc.put(this.epoch.getThreadIndex(), this.epoch.getClock());
                // this.vc.put(tid, vc.get(tid));
            }
        } else {
            this.vc.update(vc);
            // this.vc.put(tid, vc.get(tid));
        }
        return isLTE;
    }

    public void updateWithMax(VectorClock vc, int tid) {
        boolean isLTE = isLessThanOrEqual(vc);
        if (isEpoch) {
            if (isLTE) {
                this.epoch.setClock(vc.get(tid));
                this.epoch.setThreadIndex(tid);
            } else {
                isEpoch = false;
                this.vc = new VectorClock(vc);
                // this.vc = new VectorClock();

                int oldTid = this.epoch.getThreadIndex();
                int oldClock = this.epoch.getClock();
                if (oldClock > this.vc.get(oldTid)) {
                    this.vc.put(oldTid, oldClock);
                }
                // this.vc.put(this.epoch.getThreadIndex(), this.epoch.getClock());
                // this.vc.put(tid, vc.get(tid));
            }
        } else {
            this.vc.update(vc);
            // this.vc.put(tid, vc.get(tid));
        }
    }
}
