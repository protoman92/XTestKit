package org.swiften.xtestkit.ios.element.action.choice;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.bool.BooleanUtil;
import org.swiften.xtestkit.base.element.action.choice.BaseChoiceSelectorType;
import org.swiften.xtestkit.base.element.action.choice.ChoiceHelperType;
import org.swiften.xtestkit.base.element.action.choice.ChoiceType;
import org.swiften.xtestkit.model.ChoiceInputType;
import org.swiften.xtestkit.base.element.locator.general.xpath.XPath;
import org.swiften.xtestkit.base.type.PlatformType;
import org.swiften.xtestkit.mobile.Platform;

/**
 * Created by haipham on 25/5/17.
 */
public interface IOSChoiceSelectorType extends BaseChoiceSelectorType<IOSDriver<IOSElement>> {
    /**
     * Override this method to provide default implementation.
     * @param param {@link ChoiceType} instance.
     * @return {@link Flowable} instance.
     * @see BaseChoiceSelectorType#rx_selectGeneralChoice(ChoiceType)
     * @see Platform#IOS
     * @see ChoiceType#selectedChoice()
     * @see ChoiceType#input()
     * @see ChoiceInputType#scrollablePickerIndex(PlatformType)
     * @see ChoiceInputType#choicePickerXPath(PlatformType)
     * @see ChoiceHelperType#rx_type(WebElement, String...)
     * @see BooleanUtil#toTrue(Object)
     */
    @NotNull
    default Flowable<Boolean> rx_selectGeneralChoice(@NotNull ChoiceType param) {
        final ChoiceHelperType<?> ENGINE = this;
        final String SELECTED = param.selectedChoice();
        Platform platform = Platform.IOS;
        ChoiceInputType input = param.input();
        int index = input.scrollablePickerIndex(platform);
        XPath xPath = input.choicePickerXPath(platform);

        return ENGINE
            .rx_withXPath(xPath)
            .elementAt(index)
            .toFlowable()
            .flatMap(a -> ENGINE.rx_type(a, SELECTED))
            .map(BooleanUtil::toTrue);
    }
}
