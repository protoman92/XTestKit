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
import org.swiften.xtestkitcomponents.xpath.Attribute;
import org.swiften.xtestkitcomponents.xpath.Attributes;
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
     * @see Attributes#hasText(String)
     * @see Attributes#of(PlatformType)
     * @see Attributes#ofClass(String)
     * @see BaseViewType#className()
     * @see Platform#ANDROID
     * @see XPath.Builder#addAttribute(Attribute)
     */
    @NotNull
    @Override
    default XPath androidTargetItemXP(@NotNull String selected) {
        Attributes attrs = Attributes.of(Platform.ANDROID);
        String clsName = AndroidView.ViewType.EDIT_TEXT.className();

        return XPath.builder()
            .addAttribute(attrs.ofClass(clsName))
            .addAttribute(attrs.hasText(selected))
            .build();
    }

    /**
     * Get the parent {@link XPath} to use with
     * {@link #androidChoicePickerXP()} in order to narrow down the
     * {@link java.util.List} of located {@link org.openqa.selenium.WebElement}.
     * Prepend this in front of {@link #androidChoicePickerXP()}.
     * @return {@link XPath} instance.
     * @see Attribute#empty()
     * @see Platform#ANDROID
     * @see XPath.Builder#addAttribute(Attribute)
     * @see #androidChoicePickerXP()
     */
    @NotNull
    default XPath androidChoicePickerParentXP() {
        return XPath.builder().addAttribute(Attribute.empty()).build();
    }

    /**
     * Override this to provide default implementation.
     * @return {@link XPath} instance.
     * @see AndroidChoiceInputType#androidChoicePickerXP()
     * @see AndroidView.ViewType#NUMBER_PICKER
     * @see Attribute#forClass(String)
     * @see BaseViewType#className()
     * @see XPath.Builder#addAttribute(Attribute)
     * @see XPath.Builder#withXPath(XPath)
     * @see #androidChoicePickerParentXP()
     */
    @NotNull
    @Override
    default XPath androidChoicePickerXP() {
        String cls = AndroidView.ViewType.NUMBER_PICKER.className();

        return XPath.builder()
            .withXPath(androidChoicePickerParentXP())
            .addAttribute(Attribute.forClass(cls))
            .build();
    }

    /**
     * Override this to provide default implementation.
     * @return {@link XPath} instance.
     * @see AndroidView.ViewType#NUMBER_PICKER
     * @see Attribute#empty()
     * @see Attribute#withClass(String)
     * @see Attribute#withIndex(Integer)
     * @see BaseViewType#className()
     * @see XPath.Builder#addAttribute(Attribute)
     * @see #androidChoicePickerParentXP()
     * @see #androidScrollablePickerIndex()
     */
    @NotNull
    @Override
    default XPath androidChoicePickerItemXP() {
        return XPath.builder()
            .withXPath(androidChoicePickerParentXP())
            .addAttribute(Attribute.empty()
                .withClass(AndroidView.ViewType.NUMBER_PICKER.className())
                .withIndex(androidScrollablePickerIndex() + 1))
            .addAttribute(Attribute.empty())
            .build();
    }
}
