package org.swiften.xtestkit.android.element.date;

/**
 * Created by haipham on 6/2/17.
 */

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.bool.BooleanUtil;
import org.swiften.javautilities.date.DateUtil;
import org.swiften.xtestkit.android.element.locator.AndroidLocatorType;
import org.swiften.xtestkit.base.element.date.*;
import org.swiften.xtestkitcomponents.direction.Direction;
import org.swiften.xtestkit.base.element.locator.param.ByXPath;
import org.swiften.xtestkit.base.element.swipe.MultiSwipeComparisonType;
import org.swiften.xtestkit.base.element.swipe.MultiSwipeType;
import org.swiften.xtestkit.base.element.swipe.SwipeType;
import org.swiften.xtestkit.mobile.element.action.general.MobileActionType;
import org.swiften.xtestkit.mobile.element.action.swipe.MobileSwipeType;
import org.swiften.xtestkit.mobile.Platform;
import org.swiften.xtestkitcomponents.platform.PlatformType;
import org.swiften.xtestkitcomponents.xpath.Attribute;
import org.swiften.xtestkitcomponents.xpath.Attributes;
import org.swiften.xtestkitcomponents.xpath.XPath;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This interface provides common methods to handle date/time selection for
 * {@link Platform#ANDROID}.
 * This specifically caters to {@link AndroidDatePickerType#isCalendar()}.
 */
public interface CalendarDateActionType extends
    AndroidLocatorType,
    DateActionType<AndroidDriver<AndroidElement>>,
    MobileActionType<AndroidDriver<AndroidElement>>,
    MobileSwipeType<AndroidDriver<AndroidElement>>
{
    /**
     * Override this method to provide default implementation.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     * @see DateActionType#rxa_openPicker(DateType, CalendarUnit)
     * @see CalendarUnit#YEAR
     * @see #rxa_openYearPicker(DateType)
     */
    @NotNull
    @Override
    default Flowable<Boolean> rxa_openPicker(@NotNull DateType param,
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
     * @param param {@link DateType} instance.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     * @see CalendarUnit#YEAR
     * @see CalendarUnit#DAY
     * @see #rxa_calibrateDate(DateType)
     * @see #rxa_scrollAndSelect(DateType, CalendarUnit)
     * @see #NOT_AVAILABLE
     */
    @NotNull
    @Override
    default Flowable<Boolean> rxa_select(@NotNull DateType param,
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
     * @param PARAM {@link DateType} instance.
     * @return {@link Flowable} instance.
     * @see Attribute.Builder#addAttribute(String)
     * @see Attribute.Builder#withFormatible(Attribute.Formatible)
     * @see Attribute.Builder#withJoiner(Attribute.Joiner)
     * @see Attribute.Builder#withValue(Object)
     * @see Attribute.Builder#withWrapper(Attribute.Wrapper)
     * @see Attribute.Joiner#OR
     * @see Attribute.Wrapper#NONE
     * @see Attribute#withValue(Object)
     * @see BooleanUtil#isTrue(boolean)
     * @see ByXPath.Builder#withXPath(XPath)
     * @see ByXPath.Builder#withRetries(int)
     * @see CalendarUnit#MONTH
     * @see CalendarUnit#DAY
     * @see CalendarUnit#value()
     * @see DateType#date()
     * @see DateType#dateString(String)
     * @see DateUtil#notEarlierThan(Date, Date)
     * @see MultiSwipeType#rxa_performAction()
     * @see Direction#vertical(boolean)
     * @see XPath.Builder#addAttribute(Attribute)
     * @see #swipeOnce(SwipeType)
     * @see #rxa_click(WebElement)
     * @see #rxe_byXPath(ByXPath...)
     * @see #rxe_displayedDate(DateType)
     * @see #rxe_pickerView(DateType, CalendarUnit)
     * @see #rxv_hasDate(DateType)
     */
    @NotNull
    default Flowable<Boolean> rxa_calibrateDate(@NotNull final DateType PARAM) {
        final CalendarDateActionType THIS = this;
        final Date DATE = PARAM.date();

        /* dd MMMM YYYY is the format accepted by the content-desc property */
        final String DATE_STRING = PARAM.dateString("dd MMMM YYYY");

        /* Weirdly enough, the individual view element that contains the day
         * values use content description to store the day */
        Attribute<String> attr = Attribute.<String>builder()
            .addAttribute("content-desc")
            .withFormatible(new Attributes.ContainsString() {})
            .withValue(DATE_STRING)
            .withJoiner(Attribute.Joiner.OR)
            .withWrapper(Attribute.Wrapper.NONE)
            .build();

        Attribute<String> defAttr = attr.withValue("01");
        XPath xp = XPath.builder().addAttribute(attr).build();
        XPath dxp = XPath.builder().addAttribute(defAttr).build();

        final ByXPath QUERY = ByXPath.builder()
            .withXPath(xp)
            .withRetries(0)
            .build();

        final ByXPath DEF_QUERY = ByXPath.builder()
            .withXPath(dxp)
            .withRetries(0)
            .build();

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
            public Flowable<Direction> rxe_swipeDirection() {
                /* We use month to compare because the month and day views
                 * are intertwined in CALENDAR mode */
                return rxe_displayedDate(PARAM)
                    .map(a -> DateUtil.notEarlierThan(a, DATE, CalendarUnit.DAY.value()))
                    .map(Direction::vertical);
            }

            @Override
            public void swipeOnce(@NotNull SwipeType param) {
                THIS.swipeOnce(param);
            }
        }.rxa_performAction();
    }

    /**
     * Open the year picker.
     * @param param {@link DateType} instance.
     * @return {@link Flowable} instance.
     * @see BooleanUtil#toTrue(Object)
     * @see CalendarUnit#YEAR
     * @see #rxa_click(WebElement)
     * @see #rxe_elementLabel(DateType, CalendarUnit)
     */
    @NotNull
    default Flowable<Boolean> rxa_openYearPicker(@NotNull DateType param) {
        final CalendarDateActionType THIS = this;

        return rxe_elementLabel(param, CalendarUnit.YEAR)
            .flatMap(THIS::rxa_click)
            .map(BooleanUtil::toTrue);
    }

    /**
     * Select a component by scrolling until the component {@link String} is
     * visible.
     * @param PARAM {@link DateType} instance.
     * @param UNIT {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     * @see Attributes#containsText(String)
     * @see Attributes#of(PlatformType)
     * @see BooleanUtil#toTrue(Object)
     * @see ByXPath.Builder#withXPath(XPath)
     * @see ByXPath.Builder#withRetries(int)
     * @see DatePickerType#valueStringFormat(CalendarUnit)
     * @see DatePickerType#targetItemXP(CalendarUnit)
     * @see DateParam.Builder#withDate(Date)
     * @see DateParam.Builder#withDateType(DateType)
     * @see DateType#component(CalendarUnit)
     * @see DateType#datePickerType()
     * @see MultiSwipeType#rxa_performAction()
     * @see XPath.Builder#addAttribute(Attribute)
     * @see #displayString(DateType, CalendarUnit)
     * @see #getText(WebElement)
     * @see #platform()
     * @see #rxa_click(WebElement)
     * @see #rxe_pickerView(DateType, CalendarUnit)
     */
    @NotNull
    default Flowable<Boolean> rxa_scrollAndSelect(@NotNull final DateType PARAM,
                                                  @NotNull final CalendarUnit UNIT) {
        final CalendarDateActionType THIS = this;
        final String CP_STRING = displayString(PARAM, UNIT);
        final int COMPONENT = PARAM.component(UNIT);
        String format = PARAM.datePickerType().valueStringFormat(UNIT);
        final SimpleDateFormat FORMATTER = new SimpleDateFormat(format);

        PlatformType platform = platform();
        Attributes attrs = Attributes.of(platform);

        XPath xPath = PARAM.datePickerType().targetItemXP(UNIT)
            .addToEach(attrs.containsText(CP_STRING));

        /* We need a custom ByXPath because we want to limit the retry
         * count. Otherwise, the scroll action will take quite a long time as
         * we need to recursively scroll and check for the existence of the
         * element. Consider this trade-off between time and accuracy */
        final ByXPath QUERY = ByXPath.builder()
            .withXPath(xPath)
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
                    .map(a -> DateParam.builder().withDateType(PARAM).withDate(a))
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
                    .map(a -> DateParam.builder().withDateType(PARAM).withDate(a))
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
                    .map(a -> DateParam.builder().withDateType(PARAM).withDate(a))
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
                    .flatMap(THIS::rxa_click)
                    .map(BooleanUtil::toTrue);
            }

            @NotNull
            @Override
            public Flowable<WebElement> rxe_scrollableViewToSwipe() {
                return THIS.rxe_pickerView(PARAM, UNIT);
            }

            @Override
            public void swipeOnce(@NotNull SwipeType param) {
                THIS.swipeOnce(param);
            }
        }.rxa_performAction();
    }

    /**
     * Get the list view items that corresponds to {@link CalendarUnit}. This
     * assumes the user is already in a picker view.
     * @param param {@link DateType} instance.
     * @param unit {@link CalendarUnit} instance.
     * @return {@link Flowable} instance.
     * @see DateType#datePickerType()
     * @see DatePickerType#pickerItemXP(CalendarUnit)
     * @see #rxe_withXPath(XPath...)
     */
    @NotNull
    default Flowable<WebElement> rxe_listViewItems(@NotNull DateType param,
                                                   @NotNull CalendarUnit unit) {
        DatePickerType pickerType = param.datePickerType();
        return rxe_withXPath(pickerType.pickerItemXP(unit));
    }
}
