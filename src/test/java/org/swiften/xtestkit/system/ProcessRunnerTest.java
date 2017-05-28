package org.swiften.xtestkit.system;

import org.swiften.javautilities.log.LogUtil;
import org.swiften.javautilities.rx.RxTestUtil;
import org.swiften.javautilities.rx.RxUtil;
import org.swiften.xtestkit.system.process.ProcessRunner;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import static org.testng.Assert.*;

import org.swiften.javautilities.rx.CustomTestSubscriber;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.mockito.Mockito.*;

/**
 * Created by haipham on 4/6/17.
 */
public final class ProcessRunnerTest {
    @NotNull private final ProcessRunner RUNNER;

    {
        RUNNER = spy(new ProcessRunner());
    }

    @AfterMethod
    public void afterMethod() {
        reset(RUNNER);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_rxRunProcessWithError_shouldThrow() {
        try {
            // Setup
            doReturn(RxUtil.error()).when(RUNNER).rxa_execute(any());
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            RUNNER.rxa_execute("").subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertError(IOException.class);
            subscriber.assertNotComplete();
            verify(RUNNER).rxa_execute(any());
            verifyNoMoreInteractions(RUNNER);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_rxRunProcess_shouldSucceed() {
        try {
            // Setup
            doReturn(Flowable.just("")).when(RUNNER).rxa_execute(any());
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            RUNNER.rxa_execute("").subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            verify(RUNNER).rxa_execute(any());
            verifyNoMoreInteractions(RUNNER);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void actual_rxRunAppiumProcess_shouldSucceed() {
        // Setup
        final String APPIUM = "which appium";
        final String STOP = "killall node appium";
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        Flowable.timer(2000, TimeUnit.MILLISECONDS)
            .concatMap(a -> RUNNER.rxa_execute(APPIUM))
            .map(a -> a.replace("\n", ""))
            .doOnNext(RUNNER::rxa_execute)
            .delay(5000, TimeUnit.MILLISECONDS)
            .flatMap(a -> RUNNER.rxa_execute(STOP))
            .doOnError(Throwable::printStackTrace)
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
    }

    @Test(enabled = true)
    @SuppressWarnings("unchecked")
    public void test_executeStream_shouldSucceed() {
        // Setup
        String args = "/usr/local/bin/appium";
        final String CLEANUP = "killall node appium";
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        RUNNER.rxa_executeStream(args)
            .doOnNext(LogUtil::println)
            .timeout(20000, TimeUnit.MILLISECONDS)
            .doFinally(() -> RUNNER.execute(CLEANUP, LogUtil::println))
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        /* We expect a TimeoutException if the operation ran successfully */
        subscriber.assertSubscribed();
        subscriber.assertError(TimeoutException.class);
        subscriber.assertNotComplete();
    }
}
