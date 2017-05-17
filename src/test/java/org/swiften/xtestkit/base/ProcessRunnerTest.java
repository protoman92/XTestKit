package org.swiften.xtestkit.base;

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

import static org.mockito.Mockito.*;

/**
 * Created by haipham on 4/6/17.
 */
public final class ProcessRunnerTest {
    @NotNull private final ProcessRunner RUNNER;

    {
        RUNNER = spy(ProcessRunner.builder().build());
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
            doThrow(new IOException()).when(RUNNER).execute(anyString());
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            RUNNER.rxExecute("").subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertError(IOException.class);
            subscriber.assertNotComplete();
            verify(RUNNER).execute(anyString());
            verify(RUNNER).rxExecute(anyString());
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
            doReturn("Valid Output").when(RUNNER).execute(anyString());
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            RUNNER.rxExecute("").subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            verify(RUNNER).execute(anyString());
            verify(RUNNER).rxExecute(anyString());
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
            .flatMap(a -> RUNNER.rxExecute(APPIUM))
            .map(a -> a.replace("\n", ""))
            .doOnNext(RUNNER::rxExecute)
            .delay(5000, TimeUnit.MILLISECONDS)
            .flatMap(a -> RUNNER.rxExecute(STOP))
            .doOnError(Throwable::printStackTrace)
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
    }
}
