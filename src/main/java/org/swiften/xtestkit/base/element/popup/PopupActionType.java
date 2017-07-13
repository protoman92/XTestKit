package org.swiften.xtestkit.base.element.popup;

/**
 * Created by haipham on 6/19/17.
 */

import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.bool.HPBooleans;
import org.swiften.javautilities.util.LogUtil;
import org.swiften.xtestkit.base.element.click.ClickActionType;
import org.swiften.xtestkit.base.element.locator.ByXPath;
import org.swiften.xtestkit.base.element.locator.LocatorType;
import org.swiften.xtestkit.base.model.InputHelperType;
import org.swiften.xtestkitcomponents.platform.PlatformType;
import org.swiften.xtestkitcomponents.xpath.XPath;

import java.util.concurrent.TimeUnit;

/**
 * This interface provides methods to handle popups.
 */
public interface PopupActionType<D extends WebDriver> extends
    ClickActionType<D>,
    LocatorType<D>,
    InputHelperType,
    PopupDelayType
{
    /**
     * Check if a popup corresponding to {@link PopupType} is present
     * on-screen.
     * @param param {@link PopupType} instance.
     * @return {@link Flowable} instance.
     * @see HPBooleans#toTrue(Object)
     * @see ByXPath.Builder#shouldLogXPath(boolean)
     * @see ByXPath.Builder#withXPath(XPath)
     * @see PopupType#presenceXP(InputHelperType)
     * @see #rxe_byXPath(ByXPath...)
     */
    @NotNull
    default Flowable<Boolean> rxv_popupPresent(@NotNull PopupType param) {
        ByXPath query = ByXPath.builder()
            .withXPath(param.presenceXP(this))
            .shouldLogXPath(false)
            .build();

        return rxe_byXPath(query)
            .firstElement()
            .toFlowable()
            .map(HPBooleans::toTrue)
            .onErrorReturnItem(false);
    }

    /**
     * Get the {@link WebElement} that can be used to dismiss the popup.
     * @param param {@link PopupType} instance.
     * @return {@link Flowable} instance.
     * @see PopupType#dismissXP(InputHelperType)
     * @see #rxe_withXPath(XPath...)
     */
    @NotNull
    default Flowable<WebElement> rxe_popupDismiss(@NotNull PopupType param) {
        XPath xpath = param.dismissXP(this);
        return rxe_withXPath(xpath).firstElement().toFlowable();
    }

    /**
     * Dismiss a popup corresponding to {@link PopupType}.
     * @param PARAM {@link PopupType} instance.
     * @return {@link Flowable} instance.
     * @see HPBooleans#toTrue(Object)
     * @see #popupDismissDelay()
     * @see #rxa_click(WebElement)
     * @see #rxe_popupDismiss(PopupType)
     */
    @NotNull
    default Flowable<Boolean> rxa_dismissPopup(@NotNull final PopupType PARAM) {
        final PopupActionType<?> THIS = this;

        return rxe_popupDismiss(PARAM)
            .flatMap(THIS::rxa_click)
            .doOnNext(a -> LogUtil.printft("Dismissing popup %s", PARAM))
            .delay(popupDismissDelay(), TimeUnit.MILLISECONDS)
            .map(HPBooleans::toTrue);
    }

    /**
     * Poll for a popup corresponding to {@link PopupType} with a specified
     * duration interval, then dismiss it when it appears on-screen.
     * @param PARAM {@link PopupType} instance.
     * @return {@link Flowable} instance.
     * @see PopupType#applicableTo(PlatformType)
     * @see #popupPollDuration()
     * @see #platform()
     * @see #rxa_dismissPopup(PopupType)
     * @see #rxv_popupPresent(PopupType)
     */
    @NotNull
    default Flowable<Boolean> rxa_pollAndDismissPopup(@NotNull final PopupType PARAM) {
        final PopupActionType<?> THIS = this;
        PlatformType platform = platform();

        if (PARAM.applicableTo(platform)) {
            return Flowable
                .interval(popupPollDuration(), TimeUnit.MILLISECONDS)
                .flatMap(a -> THIS.rxv_popupPresent(PARAM))
                .distinctUntilChanged()
                .flatMap(a -> {
                    if (a) {
                        return THIS.rxa_dismissPopup(PARAM);
                    } else {
                        return Flowable.just(a);
                    }
                });
        } else {
            return Flowable.just(true);
        }
    }

    /**
     * Poll and dismiss multiple popups.
     * @param params {@link PopupType} varargs.
     * @param <T> Generics parameter.
     * @return {@link Flowable} instance.
     * @see PopupType#applicableTo(PlatformType)
     * @see #platform()
     * @see #rxa_pollAndDismissPopup(PopupType)
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default <T extends PopupType> Flowable<Boolean> rxa_pollAndDismissPopup(@NotNull T...params) {
        final PopupActionType<?> THIS = this;
        final PlatformType PLATFORM = platform();

        return Flowable.fromArray(params)
            .filter(a -> a.applicableTo(PLATFORM))
            .doOnNext(a -> LogUtil.printft("Polling for %s", a))
            .flatMap(THIS::rxa_pollAndDismissPopup);
    }
}
