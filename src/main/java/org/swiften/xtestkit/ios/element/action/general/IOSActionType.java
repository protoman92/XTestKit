package org.swiften.xtestkit.ios.element.action.general;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.reactivex.Flowable;
import org.intellij.lang.annotations.Flow;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.bool.BooleanUtil;
import org.swiften.javautilities.localizer.LocalizerType;
import org.swiften.xtestkit.base.element.action.click.BaseClickActionType;
import org.swiften.xtestkit.base.element.locator.general.type.BaseLocatorType;
import org.swiften.xtestkit.base.element.locator.general.xpath.XPath;
import org.swiften.xtestkit.base.param.AlertParam;
import org.swiften.xtestkit.base.type.LocalizerContainerType;
import org.swiften.xtestkit.ios.IOSView;
import org.swiften.xtestkit.mobile.Platform;
import org.swiften.xtestkit.mobile.element.action.general.MobileActionType;

/**
 * Created by haipham on 24/5/17.
 */

/**
 * This interface provides actions for
 * {@link org.swiften.xtestkit.mobile.Platform#IOS}.
 */
public interface IOSActionType extends
    BaseClickActionType,
    BaseLocatorType<IOSDriver<IOSElement>>,
    LocalizerContainerType,
    MobileActionType<IOSDriver<IOSElement>>
{
    @NotNull
    default IOSTouchActionType touchAction() {
        return new IOSTouchActionType() {};
    }

    /**
     * OVerride this method to provide custom implementation, since the default
     * {@link WebDriver.TargetLocator#alert()} usage tends to throw errors at
     * inopportune times.
     * In case there is no alert on the screen, swallow the error since this
     * is not absolutely critical.
     * @param param {@link AlertParam} instance.
     * @return {@link Flowable} instance.
     * @see AlertParam#shouldAccept()
     * @see BooleanUtil#toTrue(Object)
     * @see IOSView.ViewType#UI_BUTTON
     * @see LocalizerType#localize(String)
     * @see MobileActionType#rxa_dismissAlert(AlertParam)
     * @see Platform#IOS
     * @see XPath.Builder#setClass(String)
     * @see XPath.Builder#containsText(String)
     * @see #localizer()
     * @see #rxa_click(WebElement)
     * @see #rxe_withXPath(XPath...)
     */
    @NotNull
    default Flowable<Boolean> rxa_dismissAlert(@NotNull AlertParam param) {
        final IOSActionType THIS = this;
        LocalizerType localizer = localizer();
        String title;

        if (param.shouldAccept()) {
            title = "alert_title_allow";
        } else {
            title = "alert_title_doNotAllow";
        }

        XPath xPath = XPath.builder(Platform.IOS)
            .setClass(IOSView.ViewType.UI_BUTTON.className())
            .containsText(localizer.localize(title))
            .build();

        return rxe_withXPath(xPath)
            .firstElement()
            .toFlowable()
            .flatMap(THIS::rxa_click)
            .map(BooleanUtil::toTrue)
            .onErrorReturnItem(true);
    }
}
