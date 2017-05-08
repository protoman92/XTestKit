package org.swiften.xtestkit.engine.base.action.date.type;

/**
 * Created by haipham on 5/8/17.
 */

import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.swiften.xtestkit.engine.base.action.general.type.BaseActionType;
import org.swiften.xtestkit.engine.base.locator.general.type.BaseLocatorType;
import org.swiften.xtestkit.engine.base.type.DriverContainerType;

import java.util.Date;

/**
 * This interface provides date-related actions, such as selecting a date/
 * month/year. However, the specific implementation is left to the individual
 * platforms.
 */
public interface BaseDateActionType<D extends WebDriver> extends
    BaseActionType<D>,
    BaseLocatorType<D>,
    DriverContainerType<D>,
    BaseDateActionErrorType
{
    /**
     * Select a {@link Date}.
     * @param param A {@link DateType} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    default Flowable<Boolean> rxSelectDate(@NotNull DateType param) {
        return Flowable.error(new Exception(DATE_NOT_IMPLEMENTED));
    }

    /**
     * Check if a {@link Date} is currently active.
     * @param param A {@link DateType} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    default Flowable<Boolean> rxHasDate(@NotNull DateType param) {
        return Flowable.error(new Exception(DATE_NOT_IMPLEMENTED));
    }

    /**
     * Get all calendar {@link WebElement}.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    default Flowable<WebElement> rxAllCalendarElements() {
        return Flowable.empty();
    }
}
