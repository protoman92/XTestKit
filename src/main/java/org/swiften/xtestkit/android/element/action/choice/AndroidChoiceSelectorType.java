package org.swiften.xtestkit.android.element.action.choice;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.number.NumberUtil;
import org.swiften.xtestkit.base.element.action.choice.ChoiceHelperType;
import org.swiften.xtestkit.base.element.action.choice.BaseChoiceSelectorType;
import org.swiften.xtestkit.base.element.action.choice.ChoiceType;
import org.swiften.xtestkit.model.AndroidChoiceInputType;
import org.swiften.xtestkit.model.ChoiceInputType;
import org.swiften.xtestkit.base.element.action.swipe.SwipeRepeatType;
import org.swiften.xtestkit.base.element.locator.general.xpath.XPath;
import org.swiften.xtestkit.base.type.PlatformType;
import org.swiften.xtestkit.mobile.Platform;

/**
 * Created by haipham on 25/5/17.
 */

/**
 * This interface provides choice selection capabilities for
 * {@link org.swiften.xtestkit.mobile.Platform#ANDROID}.
 */
public interface AndroidChoiceSelectorType extends BaseChoiceSelectorType<AndroidDriver<AndroidElement>> {
    /**
     * Override this method to provide default implementation.
     * @param param {@link ChoiceType} instance.
     * @return {@link Flowable} instance.
     * @see BaseChoiceSelectorType#rx_selectGeneralChoice(ChoiceType)
     * @see Platform#ANDROID
     * @see ChoiceType#input()
     * @see ChoiceType#selectedChoice()
     * @see ChoiceInputType#scrollablePickerIndex(PlatformType)
     * @see ChoiceHelperType#rxe_withXPath(XPath...)
     * @see AndroidChoiceSwipeSelectorType#rx_scrollViewChildCount()
     * @see AndroidChoiceSwipeSelectorType#rx_execute()
     */
    @NotNull
    default Flowable<Boolean> rx_selectGeneralChoice(@NotNull ChoiceType param) {
        final AndroidChoiceSelectorType THIS = this;
        final Platform PLATFORM = Platform.ANDROID;
        final ChoiceInputType INPUT = param.input();
        final String SELECTED = param.selectedChoice();
        final int INDEX = INPUT.scrollablePickerIndex(PLATFORM);

        SwipeRepeatType selector = new AndroidChoiceSwipeSelectorType() {
            /**
             * Since there are might be multiple pickers with identical id,
             * we need to get the element that corresponds to a specified
             * index
             */
            @NotNull
            @Override
            public Flowable<WebElement> rx_scrollableViewToSwipe() {
                XPath xPath = INPUT.choicePickerXPath(PLATFORM);
                return THIS.rxe_withXPath(xPath).elementAt(INDEX).toFlowable();
            }

            @NotNull
            @Override
            public Flowable<Double> rx_elementSwipeRatio() {
                /* Customize the swipe ratio so that the picker is scrolled
                 * by one item at a time, to ensure accuracy */
                return rx_scrollViewChildCount().map(NumberUtil::inverse);
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
            public ChoiceHelperType<?> engine() {
                return THIS;
            }
        };

        return selector.rx_execute();
    }
}
