package com.runtimeverification.rvpredict.instrument;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.runtimeverification.rvpredict.util.Logger;

/**
 * This class is adapted from STARTS's SurefireMojoInterceptor
 * (https://github.com/TestingResearchIllinois/starts/blob/master/...
 * starts-core/src/main/java/edu/illinois/starts/maven/SurefireMojoInterceptor.java).
 * This class manipulates the argLine field of SurefirePlugin, where STARTS's
 * SurefireMojoInterceptor manipulates the
 * excludes field of SurefirePlugin.
 **/

public final class SurefireMojoInterceptor extends AbstractMojoInterceptor {
    public static final String UNSUPPORTED_SUREFIRE_VERSION_EXCEPTION = "Unsupported surefire version. ";
    public static Object sfMojo;

    private static Logger logger;

    private final static String[] predictArgs = { "window", "algorithm", "base-log-dir", "log-dirname",
            "solver-timeout", "global-timeout", "include", "exclude", "test-scope" };
    private final static String[] argPrefix = { " --", " --", " --base-log-dir ", " --log-dirname ",
            " --solver-timeout ", " --global-timeout ", " --include ", " --exclude ", " --test-scope " };

    /**
     * Method that executes at the very beginning of SurefirePlugin's execute
     * method.
     */
    public static void execute(Object mojo) throws Exception {
        logger = Logger.getGlobal();

        sfMojo = mojo;
        String currentArgs = checkSurefireVersion(mojo);
        manipulateArgs(mojo, currentArgs);
    }

    private static String checkSurefireVersion(Object mojo) throws Exception {
        String argLineString = "";
        // Modern versions of surefire have both of these fields. Skip if we don't have
        // these fields.
        argLineString = (String) getField("argLine", mojo);
        return argLineString;
    }

    private static void manipulateArgs(Object mojo, String currentArgs)
            throws Exception {

        // Find all properties that are set
        StringBuilder argsToAppend = new StringBuilder();
        argsToAppend.append("\"");
        for (int i = 0; i < predictArgs.length; i++) {
            String setArg = System.getProperty("rv." + predictArgs[i]);
            if (setArg != null && !setArg.equals("")) {
                argsToAppend.append(argPrefix[i] + setArg);
            }
        }
        if (Boolean.getBoolean("rv.lib-stacks")) {
            argsToAppend.append(" --lib-stacks");
        }
        if (Boolean.getBoolean("rv.verbose")) {
            argsToAppend.append(" --verbose");
        }
        if (Boolean.getBoolean("rv.pair")) {
            argsToAppend.append(" --pair");
        }
        if (Boolean.getBoolean("rv.log")) {
            argsToAppend.append(" --log");
        }
        if (Boolean.getBoolean("rv.offline")) {
            argsToAppend.append(" --offline");
        }
        argsToAppend.append("\"");

    	String rvJavaAgent = "-javaagent:" + System.getProperty("rv.jar-path");
    	String replacement = rvJavaAgent + "=" + argsToAppend;

    	String newArgLine;
    	if (currentArgs.contains(rvJavaAgent)) {
    	    newArgLine = currentArgs.replace(rvJavaAgent, replacement);
    	} else {
    	    newArgLine = currentArgs + " " + replacement;
    	}
    	logger.report("argLine: " + newArgLine, Logger.MSGTYPE.INFO);
    	setField("argLine", mojo, newArgLine);

        //String newArgLine = currentArgs + "=" + argsToAppend;
        //logger.report("argLine: " + newArgLine, Logger.MSGTYPE.INFO);
        //setField("argLine", mojo, newArgLine);
    }
}
