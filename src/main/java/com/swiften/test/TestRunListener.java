package com.swiften.test;

import com.swiften.util.Log;
import org.jetbrains.annotations.NotNull;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

/**
 * Created by haipham on 3/26/17.
 */
public class TestRunListener extends RunListener {
    @Override
    public void testRunStarted(@NotNull Description description) throws Exception {
        Log.println("Number of tests to execute: " + description.testCount());
    }

    @Override
    public void testRunFinished(@NotNull Result result) throws Exception {
        Log.println("Number of tests executed: " + result.getRunCount());
    }

    @Override
    public void testStarted(@NotNull Description description) throws Exception {
        Log.println("Starting: " + description.getMethodName());
    }

    @Override
    public void testFinished(@NotNull Description description) throws Exception {
        Log.println("Finished: " + description.getMethodName());
    }

    @Override
    public void testFailure(@NotNull Failure failure) throws Exception {
        Log.println("Failed: " + failure.getDescription().getMethodName());
    }

    @Override
    public void testAssumptionFailure(@NotNull Failure failure) {
        Log.println("Failed: " + failure.getDescription().getMethodName());
    }

    @Override
    public void testIgnored(@NotNull Description description) throws Exception {
        Log.println("Ignored: " + description.getMethodName());
    }
}
