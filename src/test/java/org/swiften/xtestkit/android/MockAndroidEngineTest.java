package org.swiften.xtestkit.android;

import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.mockito.ArgumentCaptor;
import org.swiften.javautilities.rx.CustomTestSubscriber;
import org.swiften.xtestkit.android.adb.ADBHandler;
import org.swiften.xtestkit.android.param.StartEmulatorParam;
import org.swiften.xtestkit.kit.param.AfterClassParam;
import org.swiften.xtestkit.kit.param.AfterParam;
import org.swiften.xtestkit.kit.param.BeforeClassParam;
import org.swiften.xtestkit.system.network.NetworkHandler;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

/**
 * Created by haipham on 3/22/17.
 */
public final class MockAndroidEngineTest {
    @NotNull private final AndroidEngine ENGINE;
    @NotNull private final AndroidInstance ANDROID_INSTANCE;
    @NotNull private final ADBHandler ADB_HANDLER;
    @NotNull private final NetworkHandler NETWORK_HANDLER;
    @NotNull private final String DEVICE_NAME;
    @NotNull private final String DEVICE_UID;

    {
        DEVICE_NAME = "Nexus_4_API_23";
        DEVICE_UID = "emulator-5556";

        ENGINE = spy(AndroidEngine.builder()
            .withDeviceName(DEVICE_NAME)
            .build());

        /* We return this androidInstance when calling
         * ENGINE.androidInstance() */
        ANDROID_INSTANCE = mock(AndroidInstance.class);

        /* We return this adbHandler when calling ENGINE.adbHandler() */
        ADB_HANDLER = spy(new ADBHandler());

        /* We return this networkHandler when calling ENGINE.networkHandler() */
        NETWORK_HANDLER = spy(new NetworkHandler());
    }

    @BeforeMethod
    public void beforeMethod() {
        doReturn(ANDROID_INSTANCE).when(ENGINE).androidInstance();
        doReturn(ADB_HANDLER).when(ENGINE).adbHandler();
        doReturn(NETWORK_HANDLER).when(ENGINE).networkHandler();
        doReturn(DEVICE_NAME).when(ANDROID_INSTANCE).deviceName();
        doReturn(DEVICE_UID).when(ANDROID_INSTANCE).deviceUID();

        /* Shorten the delay for testing */
        doReturn(100L).when(ADB_HANDLER).emulatorBootRetryDelay();
    }

    @AfterMethod
    public void afterMethod() {
        reset(ENGINE, ADB_HANDLER, NETWORK_HANDLER);
    }

    //region BeforeClass
    @Test
    @SuppressWarnings("unchecked")
    public void test_beforeClass_shouldSucceed() {
        // Setup
        int correctPort = 10;
        doReturn(Flowable.just(true)).when(ENGINE).rxa_startDriver(any());
        doReturn(correctPort).when(ANDROID_INSTANCE).port();
        doReturn(Flowable.just(correctPort)).when(ADB_HANDLER).rxe_availablePort(any());
        doReturn(Flowable.just(true)).when(ADB_HANDLER).rxa_startEmulator(any());
        doReturn(Flowable.just(true)).when(ADB_HANDLER).rxa_disableEmulatorAnimations(any());
        ArgumentCaptor<StartEmulatorParam> SE_CAPTOR = ArgumentCaptor.forClass(StartEmulatorParam.class);
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ENGINE.rxa_beforeClass(BeforeClassParam.DEFAULT).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        verify(ENGINE).adbHandler();
        verify(ENGINE).androidInstance();
        verify(ENGINE).testMode();
        verify(ENGINE).deviceName();
        verify(ENGINE, atLeastOnce()).address();
        verify(ENGINE, atLeastOnce()).processRunner();
        verify(ENGINE).networkHandler();
        verify(ENGINE).cm_whichAppium();
        verify(ENGINE).cm_fallBackAppium();
        verify(ENGINE).cm_startLocalAppium(anyString(), anyInt());
        verify(ENGINE).rxa_startAppiumOnNewThread(anyString());
        verify(ENGINE).rxa_startDriver(any());
        verify(ENGINE).rxa_startLocalAppium(any());
        verify(ENGINE).rxa_beforeClass(any());
        verify(ADB_HANDLER).rxe_availablePort(any());
        verify(ADB_HANDLER).rxa_startEmulator(SE_CAPTOR.capture());
        verify(ADB_HANDLER).rxa_disableEmulatorAnimations(any());
        verify(ANDROID_INSTANCE).setPort(anyInt());
        verifyNoMoreInteractions(ENGINE);
        assertEquals(ANDROID_INSTANCE.port(), SE_CAPTOR.getValue().port());
    }
    //endregion

    //region AfterClass
    @Test
    @SuppressWarnings("unchecked")
    public void test_afterClass_shouldSucceed() {
        // Setup
        doReturn(Flowable.just(true)).when(ADB_HANDLER).rxa_stopEmulator(any());
        doReturn(Flowable.just(true)).when(ENGINE).rxa_stopDriver();
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ENGINE.rxa_afterClass(AfterClassParam.DEFAULT).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        verify(ENGINE, times(3)).networkHandler();
        verify(ENGINE).androidInstance();
        verify(ENGINE).adbHandler();
        verify(ENGINE, atLeastOnce()).address();
        verify(ENGINE).testMode();
        verify(ENGINE).rxa_stopDriver();
        verify(ENGINE).rxa_afterClass(any());
        verify(ENGINE).rxa_stopLocalAppium();
        verify(ADB_HANDLER).rxa_stopEmulator(any());
        verify(NETWORK_HANDLER, times(2)).markPortAvailable(anyInt());
        verifyNoMoreInteractions(ENGINE);
    }
    //endregion

    //region AfterMethod
    @Test
    @SuppressWarnings("unchecked")
    public void test_afterMethod_shouldSucceed() {
        // Setup
        doReturn(Flowable.just(true)).when(ADB_HANDLER).rxa_clearCache(any());
        doReturn(Flowable.just(true)).when(ADB_HANDLER).rxe_appInstalled(any());
        doReturn(Flowable.just(true)).when(ENGINE).rxa_stopDriver();
        doReturn(Flowable.just(true)).when(ENGINE).rxa_resetApp();
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ENGINE.rxa_afterMethod(AfterParam.DEFAULT).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        verify(ENGINE).androidInstance();
        verify(ENGINE).adbHandler();
        verify(ENGINE).appPackage();
        verify(ENGINE).rxa_resetApp();
        verify(ENGINE).rxa_afterMethod(any());
        verify(ADB_HANDLER).rxa_clearCache(any());
        verifyNoMoreInteractions(ENGINE);
    }
    //endregion
}
