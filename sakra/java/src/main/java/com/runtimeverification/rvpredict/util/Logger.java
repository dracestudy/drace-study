package com.runtimeverification.rvpredict.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Paths;

import com.google.common.base.Strings;

public class Logger {
    private static final String RV_PREDICT_CONSOLE_PREFIX = "[RV-Predict] ";
    private static final int    WIDTH   =   75;
    private static final String DASH    =   "-";

    private PrintStream debug = System.err;
    private PrintStream result;
    private DataOutputStream pairInfo;
    private boolean report_progress;
    private boolean verbose;
    private StringStorage locStringStorage;
    private StringStorage stackStringStorage;
    private static final Logger INSTANCE = new Logger();

    public Logger(boolean report_progress) {
        PrintStream blackhole = new PrintStream(new OutputStream() {
            public void write(int b) throws IOException {
            }
            public void write(byte[] b) throws IOException {
            }
            public void write(byte[] b, int off, int len) throws IOException {
            }
        });
        DataOutputStream data_blackhole = new DataOutputStream(new OutputStream() {
            public void write(int b) throws IOException {
            }
            public void write(byte[] b) throws IOException {
            }
            public void write(byte[] b, int off, int len) throws IOException {
            }
        });
        debug = blackhole;
        result = blackhole;
        pairInfo = data_blackhole;
        this.report_progress = report_progress;
        this.verbose = false;
    }

    public Logger() {
        this(false);
    }

    /**
     * Retrieve the global instance of Logger class. This instance is the one
     * stored in the Configuration class.
     */
    public static Logger getGlobal() {
        return Logger.INSTANCE;
    }

    public void enableProgressReport() {
        report_progress = true;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public void setLogDir(String logDir) throws FileNotFoundException {
        debug = new PrintStream(new FileOutputStream(Paths.get(logDir, "debug.log").toFile()));
        result = new PrintStream(new FileOutputStream(Paths.get(logDir, "result.txt").toFile()));
        pairInfo = new DataOutputStream(new FileOutputStream(Paths.get(logDir, "pair_info.dat").toFile()));
        locStringStorage = new StringStorage(logDir, "loc");
        stackStringStorage = new StringStorage(logDir, "stack");
    }

    public long addLocationString(String str) throws IOException {
        return locStringStorage.addString(str);
    }

    public long addStackTraceString(String str) throws IOException {
        return stackStringStorage.addString(str);
    }

    public void reportPhase(String phaseMsg) {
        report(center(phaseMsg), MSGTYPE.PHASE);
    }

    private static String center(String msg) {
        int fillWidth = WIDTH - msg.length();
        return "\n" + Strings.repeat(DASH, fillWidth / 2) + msg
                + Strings.repeat(DASH, (fillWidth + 1) / 2);
    }

    /**
     * Write a debug message to disk, the location is set by setLogDir().
     *
     * @param msg The message to be written.
     */
    public void debug(String msg) {
        debug.println(msg);
    }

    public void debug(Throwable e) {
        e.printStackTrace(debug);
    }

    /**
     * Write information required for racy pair finding to disk, the location is set by setLogDir().
     *
     * @param data The data to be written.
     */
    public void writePairInfo(byte[] data) {
        try {
            pairInfo.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Write the race report to disk, the location is set by setLogDir().
     *
     * @param report The race report to be written.
     */
    public void reportRace(String report) {
        result.println(report);
    }

    /**
     * Write the race report to disk, then also report the message.
     *
     * @param report The race report to be written and printed.
     * @param type The type of the report.
     *
     * @see #report
     * @see #reportRace
     */
    public void reportAndLogResults(String report, MSGTYPE type) {
        this.reportRace(report);
        this.report(report, type);
    }

    /**
     * Reports a message with a specified type.
     *
     * <p>ERROR messages will be printed to stderr with an error prefix.
     * INFO messages will be printed to stderr with a prefix.
     * VERBOSE messages will only be printed when the verbose option is set.
     * PROGRESS messages will only be printed when the report_progress option is set.
     * PHASE messages will be printed to stderr.
     * REPORT messages will be printed to stdout.
     *
     * @param msg  The message to be reported.
     * @param type The type of the message (e.g., ERROR, INFO, PROGRESS, PHASE, VERBOSE, REPORT).
     */
    public synchronized void report(String msg, MSGTYPE type) {
        switch (type) {
        case ERROR:
            System.err.println(RV_PREDICT_CONSOLE_PREFIX + "Error: " + msg);
            break;
        case INFO:
            System.err.println(RV_PREDICT_CONSOLE_PREFIX + msg);
            break;
        case PROGRESS:
            if (!report_progress)
                return;
            /*FALLTHROUGH*/
        case PHASE:
            System.err.println(msg);
            break;
        case VERBOSE:
            if (!verbose)
                return;
            System.err.println(msg);
            break;
        case REPORT:
            System.out.println(msg);
            break;
        }
    }

    public enum MSGTYPE {
        ERROR, INFO, PHASE, PROGRESS, REPORT, VERBOSE
    }

}
