package org.swiften.xtestkit.base.element.action.date.type;

/**
 * Created by haipham on 5/8/17.
 */

import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.rx.RxUtil;
import org.swiften.xtestkit.base.element.action.general.type.BaseActionType;
import org.swiften.xtestkit.base.element.locator.general.type.BaseLocatorType;
import org.swiften.xtestkit.base.type.BaseErrorType;

import java.util.Date;

/**
 * This interface provides date-related actions, such as selecting a date/
 * month/year. However, the specific implementation is left to the individual
 * platforms.
 *
 * These methods assume that the user is in a calendar view.
 */
public interface BaseDateActionType extends BaseErrorType {
    //region Validation
    /**
     * Check if a {@link Date} is currently active. This assumes that the
     * user is in a calendar view.
     * @param param A {@link DateType} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    default Flowable<Boolean> rxHasDate(@NotNull DateType param) {
        return RxUtil.error(NOT_IMPLEMENTED);
    }
    //endregion

    //region Actions
    /**
     * Select a {@link Date}. This assumes that the user is in a calendar
     * view.
     * @param param A {@link DateType} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    default Flowable<Boolean> rxSelectDate(@NotNull DateType param) {
        return RxUtil.error(NOT_IMPLEMENTED);
    }

    /**
     * Select a year based on the {@link Date} from a {@link DateType} instance.
     * @param param A {@link DateType} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    default Flowable<Boolean> rxSelectYear(@NotNull DateType param) {
        return RxUtil.error(NOT_IMPLEMENTED);
    }

    /**
     * Select a month based on the {@link Date} from a {@link DateType} instance.
     * @param param A {@link DateType} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    default Flowable<Boolean> rxSelectMonth(@NotNull DateType param) {
        return RxUtil.error(NOT_IMPLEMENTED);
    }

    /**
     * Select a day based on the {@link Date} from a {@link DateType} instance.
     * @param param A {@link DateType} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    default Flowable<Boolean> rxSelectDay(@NotNull DateType param) {
        return RxUtil.error(NOT_IMPLEMENTED);
    }
    //endregion

    //region Elements
    /**
     * Get all calendar {@link WebElement}. This assumes that the user is in a
     * calendar view.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    default Flowable<WebElement> rxAllCalendarElements() {
        return RxUtil.error(NOT_IMPLEMENTED);
    }

    /**
     * Get the currently displayed {@link Date}, assuming the user is in a
     * calendar view.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    default Flowable<Date> rxDisplayedDate() {
        return RxUtil.error(NOT_IMPLEMENTED);
    }
    //endregion
}
