package com.swiften.xtestkit.engine.mobile.android.protocol;

import com.swiften.xtestkit.engine.mobile.android.param.StartEmulatorParam;

/**
 * Created by haipham on 4/8/17.
 */
public interface ADBDelayProtocol {
    /**
     * Use this timeout for
     * {@link com.swiften.xtestkit.engine.mobile.android.ADBHandler#rxStartEmulator(StartEmulatorParam)}.
     * @return A {@link Long} value.
     */
    default long emulatorBootTimeout() {
        return 50000;
    }

    /**
     * Use this timeout for
     * {@link com.swiften.xtestkit.engine.mobile.android.ADBHandler#rxStartEmulator(StartEmulatorParam)}.
     * @return A {@link Long} value.
     */
    default long emulatorBootRetryDelay() {
        return 1000;
    }

    /**
     * Use this timeout for operations that change device/emulator settings,
     * such as
     * {@link com.swiften.xtestkit.engine.mobile.android.ADBHandler#rxDisableEmulatorAnimations(DeviceUIDProtocol)}.
     * @return A {@link Long} value.
     */
    default long emulatorSettingTimeout() {
        return 5000;
    }
}
