package com.swiften.xtestkit.engine.android.actual;

import com.swiften.xtestkit.engine.base.param.protocol.RetryProtocol;
import com.swiften.xtestkit.engine.mobile.android.ADBHandler;
import com.swiften.xtestkit.engine.mobile.android.param.StartEmulatorParam;
import com.swiften.xtestkit.engine.mobile.android.param.StopEmulatorParam;
import com.swiften.xtestkit.engine.mobile.android.protocol.DeviceUIDProtocol;
import com.swiften.xtestkit.util.CustomTestSubscriber;
import com.swiften.xtestkit.util.TestUtil;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.apache.bcel.generic.NEW;
import org.jetbrains.annotations.NotNull;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;
import org.testng.annotations.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.util.concurrent.TimeUnit;

/**
 * Created by haipham on 3/23/17.
 */
public class ADBHandlerDeviceTest {
    @NotNull private final ADBHandler ADB_HANDLER;
    @NotNull private final StartEmulatorParam SE_PARAM;
    @NotNull private final StopEmulatorParam ST_PARAM;
    @NotNull private final DeviceUIDProtocol DUID_PARAM;
    @NotNull private final String DEVICE_NAME;
    @NotNull private final String DEVICE_UID;

    private final int PORT = 5558;

    {
        ADB_HANDLER = spy(ADBHandler.builder().build());
        SE_PARAM = mock(StartEmulatorParam.class);
        ST_PARAM = mock(StopEmulatorParam.class);
        DUID_PARAM = mock(DeviceUIDProtocol.class);

        /* Return this when calling SE_PARAM#deviceName() */
        DEVICE_NAME = "Nexus_4_API_23";
        DEVICE_UID = String.format("emulator-%d", PORT);
    }

    @BeforeClass
    @SuppressWarnings("unchecked")
    public void beforeClass() {
        doReturn(DEVICE_NAME).when(SE_PARAM).deviceName();
        doReturn(DEVICE_UID).when(SE_PARAM).deviceUID();
        doReturn(PORT).when(SE_PARAM).port();
        doReturn(PORT).when(ST_PARAM).port();
        doReturn(100).when(SE_PARAM).maxRetries();
        doReturn(3).when(ST_PARAM).minRetries();
        doReturn(DEVICE_UID).when(DUID_PARAM).deviceUID();
        TestSubscriber subscriber = CustomTestSubscriber.create();
        ADB_HANDLER.rxStartEmulator(SE_PARAM).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
    }

    @AfterClass
    @SuppressWarnings("unchecked")
    public void afterClass() {
        reset(ADB_HANDLER, ST_PARAM, SE_PARAM, DUID_PARAM);
        TestSubscriber subscriber = CustomTestSubscriber.create();
        ADB_HANDLER.rxStopEmulator(ST_PARAM).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
    }

    @Test(enabled = true)
    @SuppressWarnings("unchecked")
    public void actual_enableDisableConnection_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ADB_HANDLER.rxDisableInternetConnection(DUID_PARAM)
            .filter(success -> success)
            .flatMap(a -> ADB_HANDLER.rxEnableInternetConnection(DUID_PARAM))
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

    @Test(enabled = true)
    @SuppressWarnings("unchecked")
    public void actual_checkKeyboardOpen_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ADB_HANDLER.rxCheckKeyboardOpen(DUID_PARAM).subscribe(subscriber);
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
        ADB_HANDLER.rxDisableEmulatorAnimations(DUID_PARAM)
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

    @Test(enabled = true)
    @SuppressWarnings("unchecked")
    public void actual_runSeparateEmulatorInstance_shouldSucceed() {
        // Setup
        final int NEW_PORT = 5560;
        String uid = String.format("emulator-%d", NEW_PORT);

        final StartEmulatorParam SEP = StartEmulatorParam.builder()
            .withDeviceName("Nexus_4_API_22")
            .withPort(NEW_PORT)
            .withDeviceUID(uid)
            .withMaxRetries(100)
            .build();

        final StopEmulatorParam STP = StopEmulatorParam.builder()
            .withPort(NEW_PORT)
            .build();

        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ADB_HANDLER
            .rxStartEmulator(SEP)
            .delay(5000, TimeUnit.MILLISECONDS)
            .flatMap(a -> ADB_HANDLER.rxStopEmulator(STP))
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
    }
}
