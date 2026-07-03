package com.runtimeverification.rvpredict.instrument;

import com.runtimeverification.rvpredict.runtime.RVPredictRuntime;
import org.testng.IClassListener;
import org.testng.ITestClass;
import org.testng.ITestListener;
import org.testng.ITestResult;

public final class RVPredictTestNgBoundaryListener implements ITestListener, IClassListener {

    @Override
    public void onTestSuccess(ITestResult result) {
        RVPredictRuntime.onTestMethodFinished();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        RVPredictRuntime.onTestMethodFinished();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        RVPredictRuntime.onTestMethodFinished();
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        RVPredictRuntime.onTestMethodFinished();
    }

    @Override
    public void onAfterClass(ITestClass testClass) {
        RVPredictRuntime.onTestClassFinished();
    }

    @Override
    public void onBeforeClass(ITestClass testClass) {
    }
}
