package com.runtimeverification.rvpredict.util.vectorclock;

public class FullAdaptiveVC extends AdaptiveVC {

    public FullAdaptiveVC() {
        super();
    }

    //  public FullAdaptiveVC(int dim) {
    //      super(dim);
    //  }

    @Override
    public boolean isLTEUpdateWithMax(VectorClock vc, int tid) {
        boolean isLTE = isLessThanOrEqual(vc);
        if (isLTE) {
            this.isEpoch = true;
            this.vc = null;
            this.epoch.setClock(vc.get(tid));
            this.epoch.setThreadIndex(tid);
        } else {
            if (isEpoch) {
                isEpoch = false;
                this.vc = new VectorClock(vc);
                // this.vc = new VectorClock();

                int oldTid = this.epoch.getThreadIndex();
                int oldClock = this.epoch.getClock();
                if (oldClock > this.vc.get(oldTid)) {
                    this.vc.put(oldTid, oldClock);
                }
                // this.vc.put(this.epoch.getThreadIndex(), this.epoch.getClock());
            } else {
                this.vc.update(vc);
                // this.vc.put(tid, vc.get(tid));
            }
        }
        return isLTE;
    }
}
