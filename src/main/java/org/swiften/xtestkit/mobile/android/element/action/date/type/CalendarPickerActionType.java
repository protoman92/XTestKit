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
import org.swiften.xtestkit.base.element.action.date.CalendarUnit;
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
import org.swiften.xtestkit.base.type.PlatformType;
import org.swiften.xtestkit.mobile.android.AndroidView;
import org.swiften.xtestkit.mobile.element.action.general.type.MobileActionType;
import org.swiften.xtestkit.mobile.element.action.swipe.type.MobileSwipeType;

import java.util.Date;

/**
 * This interface provides actions for {@link AndroidDatePickerType#CALENDAR}.
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
     * Select a day if the app is using {@link AndroidDatePickerType#CALENDAR}.
     * We need to define {@link Attribute} with "content-desc", and
     * repeatedly search for the correct day until it is found. However, even
     * if the day is found, Appium could still select the wrong element -
     * so in this case an additional iteration is required.
     * This is called the calibration phase because
     * {@link #rx_selectYear(DateType)} should have brought the picker close
     * to the date we want.
     * @param PARAM {@link DateType} instance.
     * @return {@link Flowable} instance.
     * @see DateType#value()
     * @see DateType#dateString(String)
     * @see #platform()
     * @see Attribute#single(String)
     * @see XPath.ContainsString#stringFormat()
     * @see XPath.Builder#appendAttribute(Attribute, String)
     * @see SwipeRepeatType#rx_repeatSwipe()
     * @see DateUtil#notEarlierThan(Date, Date)
     */
    @NotNull
    default Flowable<Boolean> rx_calibrateDate(@NotNull final DateType PARAM) {
        final CalendarPickerActionType THIS = this;
        final Date DATE = PARAM.value();
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
            public Flowable<Double> rx_elementSwipeRatio() {
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
            public Flowable<Boolean> rx_shouldKeepSwiping() {
                /* Since there is no way to check the current month in focus,
                 * we need to use a crude workaround. Every time the list view
                 * is scrolled to a new page/the previous page, click on the
                 * first day element in order to update the displayed date.
                 * We can then use rx_displayedDate to check */
                return THIS.rx_byXPath(DEF_QUERY)
                    .firstElement()
                    .toFlowable()
                    .flatMap(THIS::rx_click)
                    .flatMap(a -> THIS.rx_byXPath(QUERY))
                    .flatMap(THIS::rx_click)
                    .flatMap(a -> rx_hasDate(PARAM))
                    .filter(BooleanUtil::isTrue);
            }

            @NotNull
            @Override
            public Flowable<WebElement> rx_scrollableViewToSwipe() {
                return rx_listView(CalendarUnit.MONTH);
            }

            @NotNull
            @Override
            public Flowable<Unidirection> rxDirectionToSwipe() {
                /* We use month to compare because the month and day views
                 * are intertwined in CALENDAR mode */
                return rx_displayedDate()
                    .map(a -> DateUtil.notEarlierThan(a, DATE, CalendarUnit.DAY.value()))
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
