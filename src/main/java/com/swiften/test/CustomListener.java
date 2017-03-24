package com.swiften.test;

import junit.framework.TestListener;
import org.jetbrains.annotations.NotNull;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

/**
 * Created by haipham on 3/25/17.
 */
public class CustomListener extends RunListener {
    @Override
    public void testRunStarted(@NotNull Description description) throws Exception {
        System.out.println("Number of tests to execute: " + description.testCount());
    }

    @Override
    public void testRunFinished(@NotNull Result result) throws Exception {
        System.out.println("Number of tests executed: " + result.getRunCount());
    }

    @Override
    public void testStarted(@NotNull Description description) throws Exception {
        System.out.println("Starting: " + description.getMethodName());
    }

    @Override
    public void testFinished(@NotNull Description description) throws Exception {
        System.out.println("Finished: " + description.getMethodName());
    }

    @Override
    public void testFailure(@NotNull Failure failure) throws Exception {
        System.out.println("Failed: " + failure.getDescription().getMethodName());
    }

    @Override
    public void testAssumptionFailure(@NotNull Failure failure) {
        System.out.println("Failed: " + failure.getDescription().getMethodName());
    }

    @Override
    public void testIgnored(@NotNull Description description) throws Exception {
        System.out.println("Ignored: " + description.getMethodName());
    }
}
