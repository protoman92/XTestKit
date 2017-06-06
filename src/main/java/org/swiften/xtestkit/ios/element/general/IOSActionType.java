package org.swiften.xtestkit.ios.element.general;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.bool.BooleanUtil;
import org.swiften.javautilities.localizer.LocalizerType;
import org.swiften.xtestkit.base.element.click.ClickActionType;
import org.swiften.xtestkit.base.element.locator.type.BaseLocatorType;
import org.swiften.xtestkit.base.param.AlertParam;
import org.swiften.xtestkit.base.type.BaseViewType;
import org.swiften.xtestkit.base.type.LocalizerContainerType;
import org.swiften.xtestkit.ios.IOSView;
import org.swiften.xtestkit.mobile.element.action.general.MobileActionType;
import org.swiften.xtestkit.mobile.Platform;
import org.swiften.xtestkitcomponents.platform.PlatformType;
import org.swiften.xtestkitcomponents.xpath.Attribute;
import org.swiften.xtestkitcomponents.xpath.Attributes;
import org.swiften.xtestkitcomponents.xpath.CompoundAttribute;
import org.swiften.xtestkitcomponents.xpath.XPath;

import java.util.concurrent.TimeUnit;

/**
 * Created by haipham on 24/5/17.
 */

/**
 * This interface provides actions for
 * {@link Platform#IOS}.
 */
public interface IOSActionType extends
    ClickActionType,
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
     * @see MobileActionType#rxa_dismissAlert(AlertParam)
     * @see AlertParam#shouldAccept()
     * @see Attributes#of(PlatformType)
     * @see Attributes#hasText(String)
     * @see BaseViewType#className()
     * @see BooleanUtil#toTrue(Object)
     * @see CompoundAttribute#single(Attribute)
     * @see CompoundAttribute#withClass(String)
     * @see IOSView.ViewType#UI_BUTTON
     * @see LocalizerType#localize(String)
     * @see MobileActionType#rxa_dismissAlert(AlertParam)
     * @see Platform#IOS
     * @see XPath.Builder#addAttribute(Attribute)
     * @see #alertDismissalDelay()
     * @see #localizer()
     * @see #rxa_click(WebElement)
     * @see #rxe_withXPath(XPath...)
     */
    @NotNull
    @Override
    default Flowable<Boolean> rxa_dismissAlert(@NotNull AlertParam param) {
        final IOSActionType THIS = this;
        final Platform PLATFORM = Platform.IOS;
        final Attributes ATTRS = Attributes.of(PLATFORM);
        final LocalizerType LOCALIZER = localizer();
        final String BTN_CLS = IOSView.ViewType.UI_BUTTON.className();
        String[] titles;

        if (param.shouldAccept()) {
            titles = new String[] { "alert_title_allow", "alert_title_ok" };
        } else {
            titles = new String[] { "alert_title_doNotAllow" };
        }

        return Flowable.fromArray(titles)
            .map(LOCALIZER::localize)
            .map(ATTRS::hasText)
            .map(CompoundAttribute::single)
            .map(a -> a.withClass(BTN_CLS))
            .map(a -> XPath.builder().addAttribute(a).build())
            .toList()
            .map(a -> a.toArray(new XPath[a.size()]))
            .toFlowable()
            .flatMap(THIS::rxe_withXPath)
            .firstElement()
            .toFlowable()
            .flatMap(THIS::rxa_click)
            .map(BooleanUtil::toTrue)
            .delay(alertDismissalDelay(), TimeUnit.MILLISECONDS)
            .onErrorReturnItem(true);
    }
}
