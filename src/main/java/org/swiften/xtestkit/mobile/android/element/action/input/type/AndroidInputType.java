package org.swiften.xtestkit.mobile.android.element.action.input.type;

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.element.action.input.type.InputType;
import org.swiften.xtestkit.base.element.locator.general.xpath.XPath;
import org.swiften.xtestkit.base.element.locator.general.xpath.type.NewXPathBuilderType;
import org.swiften.xtestkit.mobile.Platform;

/**
 * Created by haipham on 18/5/17.
 */

/**
 * This interface provides methods to help interact with
 * {@link org.swiften.xtestkit.mobile.Platform#ANDROID} input-related
 * {@link org.openqa.selenium.WebElement}.
 */
public interface AndroidInputType extends InputType, NewXPathBuilderType {
    /**
     * Override this method to provide a default {@link XPath.Builder} instance
     * with {@link Platform#ANDROID}.
     * @return A {@link XPath.Builder} instance.
     * @see Platform#ANDROID
     */
    @NotNull
    @Override
    default XPath.Builder newXPathBuilder() {
        return XPath.builder(Platform.ANDROID);
    }

    /**
     * Get the {@link XPath} instance for
     * {@link org.swiften.xtestkit.mobile.Platform#ANDROID} locator.
     * @return A {@link XPath} value.
     */
    @NotNull
    XPath androidViewXPath();
}
