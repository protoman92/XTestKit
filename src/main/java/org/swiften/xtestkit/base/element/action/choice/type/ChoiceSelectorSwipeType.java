package org.swiften.xtestkit.base.element.action.choice.type;

import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.bool.BooleanUtil;
import org.swiften.xtestkit.base.Engine;
import org.swiften.xtestkit.base.element.action.input.type.ChoiceInputType;
import org.swiften.xtestkit.base.element.action.swipe.type.SwipeRepeatComparisonType;
import org.swiften.xtestkit.base.element.action.swipe.type.SwipeType;
import org.swiften.xtestkit.base.element.locator.general.param.ByXPath;
import org.swiften.xtestkit.base.type.PlatformType;
import org.swiften.xtestkit.mobile.android.AndroidEngine;
import org.swiften.xtestkit.util.type.EngineContainerType;

/**
 * Created by haipham on 5/23/17.
 */

/**
 * This interface provides convenient methods for selecting an item from
 * a {@link java.util.List} of choices. Usually choice selection views have
 * some similarities, such as being vertical list views and containing text
 * views that display the choice values. The methods defined here help with
 * the navigation and identification of said choices.
 */
public interface ChoiceSelectorSwipeType extends SwipeRepeatComparisonType, EngineContainerType {
    /**
     * Get the associated {@link ChoiceInputType} instance.
     * @return {@link ChoiceInputType} instance.
     */
    @NotNull ChoiceInputType choiceInput();

    /**
     * Get the selected choice that we are looking for.
     * @return {@link String} value.
     */
    @NotNull String selectedChoice();

    /**
     * Get the {@link ByXPath} query to locate the target choice item.
     * @return {@link ByXPath} instance.
     */
    @NotNull
    default ByXPath targetChoiceItemQuery() {
        PlatformType platform = engine().platform();
        ChoiceInputType input = choiceInput();
        String stringValue = selectedChoice();

        return ByXPath.builder()
            .withXPath(input.targetChoiceItemXPath(platform, stringValue))
            .withRetries(1)
            .build();
    }

    /**
     * Get the selected choice's numeric representation to compare against
     * other choice items.
     * @return {@link Double} value.
     * @see #choiceInput()
     * @see #selectedChoice()
     * @see ChoiceInputType#numericValue(String)
     */
    default double selectedChoiceNumericValue() {
        ChoiceInputType input = choiceInput();
        String selected = selectedChoice();
        return input.numericValue(selected);
    }

    /**
     * Override this method to provide default implementation.
     * @param element {@link WebElement} instance.
     * @return {@link Flowable} instance.
     * @see SwipeRepeatComparisonType#rx_initialDifference(WebElement)
     * @see #engine()
     * @see #choiceInput()
     * @see #selectedChoiceNumericValue()
     * @see Engine#getText(WebElement)
     * @see ChoiceInputType#numericValue(String)
     */
    @NotNull
    @Override
    default Flowable<Integer> rx_initialDifference(@NotNull WebElement element) {
        final Engine<?> ENGINE = engine();
        final ChoiceInputType INPUT = choiceInput();
        final double NUMERIC_VALUE = selectedChoiceNumericValue();

        return Flowable.just(element)
            .map(ENGINE::getText)
            .map(INPUT::numericValue)
            .map(a -> a - NUMERIC_VALUE)
            .map(Double::intValue);
    }

    /**
     * Override this method to provide default implementation.
     * @param element {@link WebElement} instance.
     * @return {@link Flowable} instance.
     * @see SwipeRepeatComparisonType#rx_compareFirst(WebElement)
     * @see #engine()
     * @see #choiceInput()
     * @see #selectedChoiceNumericValue()
     * @see Engine#getText(WebElement)
     * @see ChoiceInputType#numericValue(String)
     */
    @NotNull
    @Override
    default Flowable<?> rx_compareFirst(@NotNull WebElement element) {
        final Engine<?> ENGINE = engine();
        final ChoiceInputType INPUT = choiceInput();
        final double NUMERIC_VALUE = selectedChoiceNumericValue();

        return Flowable.just(element)
            .map(ENGINE::getText)
            .map(INPUT::numericValue)
            .filter(a -> a > NUMERIC_VALUE);
    }

    /**
     * Override this method to provide default implementation.
     * @param element {@link WebElement} instance.
     * @return {@link Flowable} instance.
     * @see SwipeRepeatComparisonType#rx_compareLast(WebElement)
     * @see #engine()
     * @see #choiceInput()
     * @see #selectedChoiceNumericValue()
     * @see Engine#getText(WebElement)
     * @see ChoiceInputType#numericValue(String)
     */
    @NotNull
    @Override
    default Flowable<?> rx_compareLast(@NotNull WebElement element) {
        final Engine<?> ENGINE = engine();
        final ChoiceInputType INPUT = choiceInput();
        final double NUMERIC_VALUE = selectedChoiceNumericValue();

        return Flowable.just(element)
            .map(ENGINE::getText)
            .map(INPUT::numericValue)
            .filter(a -> a < NUMERIC_VALUE);
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Flowable} instance.
     * @see SwipeRepeatComparisonType#rx_scrollViewChildItems()
     * @see #engine()
     * @see #choiceInput()
     * @see Engine#platform()
     * @see ChoiceInputType#choicePickerScrollViewItemXPath(PlatformType)
     */
    @NotNull
    @Override
    default Flowable<WebElement> rx_scrollViewChildItems() {
        Engine<?> engine = engine();
        ChoiceInputType input = choiceInput();
        PlatformType platform = engine.platform();
        return engine.rx_withXPath(input.choicePickerScrollViewItemXPath(platform));
    }

    /**
     * Get the target choice item we are interested in.
     * @return {@link Flowable} instance.
     * @see #engine()
     * @see #targetChoiceItemQuery()
     * @see Engine#rx_byXPath(ByXPath...)
     */
    @NotNull
    default Flowable<WebElement> rx_targetChoiceItem() {
        Engine<?> engine = engine();
        ByXPath query = targetChoiceItemQuery();
        return engine.rx_byXPath(query).firstElement().toFlowable();
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Flowable} instance.
     * @see SwipeRepeatComparisonType#rx_elementSwipeRatio()
     * @see #engine()
     * @see #choiceInput()
     * @see Engine#platform()
     * @see ChoiceInputType#swipeRatio(PlatformType)
     */
    @NotNull
    @Override
    default Flowable<Double> rx_elementSwipeRatio() {
        PlatformType platform = engine().platform();
        double ratio = choiceInput().swipeRatio(platform);
        return Flowable.just(ratio);
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Flowable} instance.
     * @see SwipeRepeatComparisonType#rx_shouldKeepSwiping()
     * @see #engine()
     * @see #selectedChoice()
     * @see #targetChoiceItemQuery()
     * @see Engine#getText(WebElement)
     * @see Engine#rx_click(WebElement)
     * @see BooleanUtil#toTrue(Object)
     */
    @NotNull
    @Override
    default Flowable<Boolean> rx_shouldKeepSwiping() {
        final Engine<?> ENGINE = engine();
        final String STR_VALUE = selectedChoice();
        ByXPath query = targetChoiceItemQuery();

        return rx_targetChoiceItem()
            .filter(a -> ENGINE.getText(a).equals(STR_VALUE))
            .flatMap(ENGINE::rx_click)
            .map(BooleanUtil::toTrue);
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Flowable} instance.
     * @see SwipeRepeatComparisonType#rx_scrollableViewToSwipe()
     * @see #engine()
     * @see #choiceInput()
     * @see Engine#platform()
     * @see ChoiceInputType#choicePickerScrollViewXPath(PlatformType)
     */
    @NotNull
    @Override
    default Flowable<WebElement> rx_scrollableViewToSwipe() {
        Engine<?> engine = engine();
        ChoiceInputType input = choiceInput();
        PlatformType platform = engine.platform();

        return engine
            .rx_withXPath(input.choicePickerScrollViewXPath(platform))
            .firstElement()
            .toFlowable();
    }

    /**
     * Override this method to provide default implementation.
     * @param param {@link SwipeType} instance.
     * @return {@link Flowable} instance.
     * @see SwipeRepeatComparisonType#rx_swipeOnce(SwipeType)
     * @see #engine()
     * @see Engine#rx_swipeOnce(SwipeType)
     */
    @NotNull
    @Override
    default Flowable<Boolean> rx_swipeOnce(@NotNull SwipeType param) {
        return engine().rx_swipeOnce(param);
    }
}
