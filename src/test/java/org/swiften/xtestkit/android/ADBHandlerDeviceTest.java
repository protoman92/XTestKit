package org.swiften.xtestkit.android;

import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.bool.HPBooleans;
import org.swiften.javautilities.rx.CustomTestSubscriber;
import org.swiften.javautilities.rx.HPReactives;
import org.swiften.xtestkit.android.adb.ADBHandler;
import org.swiften.xtestkit.android.param.ClearCacheParam;
import org.swiften.xtestkit.android.param.StartEmulatorParam;
import org.swiften.xtestkit.android.param.StopEmulatorParam;
import org.swiften.xtestkit.android.type.DeviceUIDProviderType;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertTrue;

/**
 * Created by haipham on 3/23/17.
 */
@SuppressWarnings({"UndeclaredTests", "MessageMissingOnTestNGAssertion"})
public final class ADBHandlerDeviceTest {
    @NotNull private final ADBHandler ADB_HANDLER;
    @NotNull private final ClearCacheParam CC_PARAM;
    @NotNull private final StartEmulatorParam SE_PARAM;
    @NotNull private final StopEmulatorParam ST_PARAM;
    @NotNull private final DeviceUIDProviderType DUID_PARAM;
    @NotNull private final String APP_PACKAGE;
    @NotNull private final String DEVICE_NAME;
    @NotNull private final String DEVICE_UID;

    private final int PORT = 5558;
    private final int RETRIES_ON_ERROR = 3;

    {
        ADB_HANDLER = spy(new ADBHandler());
        CC_PARAM = mock(ClearCacheParam.class);
        SE_PARAM = mock(StartEmulatorParam.class);
        ST_PARAM = mock(StopEmulatorParam.class);
        DUID_PARAM = mock(DeviceUIDProviderType.class);

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
        ADB_HANDLER.rxa_startEmulator(SE_PARAM).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();
    }

    @AfterClass
    @SuppressWarnings("unchecked")
    public void afterClass() {
        reset(ADB_HANDLER, ST_PARAM, SE_PARAM, DUID_PARAM);
        TestSubscriber subscriber = CustomTestSubscriber.create();
        ADB_HANDLER.rxa_stopEmulator(ST_PARAM).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();
    }

    @Test(enabled = true)
    @SuppressWarnings("unchecked")
    public void actual_enableDisableConnection_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ADB_HANDLER.rxa_disableInternet(DUID_PARAM)
            .filter(success -> success)
            .flatMap(a -> ADB_HANDLER.rxa_enableInternet(DUID_PARAM))
            .filter(success -> success)
            .switchIfEmpty(HPReactives.error())
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(HPReactives.firstNextEvent(subscriber));
    }

    @Test(enabled = true)
    @SuppressWarnings("unchecked")
    public void actual_checkKeyboardOpen_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ADB_HANDLER.rxe_keyboardOpen(DUID_PARAM).subscribe(subscriber);
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
        ADB_HANDLER.rxa_disableAnimations(DUID_PARAM)
            .filter(HPBooleans::isTrue)
            .switchIfEmpty(HPReactives.error())
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(HPReactives.firstNextEvent(subscriber));
    }

    @Test(enabled = true)
    @SuppressWarnings("unchecked")
    public void actual_checkAppInstalled_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ADB_HANDLER.rxe_appInstalled(CC_PARAM).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(HPReactives.firstNextEvent(subscriber));
    }

    @Test(enabled = true)
    @SuppressWarnings("unchecked")
    public void actual_clearCachedData_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ADB_HANDLER.rxa_clearCache(CC_PARAM).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(HPReactives.firstNextEvent(subscriber));
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
            .rxa_startEmulator(SEP)
            .delay(5000, TimeUnit.MILLISECONDS)
            .flatMap(a -> ADB_HANDLER.rxa_stopEmulator(STP))
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
    }
}
