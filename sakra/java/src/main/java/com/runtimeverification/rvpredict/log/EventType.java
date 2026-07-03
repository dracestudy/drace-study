package com.runtimeverification.rvpredict.log;

import com.runtimeverification.rvpredict.log.printers.DataAccessPrinter;
import com.runtimeverification.rvpredict.log.printers.EstablishSignalPrinter;
import com.runtimeverification.rvpredict.log.printers.InvokeMethodPrinter;
import com.runtimeverification.rvpredict.log.printers.LockPrinter;
import com.runtimeverification.rvpredict.log.printers.ReadWriteSignalMaskPrinter;
import com.runtimeverification.rvpredict.log.printers.SignalHandlerPrinter;
import com.runtimeverification.rvpredict.log.printers.SignalMaskPrinter;
import com.runtimeverification.rvpredict.log.printers.SignalNumberPrinter;
import com.runtimeverification.rvpredict.log.printers.ThreadEventPrinter;
import com.runtimeverification.rvpredict.racedetection.event.RapidEventType;

import java.util.concurrent.locks.Condition;

/**
 * Enumeration of all types of events considered during logging and prediction.
 *
 */
public enum EventType {
    READ(new DataAccessPrinter("read"), RapidEventType.READ),
    WRITE(new DataAccessPrinter("write"), RapidEventType.WRITE),

    /**
     * Atomic events that are used only in the front-end.
     */
    ATOMIC_READ(new DataAccessPrinter("atomicread"), RapidEventType.DUMMY),
    ATOMIC_WRITE(new DataAccessPrinter("atomicwrite"), RapidEventType.DUMMY),
    ATOMIC_READ_THEN_WRITE(new DataAccessPrinter("atomicrw"), RapidEventType.DUMMY),

    /**
     * Event generated after acquiring an intrinsic lock or write lock.
     */
    WRITE_LOCK(new LockPrinter("writelock"), RapidEventType.ACQUIRE),

    /**
     * Event generated before releasing an intrinsic lock or write lock.
     */
    WRITE_UNLOCK(new LockPrinter("writeunlock"), RapidEventType.RELEASE),

    /**
     * Event generated after acquiring a read lock, i.e.,
     * {@code ReadWriteLock#readLock()#lock()}.
     */
    READ_LOCK(new LockPrinter("readlock"), RapidEventType.ACQUIRE),

    /**
     * Event generated before releasing a read lock, i.e.,
     * {@code ReadWriteLock#readLock()#unlock()}.
     */
    READ_UNLOCK(new LockPrinter("readunlock"), RapidEventType.RELEASE),

    /**
     * Event generated before calling {@link Object#wait()} or
     * {@link Condition#await()}.
     */
    WAIT_RELEASE(new LockPrinter("waitrelease"), RapidEventType.DUMMY),

    /**
     * Event generated after a thread is awakened from {@link Object#wait()} or
     * {@link Condition#await()} for whatever reason (e.g., spurious wakeup,
     * being notified, or being interrupted).
     */
    WAIT_ACQUIRE(new LockPrinter("waitacquire"), RapidEventType.DUMMY),

    /**
     * Event generated before calling {@code Thread#start()}.
     */
    START_THREAD(new ThreadEventPrinter("startthread"), RapidEventType.FORK),

    /**
     * Event generated after a thread is awakened from {@code Thread#join()}
     * because the joining thread finishes.
     */
    JOIN_THREAD(new ThreadEventPrinter("jointhread"), RapidEventType.JOIN),

    /**
     * Event generated after entering the class initializer code, i.e.
     * {@code <clinit>}.
     */
    CLINIT_ENTER(new EventPrinter("classinitenter"), RapidEventType.DUMMY),

    /**
     * Event generated right before exiting the class initializer code, i.e.
     * {@code <clinit>}.
     */
    CLINIT_EXIT(new EventPrinter("classinitexit"), RapidEventType.DUMMY),

    INVOKE_METHOD(new InvokeMethodPrinter(), RapidEventType.DUMMY),

    FINISH_METHOD(new EventPrinter("finishmethod"), RapidEventType.DUMMY),

    /**
     * Event generated before acquiring of any type of lock is attempted.
     * Required by, and only used for, deadlock detection, where the intention
     * to acquire a lock is more relevant than actually the acquisition itself.
     */
    PRE_LOCK(new EventPrinter("prelock"), RapidEventType.DUMMY),


    BEGIN_THREAD(new EventPrinter("beginthread"), RapidEventType.BEGIN),
    END_THREAD(new EventPrinter("endthread"), RapidEventType.END),

    ESTABLISH_SIGNAL(new EstablishSignalPrinter("establishsignal"), RapidEventType.DUMMY),
    DISESTABLISH_SIGNAL(new SignalNumberPrinter("disestablishsignal"), RapidEventType.DUMMY),
    WRITE_SIGNAL_MASK(new SignalMaskPrinter("writesignalmask", ReadonlyEventInterface::getFullWriteSignalMask), RapidEventType.DUMMY),
    READ_SIGNAL_MASK(new SignalMaskPrinter("readsignalmask", ReadonlyEventInterface::getFullReadSignalMask), RapidEventType.DUMMY),
    READ_WRITE_SIGNAL_MASK(new ReadWriteSignalMaskPrinter("readwritesignalmask"), RapidEventType.DUMMY),
    BLOCK_SIGNALS(new SignalMaskPrinter("blocksignals", ReadonlyEventInterface::getPartialSignalMask), RapidEventType.DUMMY),
    UNBLOCK_SIGNALS(new SignalMaskPrinter("unblocksignals", ReadonlyEventInterface::getPartialSignalMask), RapidEventType.DUMMY),

    ENTER_SIGNAL(new SignalHandlerPrinter("entersignal"), RapidEventType.DUMMY),
    EXIT_SIGNAL(new SignalNumberPrinter("exitsignal"), RapidEventType.DUMMY);

    private final EventPrinter printer;
    private final RapidEventType rapidEventType;

    EventType(EventPrinter printer, RapidEventType rapidEventType) {
        this.printer = printer;
        this.rapidEventType = rapidEventType;
    }

    public boolean isSyncType() {
        return (WRITE_LOCK.ordinal() <= this.ordinal() && this.ordinal() <= JOIN_THREAD.ordinal())
                || this == PRE_LOCK
                || (BEGIN_THREAD.ordinal() <= this.ordinal() && this.ordinal() <= END_THREAD.ordinal());
    }

    public boolean isMetaType() {
        return CLINIT_ENTER.ordinal() <= this.ordinal() && this.ordinal() <= FINISH_METHOD.ordinal();
    }

    public boolean isSignalType() {
        return ESTABLISH_SIGNAL.ordinal() <= this.ordinal() && this.ordinal() <= EXIT_SIGNAL.ordinal();
    }

    public EventPrinter getPrinter() {
        return printer;
    }

    public RapidEventType getRapidEventType() {
        return rapidEventType;
    }
}
