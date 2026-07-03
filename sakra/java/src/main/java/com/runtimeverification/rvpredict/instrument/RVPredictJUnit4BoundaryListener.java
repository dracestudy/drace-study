package com.runtimeverification.rvpredict.instrument;

import com.runtimeverification.rvpredict.runtime.RVPredictRuntime;
import org.junit.runner.Description;
import org.junit.runner.notification.RunListener;

public final class RVPredictJUnit4BoundaryListener extends RunListener {
    private final TestBoundaryTracker tracker = new TestBoundaryTracker();

    @Override
    public void testRunStarted(Description description) throws Exception {
        tracker.registerJUnit4Tree(description);
    }

    @Override
    public void testFinished(Description description) throws Exception {
        handleCompletedTest(description);
    }

    @Override
    public void testIgnored(Description description) throws Exception {
        handleCompletedTest(description);
    }

    private void handleCompletedTest(Description description) {
        if (description == null || !description.isTest()) {
            return;
        }

        RVPredictRuntime.onTestMethodFinished();

        String className = description.getClassName();
        if (className != null && tracker.methodFinished(className)) {
            RVPredictRuntime.onTestClassFinished();
        }
    }
}
