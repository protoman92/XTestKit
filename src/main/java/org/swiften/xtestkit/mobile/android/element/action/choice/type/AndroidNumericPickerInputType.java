package org.swiften.xtestkit.mobile.android.element.action.choice.type;

/**
 * Created by haipham on 23/5/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.element.locator.general.param.ByXPath;
import org.swiften.xtestkit.base.element.locator.general.xpath.XPath;
import org.swiften.xtestkit.base.type.PlatformType;
import org.swiften.xtestkit.mobile.Platform;
import org.swiften.xtestkit.mobile.android.AndroidView;

/**
 * This interface provides input methods for
 * {@link org.swiften.xtestkit.mobile.Platform#ANDROID} number pickers.
 */
public interface AndroidNumericPickerInputType extends AndroidChoiceInputType {
    /**
     * Override this to provide default implementation.
     * @param selected {@link String} value of the selected choice.
     * @return {@link ByXPath} instance.
     * @see Platform#ANDROID
     * @see AndroidView.ViewType#EDIT_TEXT
     * @see XPath.Builder#ofClass(String)
     * @see XPath.Builder#ofInstance(int)
     */
    @NotNull
    @Override
    default XPath androidTargetChoiceItemXPath(@NotNull String selected) {
        String cls = AndroidView.ViewType.EDIT_TEXT.className();
        return XPath.builder(Platform.ANDROID).ofClass(cls).hasText(selected).build();
    }

    /**
     * Override this to provide default implementation.
     * @return {@link XPath} instance.
     * @see Platform#ANDROID
     * @see AndroidView.ViewType#NUMBER_PICKER
     * @see #androidScrollablePickerIndex()
     * @see XPath.Builder#atIndex(int)
     * @see XPath.Builder#ofClass(String)
     */
    @NotNull
    @Override
    default XPath androidScrollViewPickerXPath() {
        String cls = AndroidView.ViewType.NUMBER_PICKER.className();
        return XPath.builder(Platform.ANDROID).setClass(cls).build();
    }

    /**
     * Override this to provide default implementation.
     * @return {@link XPath} instance.
     * @see Platform#ANDROID
     * @see AndroidView.ViewType#NUMBER_PICKER
     * @see #androidScrollablePickerIndex()
     * @see XPath.Builder#containsID(String)
     * @see XPath.Builder#ofInstance(int)
     */
    @NotNull
    @Override
    default XPath androidScrollViewPickerItemXPath() {
        PlatformType platform = Platform.ANDROID;
        XPath cxp = XPath.builder(platform).build();

        return XPath.builder(platform)
            .setClass(AndroidView.ViewType.NUMBER_PICKER.className())
            .ofInstance(androidScrollablePickerIndex())
            .addChildXPath(cxp)
            .build();
    }
}
