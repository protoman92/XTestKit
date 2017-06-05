package org.swiften.xtestkit.android.element.choice;

import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.bool.BooleanUtil;
import org.swiften.xtestkit.base.element.choice.ChoiceHelperType;
import org.swiften.xtestkit.android.model.AndroidChoiceInputType;
import org.swiften.xtestkit.base.model.ChoiceInputType;
import org.swiften.xtestkit.base.element.swipe.MultiSwipeComparisonType;
import org.swiften.xtestkit.base.element.swipe.SwipeType;
import org.swiften.xtestkit.base.element.locator.param.ByXPath;
import org.swiften.xtestkitcomponents.platform.PlatformType;
import org.swiften.xtestkit.mobile.Platform;

/**
 * Created by haipham on 5/23/17.
 */

/**
 * This interface provides convenient methods for selecting an item from
 * a {@link java.util.List} of choices. Usually choice selection views have
 * some similarities, such as being vertical list views and containing text
 * views that display the choice values. The methods defined here help with
 * the navigation and identification of said choices.
 * For {@link Platform#IOS}, we can simply call
 * {@link WebElement#sendKeys(CharSequence...)} to select a choice from a
 * {@link org.swiften.xtestkit.ios.IOSView.ViewType#UI_PICKER_WHEEL}.
 * It is only on {@link Platform#ANDROID} that
 * we have difficulty.
 */
public interface AndroidChoiceMultiSwipeType extends MultiSwipeComparisonType {
    /**
     * Get the associated {@link ChoiceHelperType} instance.
     * @return {@link ChoiceHelperType} instance.
     */
    @NotNull ChoiceHelperType helper();

    /**
     * Get the associated {@link ChoiceInputType} instance.
     * @return {@link ChoiceInputType} instance.
     */
    @NotNull AndroidChoiceInputType choiceInput();

    /**
     * Get the selected choice that we are looking for.
     * @return {@link String} value.
     */
    @NotNull String selectedChoice();

    /**
     * Get the {@link ByXPath} query to locate the target choice item.
     * @return {@link ByXPath} instance.
     * @see AndroidChoiceInputType#androidTargetItemXP(String)
     * @see ChoiceHelperType#platform()
     * @see #choiceInput()
     * @see #helper()
     * @see #selectedChoice()
     */
    @NotNull
    default ByXPath targetChoiceItemQuery() {
        AndroidChoiceInputType input = choiceInput();
        String stringValue = selectedChoice();

        return ByXPath.builder()
            .withXPath(input.androidTargetItemXP(stringValue))
            .withRetries(1)
            .build();
    }

    /**
     * Get the selected choice's numeric representation to compare against
     * other choice items.
     * @return {@link Double} value.
     * @see ChoiceInputType#numericValue(String)
     * @see #choiceInput()
     * @see #selectedChoice()
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
     * @see MultiSwipeComparisonType#rxe_initialDifference(WebElement)
     * @see ChoiceHelperType#getText(WebElement)
     * @see ChoiceInputType#numericValue(String)
     * @see ChoiceInputType#numericValueStep()
     * @see #choiceInput()
     * @see #helper()
     * @see #selectedChoiceNumericValue()
     */
    @NotNull
    @Override
    default Flowable<Integer> rxe_initialDifference(@NotNull WebElement element) {
        final ChoiceHelperType<?> ENGINE = helper();
        final ChoiceInputType INPUT = choiceInput();
        final double NUMERIC_VALUE = selectedChoiceNumericValue();
        final double STEP = INPUT.numericValueStep();

        return Flowable.just(element)
            .map(ENGINE::getText)
            .map(INPUT::numericValue)
            .map(a -> (a - NUMERIC_VALUE) / STEP)
            .map(Double::intValue);
    }

    /**
     * Override this method to provide default implementation.
     * @param element {@link WebElement} instance.
     * @return {@link Flowable} instance.
     * @see MultiSwipeComparisonType#rxa_compareFirst(WebElement)
     * @see ChoiceHelperType#getText(WebElement)
     * @see ChoiceInputType#numericValue(String)
     * @see #choiceInput()
     * @see #helper()
     * @see #selectedChoiceNumericValue()
     */
    @NotNull
    @Override
    default Flowable<?> rxa_compareFirst(@NotNull WebElement element) {
        final ChoiceHelperType<?> ENGINE = helper();
        final ChoiceInputType INPUT = choiceInput();
        final double NUMERIC_VALUE = selectedChoiceNumericValue();

        return Flowable.just(element)
            .map(ENGINE::getText)
            .map(INPUT::numericValue)
            .filter(a -> a >= NUMERIC_VALUE);
    }

    /**
     * Override this method to provide default implementation.
     * @param element {@link WebElement} instance.
     * @return {@link Flowable} instance.
     * @see MultiSwipeComparisonType#rxa_compareLast(WebElement)
     * @see ChoiceHelperType#getText(WebElement)
     * @see ChoiceInputType#numericValue(String)
     * @see #choiceInput()
     * @see #helper()
     * @see #selectedChoiceNumericValue()
     */
    @NotNull
    @Override
    default Flowable<?> rxa_compareLast(@NotNull WebElement element) {
        final ChoiceHelperType<?> ENGINE = helper();
        final ChoiceInputType INPUT = choiceInput();
        final double NUMERIC_VALUE = selectedChoiceNumericValue();

        return Flowable.just(element)
            .map(ENGINE::getText)
            .map(INPUT::numericValue)
            .filter(a -> a <= NUMERIC_VALUE);
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Flowable} instance.
     * @see MultiSwipeComparisonType#rxe_scrollViewChildItems()
     * @see ChoiceHelperType#platform()
     * @see ChoiceInputType#choicePickerItemXP(PlatformType)
     * @see #choiceInput()
     * @see #helper()
     */
    @NotNull
    @Override
    default Flowable<WebElement> rxe_scrollViewChildItems() {
        ChoiceHelperType<?> engine = helper();
        ChoiceInputType input = choiceInput();
        PlatformType platform = engine.platform();
        return engine.rxe_withXPath(input.choicePickerItemXP(platform));
    }

    /**
     * Get the target choice item we are interested in.
     * @return {@link Flowable} instance.
     * @see ChoiceHelperType#rxe_byXPath(ByXPath...)
     * @see #helper()
     * @see #targetChoiceItemQuery()
     */
    @NotNull
    default Flowable<WebElement> rxe_targetChoiceItem() {
        ChoiceHelperType<?> engine = helper();
        ByXPath query = targetChoiceItemQuery();
        return engine.rxe_byXPath(query).firstElement().toFlowable();
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Flowable} instance.
     * @see MultiSwipeComparisonType#rxe_elementSwipeRatio()
     * @see ChoiceHelperType#platform()
     * @see ChoiceInputType#swipeRatio(PlatformType)
     * @see #choiceInput()
     * @see #helper()
     */
    @NotNull
    @Override
    default Flowable<Double> rxe_elementSwipeRatio() {
        PlatformType platform = helper().platform();
        double ratio = choiceInput().swipeRatio(platform);
        return Flowable.just(ratio);
    }

    /**
     * This method will be called once the item has been located.
     * @param element {@link WebElement} instance that is displaying the
     *                selected choice.
     * @return {@link Flowable} instance.
     * @see ChoiceHelperType#rxa_click(WebElement)
     * @see #helper()
     */
    @NotNull
    default Flowable<?> rxa_targetItemLocated(@NotNull WebElement element) {
        return helper().rxa_click(element);
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Flowable} instance.
     * @see MultiSwipeComparisonType#rxv_shouldKeepSwiping()
     * @see BooleanUtil#toTrue(Object)
     * @see ChoiceHelperType#getText(WebElement)
     * @see #helper()
     * @see #selectedChoice()
     * @see #targetChoiceItemQuery()
     * @see #rxa_targetItemLocated(WebElement)
     */
    @NotNull
    @Override
    default Flowable<Boolean> rxv_shouldKeepSwiping() {
        final AndroidChoiceMultiSwipeType THIS = this;
        final ChoiceHelperType<?> ENGINE = helper();
        final String STR_VALUE = selectedChoice();

        return rxe_targetChoiceItem()
            .filter(a -> ENGINE.getText(a).equals(STR_VALUE))
            .flatMap(THIS::rxa_targetItemLocated)
            .map(BooleanUtil::toTrue);
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Flowable} instance.
     * @see MultiSwipeComparisonType#rxe_scrollableViewToSwipe()
     * @see ChoiceHelperType#platform()
     * @see ChoiceInputType#choicePickerXP(PlatformType)
     * @see #helper()
     * @see #choiceInput()
     */
    @NotNull
    @Override
    default Flowable<WebElement> rxe_scrollableViewToSwipe() {
        ChoiceHelperType<?> engine = helper();
        ChoiceInputType input = choiceInput();
        PlatformType platform = engine.platform();

        return engine
            .rxe_withXPath(input.choicePickerXP(platform))
            .firstElement()
            .toFlowable();
    }

    /**
     * Override this method to provide default implementation.
     * @param param {@link SwipeType} instance.
     * @return {@link Flowable} instance.
     * @see MultiSwipeComparisonType#rxa_swipeOnce(SwipeType)
     * @see ChoiceHelperType#rxa_swipeOnce(SwipeType)
     * @see #helper()
     */
    @NotNull
    @Override
    default Flowable<Boolean> rxa_swipeOnce(@NotNull SwipeType param) {
        return helper().rxa_swipeOnce(param);
    }
}
