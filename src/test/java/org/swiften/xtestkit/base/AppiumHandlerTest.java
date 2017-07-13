package org.swiften.xtestkit.base;

import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.mockito.ArgumentCaptor;
import org.swiften.javautilities.bool.HPBooleans;
import org.swiften.javautilities.protocol.RetryProviderType;
import org.swiften.javautilities.util.HPLog;
import org.swiften.javautilities.rx.CustomTestSubscriber;
import org.swiften.xtestkit.base.type.AppiumHandlerType;
import org.swiften.xtestkitcomponents.system.network.NetworkHandler;
import org.swiften.xtestkitcomponents.system.process.ProcessRunner;
import org.swiften.xtestkit.util.TestMessageType;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collection;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * Created by haipham on 5/28/17.
 */
@SuppressWarnings({"UndeclaredTests", "MessageMissingOnTestNGAssertion"})
public class AppiumHandlerTest implements AppiumHandlerType, TestMessageType {
    @NotNull private final AppiumHandlerType ENGINE;
    @NotNull private final ProcessRunner PROCESS_RUNNER;
    @NotNull private final NetworkHandler NETWORK_HANDLER;
    @NotNull private final RetryProviderType RETRY;
    private final int TRIES;

    {
        /* Return this processRunner when we call ENGINE.processRunner() */
        PROCESS_RUNNER = spy(new ProcessRunner());

        /* Return this networkHandler when we call ENGINE.networkHandler().
         * On the other hand, return ENGINE when we call
         * NETWORK_HANDLER.processRunner() */
        NETWORK_HANDLER = spy(new NetworkHandler());

        /* Use this parameter when a RetryProviderType is needed */
        RETRY = mock(RetryProviderType.class);

        /* The number of tries for certain test */
        TRIES = 10;

        ENGINE = spy(this);
    }

    @BeforeMethod
    public void beforeMethod() {
        doReturn(PROCESS_RUNNER).when(ENGINE).processRunner();
        doReturn(NETWORK_HANDLER).when(ENGINE).networkHandler();
        doReturn(TRIES).when(RETRY).retries();
    }

    @AfterMethod
    public void afterMethod() {
        reset(ENGINE, PROCESS_RUNNER, NETWORK_HANDLER);
        NETWORK_HANDLER.clearUsedPorts();
    }

    @NotNull
    @Override
    public Address address() {
        return new Address();
    }

    @NotNull
    @Override
    public ProcessRunner processRunner() {
        return PROCESS_RUNNER;
    }

    @NotNull
    @Override
    public NetworkHandler networkHandler() {
        return NETWORK_HANDLER;
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_startAppiumServerWithoutCLI_shouldEmitFallback() {
        try {
            // Setup
            doReturn(Flowable.just("")).when(PROCESS_RUNNER).rxa_execute(eq("which appium"));
            doReturn(Flowable.just(true)).when(ENGINE).rxa_startAppiumOnNewThread(any());
            ArgumentCaptor<String> appiumCaptor = ArgumentCaptor.forClass(String.class);
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            ENGINE.rxa_startLocalAppium(RETRY).subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            verify(ENGINE).processRunner();
            verify(ENGINE).cm_whichAppium();
            verify(ENGINE).cm_fallBackAppium();
            verify(ENGINE).rxa_startLocalAppium(any());
            verify(ENGINE).rxa_startAppiumOnNewThread(appiumCaptor.capture());
            verifyNoMoreInteractions(ENGINE);
            assertTrue(appiumCaptor.getValue().contains("appium"));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_startAppiumServers_shouldExecuteSequentially() {
        // Setup
        final AppiumHandlerTest THIS = this;
        int tries = 5;
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        Flowable.range(1, tries)
            .flatMap(a -> THIS.ENGINE.rxa_startLocalAppium(RETRY))
            .all(HPBooleans::isTrue)
            .toFlowable()
            .flatMap(a -> THIS.ENGINE.networkHandler().rxa_killAll("node appium"))
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        Collection<Integer> usedPorts = ENGINE.networkHandler().usedPorts();
        HPLog.println(usedPorts);
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertEquals(usedPorts.size(), tries);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_stopAppiumServer_shouldSucceed() {
        try {
            // Setup
            doReturn(Flowable.just("")).when(PROCESS_RUNNER).rxa_execute(any());
            TestSubscriber subscriber = CustomTestSubscriber.create();

            // When
            ENGINE.rxa_stopLocalAppium().subscribe(subscriber);
            subscriber.awaitTerminalEvent();

            // Then
            subscriber.assertSubscribed();
            subscriber.assertNoErrors();
            subscriber.assertComplete();
            verify(ENGINE).address();
            verify(ENGINE).rxa_stopLocalAppium();
            verify(ENGINE).networkHandler();
            verifyNoMoreInteractions(ENGINE);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
