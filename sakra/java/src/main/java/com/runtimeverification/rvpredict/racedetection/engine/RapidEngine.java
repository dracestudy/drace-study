package com.runtimeverification.rvpredict.racedetection.engine;

import com.runtimeverification.rvpredict.racedetection.event.RapidEvent;

public abstract class RapidEngine<E extends RapidEvent> {
    // protected ParserType parserType;
    // protected Trace trace; // CSV
    // protected ParseRVPredict rvParser;// RV
    // protected ParseStandard stdParser; // STD
    // protected ParseRoadRunner rrParser; // RR
    protected E handlerEvent;

    /*
     * ALl these methods use a reader, which we don't need right now
     *
     * public Engine(ParserType pType) {
     * this.parserType = pType;
     * }
     *
     * protected void initializeReader(String trace_folder) {
     * if (this.parserType.isRV()) {
     * initializeReaderRV(trace_folder);
     * } else if (this.parserType.isCSV()) {
     * initializeReaderCSV(trace_folder);
     * } else if (this.parserType.isSTD()) {
     * initializeReaderSTD(trace_folder);
     * } else if (this.parserType.isRR()) {
     * initializeReaderRR(trace_folder);
     * }
     * }
     *
     * protected abstract void initializeReaderRV(String trace_folder);
     *
     * protected abstract void initializeReaderCSV(String trace_file);
     *
     * protected abstract void initializeReaderSTD(String trace_file);
     *
     * protected abstract void initializeReaderRR(String trace_file);
     *
     */

    protected void printCompletionStatus() {}
}
