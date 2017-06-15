package org.swiften.xtestkit.android.element.general;

/**
 * Created by haipham on 18/5/17.
 */

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.bool.BooleanUtil;
import org.swiften.xtestkit.base.element.click.ClickActionType;
import org.swiften.xtestkit.base.element.general.ActionType;
import org.swiften.xtestkit.base.element.locator.type.LocatorType;
import org.swiften.xtestkit.base.param.AlertParam;
import org.swiften.xtestkit.mobile.Platform;
import org.swiften.xtestkit.mobile.element.action.general.MobileActionType;

import java.util.concurrent.TimeUnit;

/**
 * This interface provides general methods to execute actions for
 * {@link Platform#ANDROID}.
 */
public interface AndroidActionType extends
    ClickActionType<AndroidDriver<AndroidElement>>,
    ActionType<AndroidDriver<AndroidElement>>,
    LocatorType<AndroidDriver<AndroidElement>>,
    MobileActionType<AndroidDriver<AndroidElement>>
{
    /**
     * Since {@link WebDriver.TargetLocator#alert()} is not yet implemented
     * on {@link Platform#ANDROID}, we need a custom solution by using
     * {@link AndroidDriver#findElementById(String)}.
     * In case there is no alert on the screen, swallow the error since this
     * is not absolutely critical.
     * @param param {@link AlertParam} instance.
     * @return {@link Flowable} instance.
     * @see ActionType#rxa_dismissAlert(AlertParam)
     * @see AlertParam#shouldAccept()
     * @see BooleanUtil#toTrue(Object)
     * @see #alertDismissDelay()
     * @see #rxa_click(WebElement)
     * @see #rxe_containsID(String...)
     */
    @NotNull
    @Override
    default Flowable<Boolean> rxa_dismissAlert(@NotNull AlertParam param) {
        final AndroidActionType THIS = this;

        String id;

        if (param.shouldAccept()) {
            id = "permission_allow_button";
        } else {
            id = "permission_deny_button";
        }

        return rxe_containsID(id)
            .firstElement()
            .toFlowable()
            .flatMap(THIS::rxa_click)
            .map(BooleanUtil::toTrue)
            .delay(alertDismissDelay(), TimeUnit.MILLISECONDS)
            .onErrorReturnItem(true);
    }
}
