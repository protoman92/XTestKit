package com.swiften.xtestkit.engine.android.actual;

import com.swiften.xtestkit.engine.base.param.protocol.RetryProtocol;
import com.swiften.xtestkit.engine.mobile.android.ADBHandler;
import com.swiften.xtestkit.engine.mobile.android.param.StartEmulatorParam;
import com.swiften.xtestkit.rx.RxExtension;
import com.swiften.xtestkit.util.CustomTestSubscriber;
import com.swiften.xtestkit.util.TestUtil;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.apache.bcel.generic.RET;
import org.apache.regexp.RE;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.*;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Created by haipham on 4/9/17.
 */
public class ADBHandlerTest {
    @NotNull private final ADBHandler ADB_HANDLER;
    @NotNull private final RetryProtocol RETRY;

    {
        ADB_HANDLER = spy(ADBHandler.builder().build());
        RETRY = mock(RetryProtocol.class);
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
    public void actual_findPortMultipleTimes_shouldSucceed() {
        // Setup
        int tries = 40;
        int totalPorts = ADBHandler.availablePortsCount();
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        Flowable.range(1, tries)
            .flatMap(a -> ADB_HANDLER
                .rxFindAvailablePort(RETRY)
                .onErrorResumeNext(Flowable.empty()))
            .distinct()
            .count()
            .toFlowable()
            .compose(RxExtension.withCommonSchedulers())
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertEquals(TestUtil.<Long>getFirstNextEvent(subscriber).intValue(), totalPorts);
    }
}
