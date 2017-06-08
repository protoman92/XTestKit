package org.swiften.xtestkit.base.element.click;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.log.LogUtil;
import org.swiften.javautilities.object.ObjectUtil;
import org.swiften.xtestkitcomponents.common.RepeatType;

import java.util.concurrent.TimeUnit;

/**
 * Created by haipham on 5/15/17.
 */

/**
 * This interface provides methods to click on {@link WebElement}.
 */
public interface ClickActionType {
    /**
     * Perform a click action for {@link WebElement}.
     * @param element {@link WebElement} instance.
     * @see WebElement#click()
     */
    default void click(@NotNull WebElement element) {
        element.click();
    }

    /**
     * Send a click event to {@link WebElement} with
     * {@link WebElement#click()}.
     * @param ELEMENT The {@link WebElement} to be clicked.
     * @return {@link Flowable} instance.
     * @see #click(WebElement)
     */
    @NotNull
    default Flowable<WebElement> rxa_click(@NotNull final WebElement ELEMENT) {
        LogUtil.printft("Clicking on %s", ELEMENT);

        return Completable
            .fromAction(() -> this.click(ELEMENT))
            .<WebElement>toFlowable()
            .defaultIfEmpty(ELEMENT);
    }

    /**
     * Send a click event multiple times to {@link WebElement}.
     * @param ELEMENT {@link WebElement} instance.
     * @param param {@link RepeatType} instance.
     * @return {@link Flowable} instance.
     * @see ObjectUtil#nonNull(Object)
     * @see RepeatType#delay()
     * @see RepeatType#times()
     * @see #rxa_click(WebElement)
     */
    @NotNull
    default Flowable<WebElement> rxa_click(@NotNull final WebElement ELEMENT,
                                           @NotNull RepeatType param) {
        final ClickActionType THIS = this;
        final long DELAY = param.delay();
        final TimeUnit UNIT = TimeUnit.MILLISECONDS;

        return Flowable
            .range(0, param.times())
            .concatMap(a -> THIS.rxa_click(ELEMENT).delay(DELAY, UNIT))
            .all(ObjectUtil::nonNull)
            .map(a -> ELEMENT)
            .toFlowable();
    }
}
