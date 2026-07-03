package com.runtimeverification.rvpredict.instrument;

import com.runtimeverification.rvpredict.runtime.RVPredictRuntime;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;

public final class RVPredictJUnit5BoundaryListener implements TestExecutionListener {
    private final TestBoundaryTracker tracker = new TestBoundaryTracker();
    private volatile TestPlan currentPlan;

    @Override
    public void testPlanExecutionStarted(TestPlan testPlan) {
        currentPlan = testPlan;
        tracker.registerJUnit5Plan(testPlan);
    }

    @Override
    public void dynamicTestRegistered(TestIdentifier testIdentifier) {
        tracker.registerJUnit5DynamicTest(currentPlan, testIdentifier);
    }

    @Override
    public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
        handleCompletedTest(testIdentifier);
    }

    @Override
    public void executionSkipped(TestIdentifier testIdentifier, String reason) {
        handleCompletedTest(testIdentifier);
    }

    private void handleCompletedTest(TestIdentifier testIdentifier) {
        if (testIdentifier == null || !testIdentifier.isTest()) {
            return;
        }

        RVPredictRuntime.onTestMethodFinished();

        if (currentPlan == null) {
            return;
        }

        String className = tracker.resolveJUnit5ClassName(currentPlan, testIdentifier);
        if (className != null && tracker.methodFinished(className)) {
            RVPredictRuntime.onTestClassFinished();
        }
    }
}
