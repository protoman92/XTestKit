package org.swiften.xtestkit.mobile.ios.element.action.general.type;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.mobile.element.action.general.type.MobileActionType;

/**
 * Created by haipham on 24/5/17.
 */

/**
 * This interface provides actions for
 * {@link org.swiften.xtestkit.mobile.Platform#IOS}.
 */
public interface IOSActionType extends MobileActionType<IOSDriver<IOSElement>> {
    @NotNull
    default IOSTouchActionType touchAction() {
        return new IOSTouchActionType() {};
    }
}
