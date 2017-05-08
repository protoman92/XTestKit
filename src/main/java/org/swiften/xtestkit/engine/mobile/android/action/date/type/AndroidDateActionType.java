package org.swiften.xtestkit.engine.mobile.android.action.date.type;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.object.ObjectUtil;
import org.swiften.xtestkit.engine.base.action.date.type.BaseDateActionType;
import org.swiften.xtestkit.engine.base.action.date.type.DateType;
import org.swiften.xtestkit.engine.mobile.android.type.DateViewContainerType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by haipham on 5/8/17.
 */
public interface AndroidDateActionType extends
    BaseDateActionType<AndroidDriver<AndroidElement>>,
    DateViewContainerType
{
    /**
     * @param param A {@link DateType} instance.
     * @return A {@link Flowable} instance.
     * @see BaseDateActionType#rxSelectDate(DateType)
     */
    @NotNull
    @Override
    default Flowable<Boolean> rxSelectDate(@NotNull DateType param) {
        return Flowable.empty();
    }

    /**
     * @param param A {@link DateType} instance.
     * @return A {@link Flowable} instance.
     * @see BaseDateActionType#rxHasDate(DateType)
     */
    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default Flowable<Boolean> rxHasDate(@NotNull DateType param) {
        return Flowable
            .concatArray(
                rxElementContainingText(day(param)),
                rxElementContainingText(month(param)),
                rxElementContainingText(year(param))
            )
            .all(ObjectUtil::nonNull)
            .toFlowable();
    }

    /**
     * Get the date picker header, which displays the
     * @return A {@link Flowable} instance.
     */
    @NotNull
    default Flowable<WebElement> rxDatePickerHeader() {
        return rxElementContainingID("picker_header");
    }

    /**
     * Get the date picker month label.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    default Flowable<WebElement> rxDatePickerMonth() {
        return rxElementContainingID("date_picker_month");
    }

    /**
     * Get the date picker day label.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    default Flowable<WebElement> rxDatePickerDay() {
        return rxElementContainingID("date_picker_day");
    }

    /**
     * Get the date picker year label.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    default Flowable<WebElement> rxDatePickerYear() {
        return rxElementContainingID("date_picker_year");
    }

    /**
     * @return A {@link Flowable} instance.
     * @see BaseDateActionType#rxAllCalendarElements()
     */
    @NotNull
    @Override
    default Flowable<WebElement> rxAllCalendarElements() {
        Collection<Flowable<WebElement>> streams = new ArrayList<>();

        Collections.addAll(streams,
            rxDatePickerDay(),
            rxDatePickerMonth(),
            rxDatePickerYear()
        );

        /* If Calendar view mode is active, we need to add some additional
         * views to be found. E.g. A SimpleMonthView */
        if (dateViewType().isCalendarType()) {
            Collections.addAll(streams, rxSimpleMonthViews());
        }

        return Flowable.concat(streams);
    }

    /**
     * Get all
     * {@link org.swiften.xtestkit.engine.mobile.android.AndroidView.ViewType#SIMPLE_MONTH_VIEW}
     * {@link WebElement}.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    default Flowable<WebElement> rxSimpleMonthViews() {
        return rxElementOfClass("DayPickerView");
    }

    /**
     * Get the day, as formatted using {@link DateViewType#dayFormat()}.
     * @param param A {@link DateType} instance.
     * @return A {@link String} value.
     * @see DateViewType#dayFormat()
     */
    @NotNull
    default String day(@NotNull DateType param) {
        String format = dateViewType().dayFormat();
        return new SimpleDateFormat(format).format(param.value());
    }

    /**
     * Get the month, as formatted using {@link DateViewType#monthFormat()}.
     * @param param A {@link DateType} instance.
     * @return A {@link String} value.
     * @see DateViewType#monthFormat()
     */
    @NotNull
    default String month(@NotNull DateType param) {
        String format = dateViewType().monthFormat();
        return new SimpleDateFormat(format).format(param.value());
    }

    /**
     * Get the year, as formatted using {@link DateViewType#yearFormat()}.
     * @param param A {@link DateType} instance.
     * @return A {@link String} value.
     * @see DateViewType#yearFormat()
     */
    @NotNull
    default String year(@NotNull DateType param) {
        String format = dateViewType().yearFormat();
        return new SimpleDateFormat(format).format(param.value());
    }
}
