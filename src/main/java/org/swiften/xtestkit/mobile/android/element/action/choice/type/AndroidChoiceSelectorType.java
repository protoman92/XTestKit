package org.swiften.xtestkit.mobile.android.element.action.choice.type;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.number.NumberUtil;
import org.swiften.xtestkit.base.element.action.choice.type.ChoiceHelperType;
import org.swiften.xtestkit.base.element.action.choice.type.ChoiceSelectorSwipeType;
import org.swiften.xtestkit.base.element.action.choice.type.BaseChoiceSelectorType;
import org.swiften.xtestkit.base.element.action.choice.type.ChoiceType;
import org.swiften.xtestkit.base.element.action.input.type.ChoiceInputType;
import org.swiften.xtestkit.base.element.action.swipe.type.SwipeRepeatType;
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
     * @see ChoiceHelperType#rx_withXPath(XPath...)
     * @see ChoiceSelectorSwipeType#rx_scrollViewChildCount()
     * @see ChoiceSelectorSwipeType#rx_execute()
     */
    @NotNull
    default Flowable<Boolean> rx_selectGeneralChoice(@NotNull ChoiceType param) {
        final AndroidChoiceSelectorType THIS = this;
        final Platform PLATFORM = Platform.ANDROID;
        final ChoiceInputType INPUT = param.input();
        final String SELECTED = param.selectedChoice();
        final int INDEX = INPUT.scrollablePickerIndex(PLATFORM);

        SwipeRepeatType selector = new ChoiceSelectorSwipeType() {
            /**
             * Since there are might be multiple pickers with identical id,
             * we need to get the element that corresponds to a specified
             * index
             */
            @NotNull
            @Override
            public Flowable<WebElement> rx_scrollableViewToSwipe() {
                XPath xPath = INPUT.choicePickerScrollViewXPath(PLATFORM);
                return THIS.rx_withXPath(xPath).elementAt(INDEX).toFlowable();
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
            public ChoiceInputType choiceInput() {
                return INPUT;
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
