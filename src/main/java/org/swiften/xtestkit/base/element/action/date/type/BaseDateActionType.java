package org.swiften.xtestkit.base.element.action.date.type;

/**
 * Created by haipham on 5/8/17.
 */

import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.swiften.xtestkit.base.element.action.general.type.BaseActionType;
import org.swiften.xtestkit.base.element.locator.general.type.BaseLocatorType;
import org.swiften.xtestkit.base.type.DriverContainerType;

import java.util.Date;

/**
 * This interface provides date-related actions, such as selecting a date/
 * month/year. However, the specific implementation is left to the individual
 * platforms.
 *
 * These methods assume that the user is in a calendar view.
 */
public interface BaseDateActionType<D extends WebDriver> extends
    BaseActionType<D>,
    BaseLocatorType<D>,
    DriverContainerType<D>,
    BaseDateActionErrorType
{
    /**
     * Select a {@link Date}. This assumes that the user is in a calendar
     * view.
     * @param param A {@link DateType} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    default Flowable<Boolean> rxSelectDate(@NotNull DateType param) {
        return Flowable.error(new Exception(DATE_NOT_IMPLEMENTED));
    }

    /**
     * Check if a {@link Date} is currently active. This assumes that the
     * user is in a calendar view.
     * @param param A {@link DateType} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    default Flowable<Boolean> rxHasDate(@NotNull DateType param) {
        return Flowable.error(new Exception(DATE_NOT_IMPLEMENTED));
    }

    /**
     * Get all calendar {@link WebElement}. This assumes that the user is in a
     * calendar view.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    default Flowable<WebElement> rxAllCalendarElements() {
        return Flowable.empty();
    }
}
