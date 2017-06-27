package org.swiften.xtestkit.ios.element.choice;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.bool.BooleanUtil;
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
     * @see ChoiceSelectorType#rxa_selectGeneralChoice(ChoiceType)
     * @see Platform#IOS
     * @see ChoiceType#selectedChoice()
     * @see ChoiceType#input()
     * @see ChoiceInputType#scrollablePickerIndex(InputHelperType)
     * @see ChoiceInputType#choicePickerXP(InputHelperType)
     * @see ChoiceHelperType#rxa_sendValue(WebElement, String)
     * @see BooleanUtil#toTrue(Object)
     */
    @NotNull
    default Flowable<Boolean> rxa_selectGeneralChoice(@NotNull ChoiceType param) {
        final ChoiceHelperType<?> ENGINE = this;
        final String SELECTED = param.selectedChoice();
        Platform platform = Platform.IOS;
        ChoiceInputType input = param.input();
        int index = input.scrollablePickerIndex(this);
        XPath xpath = input.choicePickerXP(this);

        return ENGINE
            .rxe_withXPath(xpath)
            .elementAt(index)
            .toFlowable()
            .flatMap(a -> ENGINE.rxa_sendValue(a, SELECTED))
            .map(BooleanUtil::toTrue);
    }
}
