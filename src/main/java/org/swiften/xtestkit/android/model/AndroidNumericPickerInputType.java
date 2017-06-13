package org.swiften.xtestkit.android.model;

/**
 * Created by haipham on 23/5/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.android.AndroidView;
import org.swiften.xtestkit.base.element.locator.param.ByXPath;
import org.swiften.xtestkit.base.model.InputHelperType;
import org.swiften.xtestkit.mobile.Platform;
import org.swiften.xtestkitcomponents.platform.PlatformProviderType;
import org.swiften.xtestkitcomponents.platform.PlatformType;
import org.swiften.xtestkitcomponents.view.BaseViewType;
import org.swiften.xtestkitcomponents.xpath.*;

/**
 * This interface provides input methods for
 * {@link Platform#ANDROID} number pickers.
 * Use this with {@link AndroidView.ViewType#NUMBER_PICKER}.
 */
public interface AndroidNumericPickerInputType extends AndroidChoiceInputType {
    /**
     * Override this to provide default implementation.
     * @param helper {@link InputHelperType} instance.
     * @param selected {@link String} value of the selected choice.
     * @return {@link ByXPath} instance.
     * @see Attributes#hasText(String)
     * @see Attributes#of(PlatformProviderType)
     * @see Attributes#ofClass(String)
     * @see BaseViewType#className()
     * @see CompoundAttribute.Builder#addAttribute(AttributeType)
     * @see AndroidView.ViewType#EDIT_TEXT
     * @see Platform#ANDROID
     * @see XPath.Builder#addAttribute(CompoundAttribute)
     */
    @NotNull
    @Override
    default XPath androidTargetItemXP(@NotNull InputHelperType helper,
                                      @NotNull String selected) {
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
     * {@link #androidChoicePickerXP(InputHelperType)} in order to narrow down
     * the {@link java.util.List} of located
     * {@link org.openqa.selenium.WebElement}.
     * Prepend this in front of {@link #androidChoicePickerXP(InputHelperType)}.
     * @param helper {@link InputHelperType} instance.
     * @return {@link XPath} instance.
     * @see CompoundAttribute#empty()
     * @see XPath.Builder#addAttribute(AttributeType)
     * @see Platform#ANDROID
     * @see #androidChoicePickerXP(InputHelperType)
     */
    @NotNull
    default XPath androidChoicePickerParentXP(@NotNull InputHelperType helper) {
        return XPath.builder().addAttribute(CompoundAttribute.empty()).build();
    }

    /**
     * Override this to provide default implementation.
     * @return {@link XPath} instance.
     * @see BaseViewType#className()
     * @see CompoundAttribute#empty()
     * @see CompoundAttribute#withClass(String)
     * @see CompoundAttribute#withIndex(Integer)
     * @see XPath.Builder#addAttribute(AttributeType)
     * @see AndroidView.ViewType#NUMBER_PICKER
     * @see #androidChoicePickerParentXP(InputHelperType)
     * @see #androidScrollablePickerIndex(InputHelperType)
     */
    @NotNull
    @Override
    default XPath androidChoicePickerXP(@NotNull InputHelperType helper) {
        return XPath.builder()
            .withXPath(androidChoicePickerParentXP(helper))
            .addAttribute(CompoundAttribute.empty()
                .withClass(AndroidView.ViewType.NUMBER_PICKER.className())
                .withIndex(androidScrollablePickerIndex(helper) + 1))
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
     * @see XPath.Builder#addAttribute(AttributeType)
     * @see XPath.Builder#addXPath(XPath)
     * @see #androidChoicePickerXP(InputHelperType)
     */
    @NotNull
    @Override
    default XPath androidChoicePickerItemXP(@NotNull InputHelperType helper) {
        return XPath.builder()
            .withXPath(androidChoicePickerXP(helper))
            .addAttribute(CompoundAttribute.empty())
            .build();
    }
}
