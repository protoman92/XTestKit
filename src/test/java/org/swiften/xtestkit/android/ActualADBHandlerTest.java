package org.swiften.xtestkit.android;

import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.util.LogUtil;
import org.swiften.javautilities.rx.CustomTestSubscriber;
import org.swiften.javautilities.rx.RxUtil;
import org.swiften.xtestkit.android.adb.ADBErrorType;
import org.swiften.xtestkit.android.adb.ADBHandler;
import org.swiften.javautilities.protocol.RetryType;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

/**
 * Created by haipham on 4/9/17.
 */
@SuppressWarnings({"MessageMissingOnTestNGAssertion", "UndeclaredTests"})
public final class ActualADBHandlerTest implements ADBErrorType {
    @NotNull private final ADBHandler ADB_HANDLER;
    @NotNull private final RetryType RETRY;

    {
        ADB_HANDLER = spy(new ADBHandler());
        RETRY = mock(RetryType.class);
    }

    @BeforeMethod
    public void beforeMethod() {
        doReturn(3).when(RETRY).retries();
    }

    @AfterMethod
    public void afterMethod() {
        reset(ADB_HANDLER, RETRY);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_findPortMultipleTimes_shouldSucceed() {
        // Setup
        int totalPorts = ADBHandler.availablePortsCount();
        int tries = totalPorts + 5;
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        Flowable.range(1, tries)
            .flatMap(a -> ADB_HANDLER.rxe_availablePort(RETRY))
            .distinct()
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        /* Since there are limited number of ports available, we expect the
         * process to throw an Exception */
        List ports = RxUtil.nextEvents(subscriber);
        LogUtil.println(ports);
        subscriber.assertSubscribed();
        subscriber.assertErrorMessage(NO_PORT_AVAILABLE);
        subscriber.assertNotComplete();
        assertEquals(ports.size(), totalPorts);
    }
}
