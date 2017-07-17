package org.swiften.xtestkit.android.element.choice;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.bool.HPBooleans;
import org.swiften.javautilities.util.HPLog;
import org.swiften.xtestkit.android.model.AndroidChoiceInputType;
import org.swiften.xtestkit.base.element.choice.ChoiceHelperType;
import org.swiften.xtestkit.base.element.locator.ByXPath;
import org.swiften.xtestkit.base.element.swipe.MultiSwipeComparisonType;
import org.swiften.xtestkit.base.element.swipe.SwipeParamType;
import org.swiften.xtestkit.base.model.ChoiceInputType;
import org.swiften.xtestkit.base.model.InputHelperType;
import org.swiften.xtestkit.ios.IOSView;
import org.swiften.xtestkit.mobile.Platform;
import org.swiften.xtestkitcomponents.xpath.XPath;

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
 * {@link IOSView.Type#UI_PICKER_WHEEL}.
 * It is only on {@link Platform#ANDROID} that
 * we have difficulty.
 */
public interface AndroidChoiceMultiSwipeType extends MultiSwipeComparisonType {
    /**
     * Get the associated {@link ChoiceHelperType} instance.
     * @return {@link ChoiceHelperType} instance.
     */
    @NotNull ChoiceHelperType choiceHelper();

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
     * @see #choiceInput()
     * @see #choiceHelper()
     * @see #selectedChoice()
     */
    @NotNull
    default ByXPath targetChoiceItemQuery() {
        AndroidChoiceInputType input = choiceInput();
        ChoiceHelperType<?> helper = choiceHelper();
        String stringValue = selectedChoice();

        return ByXPath.builder()
            .withXPath(input.androidTargetItemXP(helper, stringValue))
            .withRetries(1)
            .build();
    }

    /**
     * Get the selected choice's numeric representation to compare against
     * other choice items.
     * @return {@link Double} value.
     * @see #choiceHelper()
     * @see #choiceInput()
     * @see #selectedChoice()
     */
    default double selectedChoiceNumericValue() {
        ChoiceHelperType<?> helper = choiceHelper();
        ChoiceInputType input = choiceInput();
        String selected = selectedChoice();
        return input.numericValue(helper, selected);
    }

    /**
     * Override this method to provide default implementation.
     * @param element {@link WebElement} instance.
     * @return {@link Flowable} instance.
     * @see MultiSwipeComparisonType#rxe_initialDifference(WebElement)
     * @see #choiceInput()
     * @see #choiceHelper()
     * @see #selectedChoiceNumericValue()
     */
    @NotNull
    @Override
    default Flowable<Integer> rxe_initialDifference(@NotNull WebElement element) {
        final ChoiceHelperType<?> HELPER = choiceHelper();
        final ChoiceInputType INPUT = choiceInput();
        final double NUMERIC_VALUE = selectedChoiceNumericValue();
        final double STEP = INPUT.numericValueStep(HELPER);

        return Flowable.just(element)
            .map(HELPER::getText)
            .map(a -> INPUT.numericValue(HELPER, a))
            .map(a -> (a - NUMERIC_VALUE) / STEP)
            .map(Double::intValue);
    }

    /**
     * Override this method to provide default implementation.
     * @param element {@link WebElement} instance.
     * @return {@link Flowable} instance.
     * @see MultiSwipeComparisonType#rxa_compareFirst(WebElement)
     * @see #choiceInput()
     * @see #choiceHelper()
     * @see #selectedChoiceNumericValue()
     */
    @NotNull
    @Override
    default Flowable<?> rxa_compareFirst(@NotNull WebElement element) {
        final ChoiceHelperType<?> HELPER = choiceHelper();
        final ChoiceInputType INPUT = choiceInput();
        final double NUMERIC = selectedChoiceNumericValue();

        return Flowable.just(element)
            .map(HELPER::getText)
            .map(a -> INPUT.numericValue(HELPER, a))
            .doOnNext(a -> HPLog.printft("Comparing %s and %s", a, NUMERIC))
            .filter(a -> a >= NUMERIC);
    }

    /**
     * Override this method to provide default implementation.
     * @param element {@link WebElement} instance.
     * @return {@link Flowable} instance.
     * @see MultiSwipeComparisonType#rxa_compareLast(WebElement)
     * @see #choiceInput()
     * @see #choiceHelper()
     * @see #selectedChoiceNumericValue()
     */
    @NotNull
    @Override
    default Flowable<?> rxa_compareLast(@NotNull WebElement element) {
        final ChoiceHelperType<?> HELPER = choiceHelper();
        final ChoiceInputType INPUT = choiceInput();
        final double NUMERIC = selectedChoiceNumericValue();

        return Flowable.just(element)
            .map(HELPER::getText)
            .map(a -> INPUT.numericValue(HELPER, a))
            .doOnNext(a -> HPLog.printft("Comparing %s and %s", a, NUMERIC))
            .filter(a -> a <= NUMERIC);
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Flowable} instance.
     * @see MultiSwipeComparisonType#rxe_scrollViewChildItems()
     * @see ChoiceHelperType#platform()
     * @see ChoiceInputType#choicePickerItemXP(InputHelperType)
     * @see #choiceInput()
     * @see #choiceHelper()
     */
    @NotNull
    @Override
    default Flowable<WebElement> rxe_scrollViewChildItems() {
        ChoiceHelperType<?> helper = choiceHelper();
        ChoiceInputType input = choiceInput();
        XPath xpath = input.choicePickerItemXP(helper);
        return helper.rxe_withXPath(xpath);
    }

    /**
     * Get the target choice item we are interested in.
     * @return {@link Flowable} instance.
     * @see ChoiceHelperType#rxe_byXPath(ByXPath...)
     * @see #choiceHelper()
     * @see #targetChoiceItemQuery()
     */
    @NotNull
    default Flowable<WebElement> rxe_targetChoiceItem() {
        ChoiceHelperType<?> engine = choiceHelper();
        ByXPath query = targetChoiceItemQuery();
        return engine.rxe_byXPath(query).firstElement().toFlowable();
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Flowable} instance.
     * @see MultiSwipeComparisonType#rxe_elementSwipeRatio()
     * @see ChoiceHelperType#platform()
     * @see ChoiceInputType#swipeRatio(InputHelperType)
     * @see #choiceInput()
     * @see #choiceHelper()
     */
    @NotNull
    @Override
    default Flowable<Double> rxe_elementSwipeRatio() {
        ChoiceHelperType<?> helper = choiceHelper();
        double ratio = choiceInput().swipeRatio(helper);
        return Flowable.just(ratio);
    }

    /**
     * This method will be called once the item has been located.
     * @return {@link Flowable} instance.
     * @see #choiceHelper()
     */
    @NotNull
    default FlowableTransformer<WebElement, ?> targetItemLocatedFn() {
        ChoiceHelperType<?> HELPER = choiceHelper();
        return upstream -> upstream.compose(HELPER.clickFn());
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Flowable} instance.
     * @see #choiceHelper()
     * @see #selectedChoice()
     * @see #targetChoiceItemQuery()
     * @see #targetItemLocatedFn()
     */
    @NotNull
    @Override
    default Flowable<Boolean> rxv_shouldKeepSwiping() {
        final AndroidChoiceMultiSwipeType THIS = this;
        final ChoiceHelperType<?> ENGINE = choiceHelper();
        final String STR_VALUE = selectedChoice();

        return rxe_targetChoiceItem()
            .filter(a -> ENGINE.getText(a).equals(STR_VALUE))
            .compose(targetItemLocatedFn())
            .map(HPBooleans::isFalse)
            .defaultIfEmpty(true)
            .onErrorReturnItem(true);
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Flowable} instance.
     * @see MultiSwipeComparisonType#rxe_scrollableViewToSwipe()
     * @see #choiceHelper()
     * @see #choiceInput()
     */
    @NotNull
    @Override
    default Flowable<WebElement> rxe_scrollableViewToSwipe() {
        ChoiceHelperType<?> helper = choiceHelper();
        ChoiceInputType input = choiceInput();
        XPath xpath = input.choicePickerXP(helper);
        return helper.rxe_withXPath(xpath).firstElement().toFlowable();
    }

    /**
     * Override this method to provide default implementation.
     * @param param {@link SwipeParamType} instance.
     * @see MultiSwipeComparisonType#swipeOnce(SwipeParamType)
     * @see #choiceHelper()
     */
    @Override
    default void swipeOnce(@NotNull SwipeParamType param) {
        choiceHelper().swipeOnce(param);
    }
}
