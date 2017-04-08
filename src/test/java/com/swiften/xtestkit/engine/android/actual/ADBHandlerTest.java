package com.swiften.xtestkit.engine.android.actual;

import com.swiften.xtestkit.engine.base.param.protocol.RetryProtocol;
import com.swiften.xtestkit.engine.mobile.android.ADBHandler;
import com.swiften.xtestkit.engine.mobile.android.param.StartEmulatorParam;
import com.swiften.xtestkit.util.CustomTestSubscriber;
import com.swiften.xtestkit.util.TestUtil;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;
import org.testng.annotations.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

/**
 * Created by haipham on 3/23/17.
 */
public class ADBHandlerTest {
    @NotNull private final ADBHandler ADB_HANDLER;
    @NotNull private final StartEmulatorParam SE_PARAM;
    @NotNull private final String DEVICE_NAME;

    {
        ADB_HANDLER = spy(ADBHandler.builder().build());
        SE_PARAM = mock(StartEmulatorParam.class);

        /* Return this when calling SE_PARAM#deviceName() */
        DEVICE_NAME = "Nexus_4_API_23";
    }

    @BeforeClass
    @SuppressWarnings("unchecked")
    public void beforeClass() {
        doReturn(DEVICE_NAME).when(SE_PARAM).deviceName();
        doReturn(100).when(SE_PARAM).maxRetries();
        TestSubscriber subscriber = CustomTestSubscriber.create();
        ADB_HANDLER.rxStartEmulator(SE_PARAM).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
    }

    @AfterClass
    @SuppressWarnings("unchecked")
    public void afterClass() {
        TestSubscriber subscriber = CustomTestSubscriber.create();
        ADB_HANDLER.rxStopEmulator(RetryProtocol.DEFAULT).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        reset(ADB_HANDLER);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void actual_enableDisableConnection_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ADB_HANDLER.rxDisableInternetConnection()
            .filter(success -> success)
            .flatMap(a -> ADB_HANDLER.rxEnableInternetConnection())
            .filter(success -> success)
            .switchIfEmpty(Flowable.error(new Exception()))
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(TestUtil.getFirstNextEvent(subscriber));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void actual_checkKeyboardOpen_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ADB_HANDLER.rxCheckKeyboardOpen().subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void actual_disableEmulatorAnimations_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ADB_HANDLER.rxDisableEmulatorAnimations()
            .filter(success -> success)
            .switchIfEmpty(Flowable.error(new Exception()))
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(TestUtil.getFirstNextEvent(subscriber));
    }
}
