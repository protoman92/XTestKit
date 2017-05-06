package org.swiften.xtestkit.engine.mobile.android;

/**
 * Created by haipham on 4/8/17.
 */
public interface ADBDelayProtocol {
    /**
     * Use this timeout for
     * {@link ADBHandler#rxStartEmulator(StartEmulatorParam)}.
     * @return A {@link Long} value.
     */
    default long emulatorBootTimeout() {
        return 50000;
    }

    /**
     * Use this timeout for
     * {@link ADBHandler#rxStartEmulator(StartEmulatorParam)}.
     * @return A {@link Long} value.
     */
    default long emulatorBootRetryDelay() {
        return 1000;
    }

    /**
     * Use this timeout for operations that change device/emulator settings,
     * such as
     * {@link ADBHandler#rxDisableEmulatorAnimations(DeviceUIDProtocol)}.
     * @return A {@link Long} value.
     */
    default long emulatorSettingTimeout() {
        return 5000;
    }
}
