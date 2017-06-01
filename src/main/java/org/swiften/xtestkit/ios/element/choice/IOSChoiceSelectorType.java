package org.swiften.xtestkit.ios.element.choice;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.bool.BooleanUtil;
import org.swiften.xtestkit.base.element.choice.BaseChoiceSelectorType;
import org.swiften.xtestkit.base.element.choice.ChoiceHelperType;
import org.swiften.xtestkit.base.element.choice.ChoiceType;
import org.swiften.xtestkit.model.ChoiceInputType;
import org.swiften.xtestkit.base.element.locator.xpath.XPath;
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
     * @see BaseChoiceSelectorType#rxa_selectGeneralChoice(ChoiceType)
     * @see Platform#IOS
     * @see ChoiceType#selectedChoice()
     * @see ChoiceType#input()
     * @see ChoiceInputType#scrollablePickerIndex(PlatformType)
     * @see ChoiceInputType#choicePickerXPath(PlatformType)
     * @see ChoiceHelperType#rxa_type(WebElement, String...)
     * @see BooleanUtil#toTrue(Object)
     */
    @NotNull
    default Flowable<Boolean> rxa_selectGeneralChoice(@NotNull ChoiceType param) {
        final ChoiceHelperType<?> ENGINE = this;
        final String SELECTED = param.selectedChoice();
        Platform platform = Platform.IOS;
        ChoiceInputType input = param.input();
        int index = input.scrollablePickerIndex(platform);
        XPath xPath = input.choicePickerXPath(platform);

        return ENGINE
            .rxe_withXPath(xPath)
            .elementAt(index)
            .toFlowable()
            .flatMap(a -> ENGINE.rxa_type(a, SELECTED))
            .map(BooleanUtil::toTrue);
    }
}
