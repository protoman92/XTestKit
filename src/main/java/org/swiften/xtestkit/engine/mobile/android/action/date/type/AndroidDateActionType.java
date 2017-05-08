package org.swiften.xtestkit.engine.mobile.android.action.date.type;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.bool.BooleanUtil;
import org.swiften.javautilities.log.LogUtil;
import org.swiften.xtestkit.engine.base.action.date.type.BaseDateActionErrorType;
import org.swiften.xtestkit.engine.base.action.date.type.BaseDateActionType;
import org.swiften.xtestkit.engine.base.action.date.type.DateType;
import org.swiften.xtestkit.engine.mobile.android.type.DateViewContainerType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        Date date = param.value();
        DateViewType viewType = dateViewType();

        String dayFormat = viewType.dayFormat();
        String monthFormat = viewType.monthFormat();
        String yearFormat = viewType.yearFormat();

        DateFormat dayFormatter = new SimpleDateFormat(dayFormat);
        DateFormat monthFormatter = new SimpleDateFormat(monthFormat);
        DateFormat yearFormatter = new SimpleDateFormat(yearFormat);

        final String DAY = dayFormatter.format(date);
        final String MONTH = monthFormatter.format(date);
        final String YEAR = yearFormatter.format(date);

        LogUtil.println(DAY, MONTH, YEAR);

        return Flowable
            .concatArray(
                rxDatePickerDay()
                    .map(a -> a.getAttribute("@text"))
                    .map(a -> a.equals(DAY)),
                rxDatePickerMonth()
                    .map(a -> a.getAttribute("@text"))
                    .map(a -> a.equals(MONTH)),
                rxDatePickerYear()
                    .map(a -> a.getAttribute("@text"))
                    .map(a -> a.equals(YEAR))
            )
            .all(BooleanUtil::isTrue)
            .toFlowable();
    }

    /**
     * Get the date picker header, which displays the
     * @return A {@link Flowable} instance.
     */
    @NotNull
    default Flowable<WebElement> rxDatePickerHeader() {
        return rxElementContainingID("date_picker_header");
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
}
