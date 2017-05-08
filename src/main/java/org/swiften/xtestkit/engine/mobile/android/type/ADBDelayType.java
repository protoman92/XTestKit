package org.swiften.xtestkit.engine.mobile.android.type;

import org.swiften.xtestkit.engine.mobile.android.ADBHandler;
import org.swiften.xtestkit.engine.mobile.android.param.StartEmulatorParam;

/**
 * Created by haipham on 4/8/17.
 */

/**
 * This interface provides delay durations for {@link ADBHandler}.
 */
public interface ADBDelayType {
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
     * {@link ADBHandler#rxDisableEmulatorAnimations(DeviceUIDType)}.
     * @return A {@link Long} value.
     */
    default long emulatorSettingTimeout() {
        return 5000;
    }
}
