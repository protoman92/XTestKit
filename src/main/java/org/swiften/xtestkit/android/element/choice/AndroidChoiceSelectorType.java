package org.swiften.xtestkit.android.element.choice;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.number.NumberUtil;
import org.swiften.xtestkit.android.model.AndroidChoiceInputType;
import org.swiften.xtestkit.base.element.choice.ChoiceHelperType;
import org.swiften.xtestkit.base.element.choice.ChoiceSelectorType;
import org.swiften.xtestkit.base.element.choice.ChoiceType;
import org.swiften.xtestkit.base.model.ChoiceInputType;
import org.swiften.xtestkit.base.model.InputHelperType;
import org.swiften.xtestkit.mobile.Platform;
import org.swiften.xtestkitcomponents.xpath.XPath;

/**
 * Created by haipham on 25/5/17.
 */

/**
 * This interface provides choice selection capabilities for
 * {@link Platform#ANDROID}.
 */
public interface AndroidChoiceSelectorType extends
    ChoiceSelectorType<AndroidDriver<AndroidElement>>,
    InputHelperType
{
    /**
     * Override this method to provide default implementation.
     * @param param {@link ChoiceType} instance.
     * @return {@link Flowable} instance.
     * @see ChoiceSelectorType#rxa_selectGeneralChoice(ChoiceType)
     * @see AndroidChoiceMultiSwipeType#rxa_performAction()
     * @see AndroidChoiceMultiSwipeType#rxe_scrollViewChildCount()
     * @see ChoiceType#input()
     * @see ChoiceType#selectedChoice()
     * @see ChoiceInputType#scrollablePickerIndex(InputHelperType)
     * @see ChoiceHelperType#rxe_withXPath(XPath...)
     * @see NumberUtil#inverse(Number)
     * @see Platform#ANDROID
     */
    @NotNull
    @Override
    default Flowable<Boolean> rxa_selectGeneralChoice(@NotNull ChoiceType param) {
        final AndroidChoiceSelectorType THIS = this;
        final ChoiceInputType INPUT = param.input();
        final String SELECTED = param.selectedChoice();
        final double RATIO = INPUT.swipeRatio(this);

        return new AndroidChoiceMultiSwipeType() {
            /**
             * Since there are might be multiple pickers with identical id,
             * we need to get the element that corresponds to a specified
             * index
             */
            @NotNull
            @Override
            public Flowable<WebElement> rxe_scrollableViewToSwipe() {
                XPath xpath = INPUT.choicePickerXP(THIS);
                return THIS.rxe_withXPath(xpath).firstElement().toFlowable();
            }

            @NotNull
            @Override
            public Flowable<Double> rxe_elementSwipeRatio() {
                /* Customize the swipe ratio so that the picker is scrolled
                 * by one item at a time, to ensure accuracy */
                return rxe_scrollViewChildCount()
                    .map(NumberUtil::inverse)
                    .map(a -> a * RATIO);
            }

            @NotNull
            @Override
            public AndroidChoiceInputType choiceInput() {
                return (AndroidChoiceInputType)INPUT;
            }

            @NotNull
            @Override
            public String selectedChoice() {
                return SELECTED;
            }

            @NotNull
            @Override
            public ChoiceHelperType<?> choiceHelper() {
                return THIS;
            }
        }.rxa_performAction();
    }
}
