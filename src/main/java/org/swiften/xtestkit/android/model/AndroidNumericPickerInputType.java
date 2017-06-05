package org.swiften.xtestkit.android.model;

/**
 * Created by haipham on 23/5/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.android.AndroidView;
import org.swiften.xtestkit.base.element.locator.param.ByXPath;
import org.swiften.xtestkit.base.type.BaseViewType;
import org.swiften.xtestkit.mobile.Platform;
import org.swiften.xtestkitcomponents.platform.PlatformType;
import org.swiften.xtestkitcomponents.xpath.XPath;

/**
 * This interface provides input methods for
 * {@link Platform#ANDROID} number pickers.
 * Use this with {@link AndroidView.ViewType#NUMBER_PICKER}.
 */
public interface AndroidNumericPickerInputType extends AndroidChoiceInputType {
    /**
     * Override this to provide default implementation.
     * @param selected {@link String} value of the selected choice.
     * @return {@link ByXPath} instance.
     * @see AndroidView.ViewType#EDIT_TEXT
     * @see BaseViewType#className()
     * @see Platform#ANDROID
     * @see XPath.Builder#addAnyClass()
     * @see XPath.Builder#ofClass(String)
     * @see XPath.Builder#ofInstance(int)
     */
    @NotNull
    @Override
    default XPath androidTargetItemXP(@NotNull String selected) {
        return XPath.builder(Platform.ANDROID)
            .ofClass(AndroidView.ViewType.EDIT_TEXT.className())
            .hasText(selected)
            .addAnyClass()
            .build();
    }

    /**
     * Get the parent {@link XPath} to use with
     * {@link #androidChoicePickerXP()} in order to narrow down the
     * {@link java.util.List} of located {@link org.openqa.selenium.WebElement}.
     * Prepend this in front of {@link #androidChoicePickerXP()}.
     * @return {@link XPath} instance.
     * @see Platform#ANDROID
     * @see XPath.Builder#addAnyClass()
     * @see #androidChoicePickerXP()
     */
    @NotNull
    default XPath androidChoicePickerParentXP() {
        return XPath.builder(Platform.ANDROID).addAnyClass().build();
    }

    /**
     * Override this to provide default implementation.
     * @return {@link XPath} instance.
     * @see AndroidChoiceInputType#androidChoicePickerXP()
     * @see AndroidView.ViewType#NUMBER_PICKER
     * @see BaseViewType#className()
     * @see Platform#ANDROID
     * @see XPath.Builder#addChildXPath(XPath)
     * @see XPath.Builder#atIndex(int)
     * @see XPath.Builder#ofClass(String)
     * @see XPath.Builder#withXPath(XPath)
     * @see #androidChoicePickerParentXP()
     * @see #androidScrollablePickerIndex()
     */
    @NotNull
    @Override
    default XPath androidChoicePickerXP() {
        Platform platform = Platform.ANDROID;
        String cls = AndroidView.ViewType.NUMBER_PICKER.className();

        return XPath.builder(platform)
            .withXPath(androidChoicePickerParentXP())
            .addChildXPath(XPath.builder(platform).addClass(cls).build())
            .build();
    }

    /**
     * Override this to provide default implementation.
     * @return {@link XPath} instance.
     * @see AndroidView.ViewType#NUMBER_PICKER
     * @see BaseViewType#className()
     * @see Platform#ANDROID
     * @see XPath.Builder#addAnyClass()
     * @see XPath.Builder#addChildXPath(XPath)
     * @see XPath.Builder#containsID(String)
     * @see XPath.Builder#ofInstance(int)
     * @see #androidChoicePickerParentXP()
     * @see #androidScrollablePickerIndex()
     */
    @NotNull
    @Override
    default XPath androidChoicePickerItemXP() {
        PlatformType platform = Platform.ANDROID;

        XPath child = XPath.builder(platform)
            .addClass(AndroidView.ViewType.NUMBER_PICKER.className())

            /* Need to add 1 since XPath index is 1-based */
            .setIndex(androidScrollablePickerIndex() + 1)
            .addChildXPath(XPath.builder(platform).addAnyClass().build())
            .build();

        return XPath.builder(platform)
            .withXPath(androidChoicePickerParentXP())
            .addChildXPath(child)
            .build();
    }
}
