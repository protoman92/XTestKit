package org.swiften.xtestkit.mobile.element.action.general.type;

import io.appium.java_client.MobileDriver;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.element.action.general.type.BaseActionType;

/**
 * Created by haipham on 5/8/17.
 */

/**
 * This interface provides actions for mobile driver.
 * @param <D> Generics parameter that extends {@link MobileDriver}.
 */
public interface MobileActionType<D extends MobileDriver> extends BaseActionType<D> {
    /**
     * Get a {@link MobileTouchActionType} to perform touch actions on mobile
     * apps. Override this to provide custom subclasses.
     * @return A {@link MobileTouchActionType} instance.
     */
    @NotNull
    default MobileTouchActionType touchAction() {
        return new MobileTouchActionType() {};
    }

    /**
     * Launch an app}.
     * @return A {@link Flowable} instance.
     * @see D#launchApp()
     */
    @NotNull
    default Flowable<Boolean> rxLaunchApp() {
        return Completable
            .fromAction(driver()::launchApp)
            .<Boolean>toFlowable()
            .defaultIfEmpty(true);
    }

    /**
     * Reset an installed app.
     * @return A {@link Flowable} instance.
     * @see D#resetApp()
     */
    @NotNull
    default Flowable<Boolean> rxResetApp() {
        return Completable
            .fromAction(driver()::closeApp)
            .<Boolean>toFlowable()
            .defaultIfEmpty(true);
    }
}
