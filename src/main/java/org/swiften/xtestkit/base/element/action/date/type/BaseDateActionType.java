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
     * Check if {@link Date} is currently active. This assumes that the
     * user is in a calendar view.
     * @param param {@link DateType} instance.
     * @return {@link Flowable} instance.
     */
    @NotNull
    default Flowable<Boolean> rx_hasDate(@NotNull DateType param) {
        return RxUtil.error(NOT_AVAILABLE);
    }
    //endregion

    //region Actions
    /**
     * Select {@link Date}. This assumes that the user is in a calendar
     * view.
     * @param param {@link DateType} instance.
     * @return {@link Flowable} instance.
     */
    @NotNull
    default Flowable<Boolean> rx_selectDate(@NotNull DateType param) {
        return RxUtil.error(NOT_AVAILABLE);
    }

    /**
     * Select a year based on the {@link Date} from {@link DateType} instance.
     * @param param {@link DateType} instance.
     * @return {@link Flowable} instance.
     */
    @NotNull
    default Flowable<Boolean> rx_selectYear(@NotNull DateType param) {
        return RxUtil.error(NOT_AVAILABLE);
    }

    /**
     * Select a month based on the {@link Date} from {@link DateType} instance.
     * @param param {@link DateType} instance.
     * @return {@link Flowable} instance.
     */
    @NotNull
    default Flowable<Boolean> rx_selectMonth(@NotNull DateType param) {
        return RxUtil.error(NOT_AVAILABLE);
    }

    /**
     * Select a day based on the {@link Date} from {@link DateType} instance.
     * @param param {@link DateType} instance.
     * @return {@link Flowable} instance.
     */
    @NotNull
    default Flowable<Boolean> rx_selectDay(@NotNull DateType param) {
        return RxUtil.error(NOT_AVAILABLE);
    }
    //endregion

    //region Elements
    /**
     * Get all calendar {@link WebElement}. This assumes that the user is in a
     * calendar view.
     * @return {@link Flowable} instance.
     */
    @NotNull
    default Flowable<WebElement> rxAllCalendarElements() {
        return RxUtil.error(NOT_AVAILABLE);
    }

    /**
     * Get the currently displayed {@link Date}, assuming the user is in a
     * calendar view.
     * @return {@link Flowable} instance.
     */
    @NotNull
    default Flowable<Date> rxDisplayedDate() {
        return RxUtil.error(NOT_AVAILABLE);
    }
    //endregion

    /**
     * Get the day formatted as {@link String}.
     * @param param {@link DateType} instance.
     * @return {@link String} value.
     * @see #NOT_AVAILABLE
     */
    @NotNull
    default String dayString(@NotNull DateType param) {
        throw new RuntimeException(NOT_AVAILABLE);
    }

    /**
     * Get the month formatted as {@link String}.
     * @param param {@link DateType} instance.
     * @return {@link String} value.
     * @see #NOT_AVAILABLE
     */
    @NotNull
    default String monthString(@NotNull DateType param) {
        throw new RuntimeException(NOT_AVAILABLE);
    }

    /**
     * Get the year formatted as {@link String}.
     * @param param {@link DateType} instance.
     * @return {@link String} value.
     * @see #NOT_AVAILABLE
     */
    @NotNull
    default String yearString(@NotNull DateType param) {
        throw new RuntimeException(NOT_AVAILABLE);
    }

    /**
     * Get {@link String} representation of {@link DateType#value()}.
     * @param param {@link DateType} instance.
     * @return {@link String} value.
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
