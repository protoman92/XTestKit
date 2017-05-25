package org.swiften.xtestkit.android;

import org.swiften.xtestkit.base.type.RetryType;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.rx.CustomTestSubscriber;
import org.swiften.javautilities.rx.RxTestUtil;
import org.swiften.javautilities.rx.RxUtil;
import org.swiften.xtestkit.android.adb.ADBHandler;
import org.testng.annotations.*;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Created by haipham on 4/9/17.
 */
public final class ActualADBHandlerTest {
    @NotNull private final ADBHandler ADB_HANDLER;
    @NotNull private final RetryType RETRY;

    {
        ADB_HANDLER = spy(ADBHandler.builder().build());
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
        int tries = 40;
        int totalPorts = ADBHandler.availablePortsCount();
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        Flowable.range(1, tries)
            .flatMap(a -> ADB_HANDLER
                .rx_availablePort(RETRY)
                .onErrorResumeNext(Flowable.empty()))
            .distinct()
            .count()
            .toFlowable()
            .compose(RxUtil.withCommonSchedulers())
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertEquals(RxTestUtil.<Long>firstNextEvent(subscriber).intValue(), totalPorts);
    }
}
