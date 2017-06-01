package org.swiften.xtestkit.android.element.date;

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
import org.swiften.xtestkit.base.element.click.BaseClickActionType;
import org.swiften.xtestkit.base.element.date.CalendarUnit;
import org.swiften.xtestkit.base.element.date.BaseDateActionType;
import org.swiften.xtestkit.base.element.date.DateType;
import org.swiften.xtestkit.base.element.general.Unidirection;
import org.swiften.xtestkit.base.element.general.BaseActionType;
import org.swiften.xtestkit.base.element.swipe.SwipeType;
import org.swiften.xtestkit.base.element.swipe.SwipeRepeatType;
import org.swiften.xtestkit.base.element.locator.type.BaseLocatorType;
import org.swiften.xtestkit.base.element.locator.xpath.Attribute;
import org.swiften.xtestkit.base.element.locator.xpath.XPath;
import org.swiften.xtestkit.base.element.locator.param.ByXPath;
import org.swiften.xtestkit.base.type.PlatformType;
import org.swiften.xtestkit.mobile.element.action.general.MobileActionType;
import org.swiften.xtestkit.mobile.element.action.swipe.MobileSwipeType;

import java.util.Date;

/**
 * This interface provides actions for {@link AndroidDatePickerType#VERTICAL_CALENDAR}.
 */
public interface CalendarPickerActionType extends
    BaseClickActionType,
    BaseDateActionType<AndroidDriver<AndroidElement>>,
    BaseLocatorType<AndroidDriver<AndroidElement>>,
    MobileActionType<AndroidDriver<AndroidElement>>,
    BaseActionType<AndroidDriver<AndroidElement>>,
    MobileSwipeType<AndroidDriver<AndroidElement>>
{
    /**
     * Select a day if the app is using {@link AndroidDatePickerType#VERTICAL_CALENDAR}.
     * We need to define {@link Attribute} with "content-desc", and
     * repeatedly search for the correct day until it is found. However, even
     * if the day is found, Appium could still select the wrong element -
     * so in this case an additional iteration is required.
     * This is called the calibration phase because year selection should
     * have brought the picker close to the date we want.
     * @param PARAM {@link DateType} instance.
     * @return {@link Flowable} instance.
     * @see DateType#date()
     * @see DateType#dateString(String)
     * @see #platform()
     * @see Attribute#single(String)
     * @see XPath.ContainsString#stringFormat()
     * @see XPath.Builder#appendAttribute(Attribute, String)
     * @see SwipeRepeatType#rxa_performAction()
     * @see DateUtil#notEarlierThan(Date, Date)
     */
    @NotNull
    default Flowable<Boolean> rx_calibrateDate(@NotNull final DateType PARAM) {
        final CalendarPickerActionType THIS = this;
        final Date DATE = PARAM.date();
        PlatformType platform = platform();

        /* dd MMMM YYYY is the format accepted by the content-desc property */
        final String DATE_STRING = PARAM.dateString("dd MMMM YYYY");

        /* Weirdly enough, the individual view element that contains the day
         * values use content description to store the day */
        Attribute attr = Attribute.single("content-desc");
        String format = ((XPath.ContainsString) () -> DATE_STRING).stringFormat();
        XPath.ContainsString defFormat = () -> "01";
        XPath xp = XPath.builder(platform).appendAttribute(attr, format).build();
        XPath dxp = XPath.builder(platform).appendAttribute(attr, defFormat).build();
        final ByXPath QUERY = ByXPath.builder().withXPath(xp).withRetries(0).build();
        final ByXPath DEF_QUERY = ByXPath.builder().withXPath(dxp).withRetries(0).build();

        SwipeRepeatType repeater = new SwipeRepeatType() {
            @NotNull
            @Override
            public Flowable<Double> rxe_elementSwipeRatio() {
                /* We need the scroll ratio to be higher because the calendar
                 * day view tends to snap into place if the scroll/swipe motion
                 * is not strong enough, which, sometimes, may lead to wrong
                 * page in focus.
                 *
                 * Based on empirical tests, it seems 0.7-0.8 are a good
                 * ratios. Amend if necessary */
                return Flowable.just(0.7d);
            }

            @NotNull
            @Override
            public Flowable<Boolean> rxv_shouldKeepSwiping() {
                /* Since there is no way to check the current month in focus,
                 * we need to use a crude workaround. Every time the list view
                 * is scrolled to a new page/the previous page, click on the
                 * first day element in order to update the displayed date.
                 * We can then use rxe_displayedDate to check */
                return THIS.rxe_byXPath(DEF_QUERY)
                    .firstElement()
                    .toFlowable()
                    .flatMap(THIS::rxa_click)
                    .flatMap(a -> THIS.rxe_byXPath(QUERY))
                    .flatMap(THIS::rxa_click)
                    .flatMap(a -> rxv_hasDate(PARAM))
                    .filter(BooleanUtil::isTrue);
            }

            @NotNull
            @Override
            public Flowable<WebElement> rxe_scrollableViewToSwipe() {
                return rxe_pickerView(PARAM, CalendarUnit.MONTH);
            }

            @NotNull
            @Override
            public Flowable<Unidirection> rx_directionToSwipe() {
                /* We use month to compare because the month and day views
                 * are intertwined in VERTICAL_CALENDAR mode */
                return rxe_displayedDate(PARAM)
                    .map(a -> DateUtil.notEarlierThan(a, DATE, CalendarUnit.DAY.value()))
                    .map(Unidirection::vertical);
            }

            @Override
            public void swipeOnce(@NotNull SwipeType param) {
                THIS.swipeOnce(param);
            }
        };

        return repeater.rxa_performAction();
    }
}
