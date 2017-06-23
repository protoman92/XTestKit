package org.swiften.xtestkit.base.element.visibility;

/**
 * Created by haipham on 5/16/17.
 */

import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.number.NumberUtil;
import org.swiften.javautilities.protocol.DelayProviderType;
import org.swiften.javautilities.rx.RxUtilParam;
import org.swiften.javautilities.rx.RxUtil;
import org.swiften.xtestkit.base.element.locator.LocatorType;

import java.util.concurrent.TimeUnit;

/**
 * This interface provides methods to handle view visibility, such as polling
 * until a progress bar finishes. This can be used as a proxy for when a
 * network operation completes.
 * @param <D> Generics parameter that extends {@link WebDriver}.
 */
public interface VisibilityActionType<D extends WebDriver> extends LocatorType<D>, VisibilityDelayType {
    /**
     * Watch until {@link WebElement} disappears from the screen. This could
     * be useful in case we are waiting for progress bars.
     * @param flowable {@link Flowable} instance that emits the
     *                 {@link WebElement} to be checked for visibility.
     * @return {@link Flowable} instance.
     * @see NumberUtil#isZero(Number)
     * @see RxUtilParam.Builder#withDelay(long)
     * @see RxUtilParam.Builder#withTimeUnit(TimeUnit)
     * @see RxUtil#repeatUntil(Flowable, DelayProviderType)
     * @see #consecutiveVisibilityCheckDelay()
     */
    @NotNull
    default Flowable<Boolean> rxa_watchUntilHidden(@NotNull Flowable<WebElement> flowable) {
        Flowable<Boolean> counter = flowable.count()
            .map(NumberUtil::isZero)
            .toFlowable();

        RxUtilParam param = RxUtilParam.builder()
            .withDelay(consecutiveVisibilityCheckDelay())
            .withTimeUnit(TimeUnit.MILLISECONDS)
            .build();

        return Flowable.just(true).compose(RxUtil.repeatUntil(counter, param));
    }
}
