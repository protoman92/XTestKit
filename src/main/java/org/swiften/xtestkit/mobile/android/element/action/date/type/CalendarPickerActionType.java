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
import org.swiften.xtestkit.base.element.action.click.BaseClickActionType;
import org.swiften.xtestkit.base.element.action.date.CalendarElement;
import org.swiften.xtestkit.base.element.action.date.type.BaseDateActionType;
import org.swiften.xtestkit.base.element.action.date.type.DateType;
import org.swiften.xtestkit.base.element.action.general.model.Unidirection;
import org.swiften.xtestkit.base.element.action.general.type.BaseActionType;
import org.swiften.xtestkit.base.element.action.swipe.type.SwipeType;
import org.swiften.xtestkit.base.element.action.swipe.type.SwipeRepeatType;
import org.swiften.xtestkit.base.element.locator.general.type.BaseLocatorType;
import org.swiften.xtestkit.base.element.locator.general.xpath.Attribute;
import org.swiften.xtestkit.base.element.locator.general.xpath.XPath;
import org.swiften.xtestkit.base.element.locator.general.param.ByXPath;
import org.swiften.xtestkit.base.element.property.type.sub.OfClassType;
import org.swiften.xtestkit.mobile.android.AndroidView;
import org.swiften.xtestkit.mobile.android.type.DatePickerContainerType;
import org.swiften.xtestkit.mobile.element.action.general.type.MobileActionType;
import org.swiften.xtestkit.mobile.element.action.swipe.type.MobileSwipeType;

import java.util.Date;

/**
 * This interface provides actions for
 * {@link DatePickerContainerType.DatePickerType#CALENDAR}.
 */
public interface CalendarPickerActionType extends
    BaseClickActionType,
    BaseDateActionType,
    BaseLocatorType<AndroidDriver<AndroidElement>>,
    MobileActionType<AndroidDriver<AndroidElement>>,
    BaseActionType<AndroidDriver<AndroidElement>>,
    MobileSwipeType<AndroidDriver<AndroidElement>>
{
    /**
     * Get the calendar list view. Applicable to
     * {@link DatePickerContainerType.DatePickerType#CALENDAR}.
     * @return A {@link Flowable} instance.
     * @see #rxElementOfClass(OfClassType[])
     */
    @NotNull
    default Flowable<WebElement> rxCalendarListView() {
        return rxElementOfClass(AndroidView.ViewType.LIST_VIEW.className());
    }

    /**
     * Select a day if the app is using
     * {@link DatePickerContainerType.DatePickerType#CALENDAR}.
     * We need to define an {@link Attribute} with "content-desc", and
     * repeatedly search for the correct day until it is found. However, even
     * if the day is found, Appium could still select the wrong element -
     * so in this case an additional iteration is required.
     * This is called the calibration phase because
     * {@link #rx_selectYear(DateType)} should have brought the picker close
     * to the date we want.
     * @param PARAM A {@link DateType} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    default Flowable<Boolean> rxCalibrateDate(@NotNull final DateType PARAM) {
        final CalendarPickerActionType THIS = this;
        final Date DATE = PARAM.value();

        /* dd MMMM YYYY is the format accepted by the content-desc property */
        final String DATE_STRING = PARAM.dateString("dd MMMM YYYY");

        /* Weirdly enough, the individual view element that contains the day
         * values use content description to store the day */
        Attribute contentDesc = Attribute.single("content-desc");
        String format = ((XPath.ContainsString) () -> DATE_STRING).stringFormat();

        XPath xPath = XPath.builder(platform())
            .appendAttribute(contentDesc, format)
            .build();

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

        SwipeRepeatType repeater = new SwipeRepeatType() {
            @Override
            public double elementSwipeRatio() {
                /* We need the scroll ratio to be higher because the calendar
                 * day view tends to snap into place if the scroll/swipe motion
                 * is not strong enough, which, sometimes, may lead to wrong
                 * page in focus.
                 *
                 * Based on empirical tests, it seems 0.7-0.8 are a good
                 * ratios. Amend if necessary */
                return 0.7d;
            }

            @NotNull
            @Override
            public Flowable<Boolean> rx_shouldKeepSwiping() {
                /* Since there is no way to check the current month in focus,
                 * we need to use a crude workaround. Every time the list view
                 * is scrolled to a new page/the previous page, click on the
                 * first day element in order to update the displayed date.
                 * We can then use rxDisplayedDate to check */
                return THIS.rxElementByXPath(DEFAULT_BY_XPATH)
                    .flatMap(THIS::rx_click)
                    .flatMap(a -> THIS.rxElementsByXPath(BY_XPATH))
                    .flatMap(THIS::rx_click)
                    .flatMap(a -> rx_hasDate(PARAM))
                    .filter(BooleanUtil::isTrue);
            }

            @NotNull
            @Override
            public Flowable<WebElement> rx_scrollableViewToSwipe() {
                return rxCalendarListView();
            }

            @NotNull
            @Override
            public Flowable<Unidirection> rxDirectionToSwipe() {
                /* We use month to compare because the month and day views
                 * are intertwined in CALENDAR mode */
                return rxDisplayedDate()
                    .map(a -> DateUtil.notEarlierThan(
                        a, DATE, CalendarElement.DAY.value()
                    ))
                    .map(Unidirection::vertical);
            }

            @Override
            public void swipeOnce(@NotNull SwipeType param) {
                THIS.swipeOnce(param);
            }
        };

        return repeater.rx_repeatSwipe();
    }
}
