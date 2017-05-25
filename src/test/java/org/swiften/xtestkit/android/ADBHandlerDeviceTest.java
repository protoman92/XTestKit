package org.swiften.xtestkit.android;

import org.swiften.javautilities.rx.RxUtil;
import org.swiften.xtestkit.android.adb.ADBHandler;
import org.swiften.xtestkit.android.type.DeviceUIDType;
import org.swiften.xtestkit.android.param.ClearCacheParam;
import org.swiften.xtestkit.android.param.StartEmulatorParam;
import org.swiften.xtestkit.android.param.StopEmulatorParam;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import org.swiften.javautilities.rx.CustomTestSubscriber;
import org.swiften.javautilities.rx.RxTestUtil;
import org.testng.annotations.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.util.concurrent.TimeUnit;

/**
 * Created by haipham on 3/23/17.
 */
public final class ADBHandlerDeviceTest {
    @NotNull private final ADBHandler ADB_HANDLER;
    @NotNull private final ClearCacheParam CC_PARAM;
    @NotNull private final StartEmulatorParam SE_PARAM;
    @NotNull private final StopEmulatorParam ST_PARAM;
    @NotNull private final DeviceUIDType DUID_PARAM;
    @NotNull private final String APP_PACKAGE;
    @NotNull private final String DEVICE_NAME;
    @NotNull private final String DEVICE_UID;

    private final int PORT = 5558;
    private final int RETRIES_ON_ERROR = 3;

    {
        ADB_HANDLER = spy(ADBHandler.builder().build());
        CC_PARAM = mock(ClearCacheParam.class);
        SE_PARAM = mock(StartEmulatorParam.class);
        ST_PARAM = mock(StopEmulatorParam.class);
        DUID_PARAM = mock(DeviceUIDType.class);

        /* Return this when calling SE_PARAM#deviceName() */
        APP_PACKAGE = "com.android.development";
        DEVICE_NAME = "Nexus_4_API_22";
        DEVICE_UID = String.format("emulator-%d", PORT);
    }

    @BeforeClass
    @SuppressWarnings("unchecked")
    public void beforeClass() {
        doReturn(DEVICE_NAME).when(SE_PARAM).deviceName();
        doReturn(DEVICE_UID).when(SE_PARAM).deviceUID();
        doReturn(PORT).when(SE_PARAM).port();
        doReturn(PORT).when(ST_PARAM).port();
        doReturn(100).when(SE_PARAM).retries();
        doReturn(RETRIES_ON_ERROR).when(ST_PARAM).retries();
        doReturn(APP_PACKAGE).when(CC_PARAM).appPackage();
        doReturn(RETRIES_ON_ERROR).when(CC_PARAM).retries();
        doReturn(DEVICE_UID).when(DUID_PARAM).deviceUID();
        doReturn(DEVICE_UID).when(CC_PARAM).deviceUID();
        TestSubscriber subscriber = CustomTestSubscriber.create();
        ADB_HANDLER.rx_startEmulator(SE_PARAM).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();
    }

    @AfterClass
    @SuppressWarnings("unchecked")
    public void afterClass() {
        reset(ADB_HANDLER, ST_PARAM, SE_PARAM, DUID_PARAM);
        TestSubscriber subscriber = CustomTestSubscriber.create();
        ADB_HANDLER.rx_stopEmulator(ST_PARAM).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();
    }

    @Test(enabled = true)
    @SuppressWarnings("unchecked")
    public void actual_enableDisableConnection_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ADB_HANDLER.rx_disableInternet(DUID_PARAM)
            .filter(success -> success)
            .flatMap(a -> ADB_HANDLER.rx_enableInternet(DUID_PARAM))
            .filter(success -> success)
            .switchIfEmpty(RxUtil.error())
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(RxTestUtil.firstNextEvent(subscriber));
    }

    @Test(enabled = true)
    @SuppressWarnings("unchecked")
    public void actual_checkKeyboardOpen_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ADB_HANDLER.rx_checkKeyboardOpen(DUID_PARAM).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
    }

    @Test(enabled = true)
    @SuppressWarnings("unchecked")
    public void actual_disableEmulatorAnimations_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ADB_HANDLER.rx_disableEmulatorAnimations(DUID_PARAM)
            .filter(success -> success)
            .switchIfEmpty(RxUtil.error())
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(RxTestUtil.firstNextEvent(subscriber));
    }

    @Test(enabled = true)
    @SuppressWarnings("unchecked")
    public void actual_checkAppInstalled_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ADB_HANDLER.rx_checkAppInstalled(CC_PARAM).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(RxTestUtil.firstNextEvent(subscriber));
    }

    @Test(enabled = true)
    @SuppressWarnings("unchecked")
    public void actual_clearCachedData_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ADB_HANDLER.rx_clearCache(CC_PARAM).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(RxTestUtil.firstNextEvent(subscriber));
    }

    @Test(enabled = true)
    @SuppressWarnings("unchecked")
    public void actual_runSeparateEmulatorInstance_shouldSucceed() {
        // Setup
        final int NEW_PORT = 5560;
        String uid = String.format("emulator-%d", NEW_PORT);

        final StartEmulatorParam SEP = StartEmulatorParam.builder()
            .withDeviceName("Nexus_4_API_23")
            .withPort(NEW_PORT)
            .withDeviceUID(uid)
            .withRetries(100)
            .build();

        final StopEmulatorParam STP = StopEmulatorParam.builder()
            .withPort(NEW_PORT)
            .build();

        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ADB_HANDLER
            .rx_startEmulator(SEP)
            .delay(5000, TimeUnit.MILLISECONDS)
            .flatMap(a -> ADB_HANDLER.rx_stopEmulator(STP))
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
    }
}
