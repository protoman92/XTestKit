package org.swiften.xtestkit.ios.element.choice;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.bool.HPBooleans;
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
 * This interface provides choice selector capabilities for {@link Platform#IOS}.
 */
public interface IOSChoiceSelectorType extends
    ChoiceSelectorType<IOSDriver<IOSElement>>,
    InputHelperType
{
    /**
     * Override this method to provide default implementation.
     * @param param {@link ChoiceType} instance.
     * @return {@link Flowable} instance.
     * @see #rxe_withXPath(XPath...)
     */
    @NotNull
    default Flowable<Boolean> rxa_selectGeneralChoice(@NotNull ChoiceType param) {
        ChoiceInputType input = param.input();

        return rxe_withXPath(input.choicePickerXP(this))
            .elementAt(input.scrollablePickerIndex(this))
            .toFlowable()
            .compose(sendValueFn(param.selectedChoice()))
            .map(HPBooleans::toTrue);
    }
}
