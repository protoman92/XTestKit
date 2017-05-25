package org.swiften.xtestkit.mobile.ios.element.action.choice.type;

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.element.locator.general.xpath.XPath;
import org.swiften.xtestkit.mobile.Platform;
import org.swiften.xtestkit.mobile.ios.IOSView;

/**
 * Created by haipham on 24/5/17.
 */
public interface IOSNumericPickerInputType extends IOSChoiceInputType {
    /**
     * Override this method to provide default implementation.
     * @return {@link XPath} instance.
     * @see Platform#IOS
     * @see IOSView.ViewType#UI_PICKERWHEEL
     */
    @NotNull
    @Override
    default XPath iOSScrollViewPickerXPath() {
        String cls = IOSView.ViewType.UI_PICKERWHEEL.className();
        return XPath.builder(Platform.IOS).setClass(cls).build();
    }
}
