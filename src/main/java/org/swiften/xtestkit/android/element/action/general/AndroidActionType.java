package org.swiften.xtestkit.android.element.action.general;

/**
 * Created by haipham on 18/5/17.
 */

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.bool.BooleanUtil;
import org.swiften.javautilities.object.ObjectUtil;
import org.swiften.javautilities.rx.RxUtil;
import org.swiften.xtestkit.base.element.action.click.BaseClickActionType;
import org.swiften.xtestkit.base.element.action.general.BaseActionType;
import org.swiften.xtestkit.base.element.locator.general.type.BaseLocatorErrorType;
import org.swiften.xtestkit.base.param.AlertParam;
import org.swiften.xtestkit.mobile.Platform;
import org.swiften.xtestkit.mobile.element.action.general.MobileActionType;

/**
 * This interface provides general methods to execute actions for
 * {@link org.swiften.xtestkit.mobile.Platform#ANDROID}.
 */
public interface AndroidActionType extends
    BaseClickActionType,
    BaseActionType<AndroidDriver<AndroidElement>>,
    BaseLocatorErrorType,
    MobileActionType<AndroidDriver<AndroidElement>>
{
    /**
     * Since {@link WebDriver.TargetLocator#alert()} is not yet implemented
     * on {@link Platform#ANDROID}, we need a custom solution by using
     * {@link AndroidDriver#findElementById(String)}.
     * @param param {@link AlertParam} instance.
     * @return {@link Flowable} instance.
     * @see BaseActionType#rxa_dismissAlert(AlertParam)
     * @see #driver()
     * @see WebDriver#findElement(By)
     * @see ObjectUtil#nonNull(Object)
     * @see #rxa_click(WebElement)
     * @see RxUtil#error(String)
     * @see #NO_SUCH_ELEMENT
     * @see BooleanUtil#toTrue(Object)
     */
    @NotNull
    @Override
    default Flowable<Boolean> rxa_dismissAlert(@NotNull AlertParam param) {
        final AndroidActionType THIS = this;

        return Flowable.just(param.shouldAccept())
            .map(a -> a ? "permission_allow_button" : "permission_deny_button")
            .map(id -> String.format("com.android.packageinstaller:id/%s", id))
            .map(id -> driver().findElement(By.id(id)))
            .filter(ObjectUtil::nonNull)
            .switchIfEmpty(RxUtil.error(NO_SUCH_ELEMENT))
            .flatMap(THIS::rxa_click)
            .map(BooleanUtil::toTrue);
    }
}
