package org.swiften.xtestkit.android.model;

/**
 * Created by haipham on 23/5/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.android.AndroidView;
import org.swiften.xtestkit.base.element.locator.param.ByXPath;
import org.swiften.xtestkitcomponents.view.BaseViewType;
import org.swiften.xtestkit.mobile.Platform;
import org.swiften.xtestkitcomponents.platform.PlatformType;
import org.swiften.xtestkitcomponents.xpath.Attribute;
import org.swiften.xtestkitcomponents.xpath.Attributes;
import org.swiften.xtestkitcomponents.xpath.CompoundAttribute;
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
     * @see CompoundAttribute.Builder#addAttribute(Attribute)
     * @see Platform#ANDROID
     * @see XPath.Builder#addAttribute(CompoundAttribute)
     */
    @NotNull
    @Override
    default XPath androidTargetItemXP(@NotNull String selected) {
        Attributes attrs = Attributes.of(Platform.ANDROID);
        String clsName = AndroidView.ViewType.EDIT_TEXT.className();

        CompoundAttribute attribute = CompoundAttribute.builder()
            .addAttribute(attrs.ofClass(clsName))
            .addAttribute(attrs.hasText(selected))
            .build();

        return XPath.builder().addAttribute(attribute).build();
    }

    /**
     * Get the parent {@link XPath} to use with
     * {@link #androidChoicePickerXP()} in order to narrow down the
     * {@link java.util.List} of located {@link org.openqa.selenium.WebElement}.
     * Prepend this in front of {@link #androidChoicePickerXP()}.
     * @return {@link XPath} instance.
     * @see CompoundAttribute#empty()
     * @see Platform#ANDROID
     * @see XPath.Builder#addAttribute(Attribute)
     * @see #androidChoicePickerXP()
     */
    @NotNull
    default XPath androidChoicePickerParentXP() {
        return XPath.builder().addAttribute(CompoundAttribute.empty()).build();
    }

    /**
     * Override this to provide default implementation.
     * @return {@link XPath} instance.
     * @see BaseViewType#className()
     * @see CompoundAttribute#empty()
     * @see CompoundAttribute#withClass(String)
     * @see CompoundAttribute#withIndex(Integer)
     * @see XPath.Builder#addAttribute(Attribute)
     * @see AndroidView.ViewType#NUMBER_PICKER
     * @see #androidChoicePickerParentXP()
     * @see #androidScrollablePickerIndex()
     */
    @NotNull
    @Override
    default XPath androidChoicePickerXP() {
        return XPath.builder()
            .withXPath(androidChoicePickerParentXP())
            .addAttribute(CompoundAttribute.empty()
                .withClass(AndroidView.ViewType.NUMBER_PICKER.className())
                .withIndex(androidScrollablePickerIndex() + 1))
            .build();
    }

    /**
     * Override this to provide default implementation.
     * @return {@link XPath} instance.
     * @see AndroidView.ViewType#NUMBER_PICKER
     * @see CompoundAttribute#empty()
     * @see CompoundAttribute#withClass(String)
     * @see CompoundAttribute#withIndex(Integer)
     * @see BaseViewType#className()
     * @see XPath.Builder#addAttribute(Attribute)
     * @see XPath.Builder#addXPath(XPath)
     * @see #androidChoicePickerXP()
     */
    @NotNull
    @Override
    default XPath androidChoicePickerItemXP() {
        return XPath.builder()
            .withXPath(androidChoicePickerXP())
            .addAttribute(CompoundAttribute.empty())
            .build();
    }
}
