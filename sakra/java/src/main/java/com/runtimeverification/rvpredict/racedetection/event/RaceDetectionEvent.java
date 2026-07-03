package com.runtimeverification.rvpredict.racedetection.event;

import java.nio.ByteBuffer;

import com.runtimeverification.rvpredict.racedetection.engine.State;

public abstract class RaceDetectionEvent<S extends State> extends RapidEvent {
    public RaceDetectionEvent() {
        super();
    }

    public void copyFrom(RapidEvent fromEvent) {
        super.copyFrom(fromEvent);
    }

    public void printRaceInfo(S state) {
        if (this.getType().isLockType()) {
            this.printRaceInfoLockType(state);
        } else if (this.getType().isAccessType()) {
            this.printRaceInfoAccessType(state);
        } else if (this.getType().isExtremeType()) {
            this.printRaceInfoExtremeType(state);
        }
    }

    public abstract boolean handle(S state);

    public boolean handleSub(S state) {
        boolean raceDetected = false;

        if (this.getType().isAcquire()) {
            raceDetected = this.handleSubAcquire(state);
        }
        if (this.getType().isRelease()) {
            raceDetected = this.handleSubRelease(state);
        }
        if (this.getType().isRead()) {
            raceDetected = this.handleSubRead(state);
        }
        if (this.getType().isWrite()) {
            raceDetected = this.handleSubWrite(state);
        }
        if (this.getType().isFork()) {
            raceDetected = this.handleSubFork(state);
        }
        if (this.getType().isJoin()) {
            raceDetected = this.handleSubJoin(state);
        }
        if (this.getType().isBegin()) {
            raceDetected = this.handleSubBegin(state);
        }
        if (this.getType().isEnd()) {
            raceDetected = this.handleSubEnd(state);
        }

        return raceDetected;
    }

    public abstract void printRaceInfoLockType(S state);

    public abstract void printRaceInfoAccessType(S state);

    public abstract void printRaceInfoExtremeType(S state);

    public abstract void printRaceInfoTransactionType(S state);

    public abstract boolean handleSubAcquire(S state);

    public abstract boolean handleSubRelease(S state);

    public abstract boolean handleSubRead(S state);

    public abstract boolean handleSubWrite(S state);

    public abstract boolean handleSubFork(S state);

    public abstract boolean handleSubJoin(S state);

    public abstract boolean handleSubBegin(S state);

    public abstract boolean handleSubEnd(S state);

    public abstract ByteBuffer toThreadVectorClockBytes(S state);

    /**
     * Preprocess the event. This has to be called before handle() is called.
     *
     * @param state The state to preprocess the event with.
     */
    public abstract void preProcess(S state);
}
