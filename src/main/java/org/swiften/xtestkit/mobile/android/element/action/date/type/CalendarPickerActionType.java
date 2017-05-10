package org.swiften.xtestkit.mobile.android.element.action.date.type;

/**
 * Created by haipham on 5/10/17.
 */

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.bool.BooleanUtil;
import org.swiften.javautilities.date.DateUtil;
import org.swiften.javautilities.rx.RxUtil;
import org.swiften.xtestkit.base.element.action.date.CalendarElement;
import org.swiften.xtestkit.base.element.action.date.type.BaseDateActionType;
import org.swiften.xtestkit.base.element.action.date.type.DateType;
import org.swiften.xtestkit.base.element.action.general.model.Unidirection;
import org.swiften.xtestkit.base.element.locator.xpath.Attribute;
import org.swiften.xtestkit.base.element.locator.xpath.XPath;
import org.swiften.xtestkit.base.param.ByXPath;
import org.swiften.xtestkit.base.type.ClassContainerType;
import org.swiften.xtestkit.mobile.android.AndroidView;
import org.swiften.xtestkit.mobile.android.element.property.type.AndroidElementInteractionType;
import org.swiften.xtestkit.mobile.android.type.DatePickerContainerType;
import org.swiften.xtestkit.mobile.element.action.general.type.MobileActionType;

import java.util.Date;

/**
 * This interface provides actions for
 * {@link DatePickerContainerType.DatePickerType#CALENDAR}.
 */
public interface CalendarPickerActionType extends
    BaseAndroidDateActionType,
    BaseDateActionType<AndroidDriver<AndroidElement>>,
    AndroidElementInteractionType,
    MobileActionType<AndroidDriver<AndroidElement>>
{
    /**
     * Get the calendar list view. Applicable to
     * {@link DatePickerContainerType.DatePickerType#CALENDAR}.
     * @return A {@link Flowable} instance.
     * @see #rxElementOfClass(ClassContainerType)
     */
    @NotNull
    default Flowable<WebElement> rxCalendarListView() {
        return rxElementOfClass(AndroidView.ViewType.LIST_VIEW);
    }

    /**
     * Select a day if the app is using
     * {@link DatePickerContainerType.DatePickerType#CALENDAR}.
     * We need to define an {@link Attribute} with "content-desc", and
     * repeatedly search for the correct day until it is found. However, even
     * if the day is found, Appium could still select the wrong element -
     * so in this case an additional iteration is required.
     * This is called the calibration phase because
     * {@link #rxSelectYear(DateType)} should have brought the picker close
     * to the date we want.
     * @param PARAM A {@link DateType} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    default Flowable<Boolean> rxCalibrateDateForCalendar(@NotNull final DateType PARAM) {
        final CalendarPickerActionType THIS = this;
        final Date DATE = PARAM.value();

        /* dd MMMM YYYY is the format accepted by the content-desc property */
        final String DATE_STRING = PARAM.dateString("dd MMMM YYYY");

        /* Weirdly enough, the individual view element that contains the day
         * values use content description to store the day */
        Attribute contentDesc = Attribute.withSingleAttribute("content-desc");
        String format = ((XPath.ContainsString) () -> DATE_STRING).format();

        XPath xPath = XPath.builder(platform())
            .appendAttribute(contentDesc, format)
            .build();

        /* Since there is no way to check the current month in focus, we need
         * to use a crude workaround. Every time the list view is scrolled to
         * a new page/the previous page, click on the first day element in
         * order to update the displayed date. We can then use rxDisplayedDate
         * to check */
        final ByXPath BY_XPATH = ByXPath.builder()
            .withXPath(xPath)
            .withError(NO_SUCH_ELEMENT)
            .withRetryCount(0)
            .build();

        XPath defaultXP = XPath.builder(platform())
            .appendAttribute(contentDesc, ((XPath.ContainsString) () -> "01"))
            .build();

        final ByXPath DEFAULT_BY_XPATH = ByXPath.builder()
            .withXPath(defaultXP)
            .withError(NO_SUCH_ELEMENT)
            .withRetryCount(0)
            .build();

        /* We need the scroll ratio to be higher because the calendar day view
         * tends to snap into place if the scroll/swipe motion is not strong
         * enough, which, sometimes, may lead to wrong page in focus
         *
         * Based on empirical tests, it seems 0.7-0.8 are a good ratios. Amend
         * if necessary */
        class ScrollAndCheck {
            @NotNull
            @SuppressWarnings("WeakerAccess")
            Flowable<Boolean> repeat() {
                /* First step is to click on the first element in the current
                 * month view */
                return THIS.rxElementByXPath(DEFAULT_BY_XPATH)
                    .flatMap(THIS::rxClick)
                    .flatMap(a -> THIS.rxElementsByXPath(BY_XPATH))
                    .flatMap(THIS::rxClick)
                    .flatMap(a -> rxHasDate(PARAM))
                    .filter(BooleanUtil::isTrue)
                    .switchIfEmpty(RxUtil.error(""))
                    .onErrorResumeNext(Flowable.zip(
                        rxCalendarListView(),

                        /* We use month to compare because the month and day
                         * views are intertwined in CALENDAR mode */
                        rxDisplayedDate()
                            .map(a -> DateUtil.notEarlierThan(
                                a, DATE, CalendarElement.DAY.value()
                            ))
                            .map(Unidirection::vertical),

                        (element, direction) -> THIS.rxScrollPickerView(
                            element, direction, 0.7d
                        ))
                        .flatMap(a -> a)
                        .flatMap(a -> new ScrollAndCheck().repeat()));
            }
        }

        return new ScrollAndCheck().repeat();
    }
}
