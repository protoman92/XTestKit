package org.swiften.xtestkit.android.element.date;

/**
 * Created by haipham on 6/2/17.
 */

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.bool.HPBooleans;
import org.swiften.javautilities.date.HPDates;
import org.swiften.javautilities.object.HPObjects;
import org.swiften.xtestkit.android.element.locator.AndroidLocatorType;
import org.swiften.xtestkit.android.type.AndroidSDK;
import org.swiften.xtestkit.android.type.AndroidSDKProviderType;
import org.swiften.xtestkit.base.element.date.*;
import org.swiften.xtestkit.base.element.locator.ByXPath;
import org.swiften.xtestkit.base.element.swipe.MultiSwipeComparisonType;
import org.swiften.xtestkit.base.element.swipe.MultiSwipeType;
import org.swiften.xtestkit.base.element.swipe.SwipeParamType;
import org.swiften.xtestkit.ios.element.locator.AndroidXMLAttribute;
import org.swiften.xtestkit.mobile.Platform;
import org.swiften.xtestkit.mobile.element.action.general.MobileActionType;
import org.swiften.xtestkit.mobile.element.action.swipe.MobileSwipeType;
import org.swiften.xtestkitcomponents.direction.Direction;
import org.swiften.xtestkitcomponents.xpath.*;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This interface provides common methods to handle date/time selection for
 * {@link Platform#ANDROID}.
 * This specifically caters to {@link AndroidDatePickerType#isCalendar()}.
 */
public interface CalendarDateActionType extends
    AndroidLocatorType,
    AndroidSDKProviderType,
    DateActionType<AndroidDriver<AndroidElement>>,
    MobileActionType<AndroidDriver<AndroidElement>>,
    MobileSwipeType<AndroidDriver<AndroidElement>>
{
    /**
     * Override this method to provide default implementation.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     * @see DateActionType#rxa_openPicker(DateProviderType, CalendarUnit)
     * @see CalendarUnit#YEAR
     * @see #rxa_openYearPicker(DateProviderType)
     */
    @NotNull
    @Override
    default Flowable<Boolean> rxa_openPicker(@NotNull DateProviderType param,
                                             @NotNull CalendarUnit unit) {
        switch (unit) {
            case YEAR:
                return rxa_openYearPicker(param);

            default:
                return Flowable.just(true);
        }
    }

    /**
     * Override this method to provide default implementation.
     * @param param {@link DateProviderType} instance.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     * @see CalendarUnit#YEAR
     * @see CalendarUnit#DAY
     * @see #rxa_calibrateDate(DateProviderType)
     * @see #rxa_scrollAndSelect(DateProviderType, CalendarUnit)
     * @see #NOT_AVAILABLE
     */
    @NotNull
    @Override
    default Flowable<Boolean> rxa_select(@NotNull DateProviderType param,
                                         @NotNull CalendarUnit unit) {
        switch (unit) {
            case YEAR:
                return rxa_scrollAndSelect(param, CalendarUnit.YEAR);

            case DAY:
                /* In this case, the day selection is a bit different. We
                 * need to scroll the list view and check content-desc for
                 * the date String. We also need to continually click on a
                 * day to snap the list view into position */
                return rxa_calibrateDate(param);

            default:
                return Flowable.just(true);
        }
    }

    /**
     * Select a day if the app uses {@link AndroidDatePickerType#isCalendar()}.
     * We need to define {@link Attribute} with "content-desc", and
     * repeatedly search for the correct day until it is found. However, even
     * if the day is found, Appium could still select the wrong element -
     * so in this case an additional iteration is required.
     * This is called the calibration phase because year selection should
     * have brought the picker close to the date we want.
     * @param PARAM {@link DateProviderType} instance.
     * @return {@link Flowable} instance.
     * @see AndroidSDK#isAtLeastM()
     * @see AndroidXMLAttribute#CONTENT_DESC
     * @see #androidSDK()
     * @see #swipeOnce(SwipeParamType)
     * @see #clickFn()
     * @see #rxe_byXPath(ByXPath...)
     * @see #rxe_displayedDate(DateProviderType)
     * @see #rxe_pickerView(DateProviderType, CalendarUnit)
     * @see #rxv_hasDate(DateProviderType)
     */
    @NotNull
    default Flowable<Boolean> rxa_calibrateDate(@NotNull final DateProviderType PARAM) {
        final CalendarDateActionType THIS = this;
        final Date DATE = PARAM.date();

        /* dd MMMM yyyy is the format accepted by the content-desc property */
        final String DATE_STRING = PARAM.dateString("dd MMMM yyyy");

        /* Weirdly enough, the individual view element that contains the day
         * values use content description to store the day */
        Attribute<String> attr = Attribute.<String>builder()
            .addAttribute(AndroidXMLAttribute.CONTENT_DESC.value())
            .withFormatible(Formatibles.containsString())
            .withValue(DATE_STRING)
            .withJoiner(Joiner.OR)
            .withWrapper(Wrapper.NONE)
            .build();

        Attribute<String> defAttr = attr.withValue("01");
        XPath xp = XPath.builder().addAttribute(attr).build();
        XPath dxp = XPath.builder().addAttribute(defAttr).build();
        final ByXPath Q = ByXPath.builder().withXPath(xp).withRetries(0).build();
        final ByXPath DQ = ByXPath.builder().withXPath(dxp).withRetries(0).build();
        final boolean AT_LEAST_M = androidSDK().isAtLeastM();

        return new MultiSwipeType() {
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
            @SuppressWarnings("unchecked")
            public Flowable<Boolean> rxv_shouldKeepSwiping() {
                /* Since there is no way to check the current month in focus,
                 * we need to use a crude workaround. Every time the list view
                 * is scrolled to a new page/the previous page, click on the
                 * first day element in order to update the displayed date.
                 * We can then use rxe_displayedDate to check */
                return Flowable
                    .concatArrayDelayError(
                        Flowable
                            .concatArray(rxe_byXPath(DQ), rxe_byXPath(Q))
                            .compose(clickFn())
                            .all(HPObjects::nonNull)
                            .toFlowable(),

                        rxv_hasDate(PARAM)
                    )
                    .all(HPBooleans::isFalse)
                    .toFlowable()
                    .defaultIfEmpty(true)
                    .onErrorReturnItem(true);
            }

            @NotNull
            @Override
            public Flowable<WebElement> rxe_scrollableViewToSwipe() {
                return rxe_pickerView(PARAM, CalendarUnit.MONTH);
            }

            @NotNull
            @Override
            public Flowable<Direction> rxe_swipeDirection() {
                final int DAY = CalendarUnit.DAY.value();

                /* We use month to compare because the month and day views
                 * are intertwined in calendar mode */
                return rxe_displayedDate(PARAM)
                    .map(a -> HPDates.notEarlierThan(a, DATE, DAY))
                    .map(a -> {
                        if (AT_LEAST_M) {
                            return Direction.horizontal(a);
                        } else {
                            return Direction.vertical(a);
                        }
                    });
            }

            @Override
            public void swipeOnce(@NotNull SwipeParamType param) {
                THIS.swipeOnce(param);
            }
        }.rxa_performAction();
    }

    /**
     * Open the year picker.
     * @param param {@link DateProviderType} instance.
     * @return {@link Flowable} instance.
     * @see HPBooleans#toTrue(Object)
     * @see CalendarUnit#YEAR
     * @see #clickFn()
     * @see #rxe_elementLabel(DateProviderType, CalendarUnit)
     */
    @NotNull
    default Flowable<Boolean> rxa_openYearPicker(@NotNull DateProviderType param) {
        return rxe_elementLabel(param, CalendarUnit.YEAR)
            .compose(clickFn())
            .map(HPBooleans::toTrue);
    }

    /**
     * Select a component by scrolling until the component {@link String} is
     * visible.
     * @param PARAM {@link DateProviderType} instance.
     * @param UNIT {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     * @see Attributes#containsText(String)
     * @see #displayString(DateProviderType, CalendarUnit)
     * @see #getText(WebElement)
     * @see #platform()
     * @see #clickFn()
     * @see #rxe_pickerView(DateProviderType, CalendarUnit)
     */
    @NotNull
    default Flowable<Boolean> rxa_scrollAndSelect(@NotNull final DateProviderType PARAM,
                                                  @NotNull final CalendarUnit UNIT) {
        final CalendarDateActionType THIS = this;
        final String CP_STRING = displayString(PARAM, UNIT);
        final int COMPONENT = PARAM.component(UNIT);
        String format = PARAM.datePickerType().valueStringFormat(UNIT);
        final SimpleDateFormat FORMATTER = new SimpleDateFormat(format);
        Attributes attrs = Attributes.of(this);

        XPath xpath = PARAM.datePickerType().targetItemXP(UNIT)
            .addToEach(attrs.containsText(CP_STRING));

        /* We need a custom ByXPath because we want to limit the retry
         * count. Otherwise, the scroll action will take quite a long time as
         * we need to recursively scroll and check for the existence of the
         * element. Consider this trade-off between time and accuracy */
        final ByXPath QUERY = ByXPath.builder()
            .withXPath(xpath)
            .withRetries(1)
            .build();

        /* This method is needed because sometimes Appium cannot correctly
         * detect the {@link WebElement} that contains the text we are looking
         * for - as a result, it will continue scrolling in the same direction
         * forever. With this method, even if the correct {@link WebElement}
         * is scrolled past, it will again come into focus (even several times
         * if needed), and eventually the element will be detected. */
        return new MultiSwipeComparisonType() {
            @NotNull
            @Override
            public Flowable<Integer> rxe_initialDifference(@NotNull WebElement element) {
                return Flowable.just(element)
                    .map(THIS::getText)
                    .map(FORMATTER::parse)
                    .map(a -> DateParam.builder()
                        .withDateProvider(PARAM).withDate(a))
                    .map(DateParam.Builder::build)
                    .map(a -> a.component(UNIT))
                    .map(a -> a - COMPONENT);
            }

            @NotNull
            @Override
            public Flowable<?> rxa_compareFirst(@NotNull WebElement element) {
                return Flowable.just(element)
                    .map(THIS::getText)
                    .map(FORMATTER::parse)
                    .map(a -> DateParam.builder()
                        .withDateProvider(PARAM).withDate(a))
                    .map(DateParam.Builder::build)
                    .map(a -> a.component(UNIT))
                    .filter(a -> a > COMPONENT);
            }

            @NotNull
            @Override
            public Flowable<?> rxa_compareLast(@NotNull WebElement element) {
                return Flowable.just(element)
                    .map(THIS::getText)
                    .map(FORMATTER::parse)
                    .map(a -> DateParam.builder()
                        .withDateProvider(PARAM).withDate(a))
                    .map(DateParam.Builder::build)
                    .map(a -> a.component(UNIT))
                    .filter(a -> a < COMPONENT);
            }

            @NotNull
            @Override
            public Flowable<WebElement> rxe_scrollViewChildItems() {
                return THIS.rxe_listViewItems(PARAM, UNIT);
            }

            @NotNull
            @Override
            public Flowable<Double> rxe_elementSwipeRatio() {
                return Flowable.just(0.5d);
            }

            @NotNull
            @Override
            public Flowable<Boolean> rxv_shouldKeepSwiping() {
                return THIS.rxe_byXPath(QUERY)
                    /* Sometimes the driver will get the wrong element. In
                     * this case, keep scrolling so that in the next scroll,
                     * the element we are interested in gets more focus and
                     * is easier to detect. This is why we need to keep the
                     * scroll low in order to catch potential oddities like
                     * this */
                    .filter(a -> THIS.getText(a).equals(CP_STRING))
                    .compose(THIS.clickFn())
                    .map(HPBooleans::isFalse)
                    .defaultIfEmpty(true)
                    .onErrorReturnItem(true);
            }

            @NotNull
            @Override
            public Flowable<WebElement> rxe_scrollableViewToSwipe() {
                return THIS.rxe_pickerView(PARAM, UNIT);
            }

            @Override
            public void swipeOnce(@NotNull SwipeParamType param) {
                THIS.swipeOnce(param);
            }
        }.rxa_performAction();
    }

    /**
     * Get the list view items that corresponds to {@link CalendarUnit}. This
     * assumes the user is already in a picker view.
     * @param param {@link DateProviderType} instance.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     * @see #rxe_withXPath(XPath...)
     */
    @NotNull
    default Flowable<WebElement> rxe_listViewItems(@NotNull DateProviderType param,
                                                   @NotNull CalendarUnit unit) {
        DatePickerType pickerType = param.datePickerType();
        return rxe_withXPath(pickerType.pickerItemXP(unit));
    }
}
