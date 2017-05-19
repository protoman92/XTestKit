package org.swiften.xtestkit.base.element.action.date.type;

/**
 * Created by haipham on 5/8/17.
 */

import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.rx.RxUtil;
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
    default Flowable<Boolean> rx_hasDate(@NotNull DateType param) {
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
    default Flowable<Boolean> rx_selectDate(@NotNull DateType param) {
        return RxUtil.error(NOT_IMPLEMENTED);
    }

    /**
     * Select a year based on the {@link Date} from a {@link DateType} instance.
     * @param param A {@link DateType} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    default Flowable<Boolean> rx_selectYear(@NotNull DateType param) {
        return RxUtil.error(NOT_IMPLEMENTED);
    }

    /**
     * Select a month based on the {@link Date} from a {@link DateType} instance.
     * @param param A {@link DateType} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    default Flowable<Boolean> rx_selectMonth(@NotNull DateType param) {
        return RxUtil.error(NOT_IMPLEMENTED);
    }

    /**
     * Select a day based on the {@link Date} from a {@link DateType} instance.
     * @param param A {@link DateType} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    default Flowable<Boolean> rx_selectDay(@NotNull DateType param) {
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

    /**
     * Get the day formatted as a {@link String}.
     * @param param A {@link DateType} instance.
     * @return A {@link String} value.
     * @see #NOT_IMPLEMENTED
     */
    @NotNull
    default String dayString(@NotNull DateType param) {
        throw new RuntimeException(NOT_IMPLEMENTED);
    }

    /**
     * Get the month formatted as a {@link String}.
     * @param param A {@link DateType} instance.
     * @return A {@link String} value.
     * @see #NOT_IMPLEMENTED
     */
    @NotNull
    default String monthString(@NotNull DateType param) {
        throw new RuntimeException(NOT_IMPLEMENTED);
    }

    /**
     * Get the year formatted as a {@link String}.
     * @param param A {@link DateType} instance.
     * @return A {@link String} value.
     * @see #NOT_IMPLEMENTED
     */
    @NotNull
    default String yearString(@NotNull DateType param) {
        throw new RuntimeException(NOT_IMPLEMENTED);
    }

    /**
     * Get a {@link String} representation of a {@link DateType#value()}.
     * @param param A {@link DateType} instance.
     * @return A {@link String} value.
     * @see #dateString(DateType)
     * @see #monthString(DateType)
     * @see #yearString(DateType)
     */
    @NotNull
    default String dateString(@NotNull DateType param) {
        return String.format(
            "Day: %s, Month: %s, Year: %s",
            dayString(param),
            monthString(param),
            yearString(param)
        );
    }
}
