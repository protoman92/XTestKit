package org.swiften.xtestkit.base.element.checkbox;

/**
 * Created by haipham on 5/15/17.
 */

import io.reactivex.FlowableTransformer;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.bool.HPBooleans;
import org.swiften.javautilities.rx.HPReactives;
import org.swiften.xtestkit.base.element.click.ClickActionType;

/**
 * This interface provides methods to work with checkboxes.
 * @param <D> Generics parameter.
 */
public interface CheckBoxActionType<D extends WebDriver> extends ClickActionType<D> {
    /**
     * Check if a check box is checked.
     * @param element {@link WebElement} instance.
     * @return {@link Boolean} value.
     * @see WebElement#getAttribute(String)
     * @see Boolean#valueOf(String)
     */
    default boolean isCheckBoxChecked(@NotNull WebElement element) {
        try {
            String checked = element.getAttribute("checked");
            return Boolean.valueOf(checked);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Toggle a checkbox to be checked/unchecked.
     * @param element {@link WebElement} instance.
     * @param checked {@link Boolean} value.
     * @see #isCheckBoxChecked(WebElement)
     * @see ClickActionType#click(WebElement)
     */
    default void setCheckBoxState(@NotNull WebElement element, boolean checked) {
        if (isCheckBoxChecked(element) != checked) {
            click(element);
        }
    }

    /**
     * Toggle a checkbox to be checked/unchecked.
     * @param CHECKED {@link Boolean} value.
     * @return {@link FlowableTransformer} instance.
     * @see #setCheckBoxState(WebElement, boolean)
     */
    @NotNull
    default FlowableTransformer<WebElement, Boolean> toggleCheckBoxFn(final boolean CHECKED) {
        final CheckBoxActionType THIS = this;

        return upstream -> upstream
            .compose(HPReactives.completableFn(a -> THIS.setCheckBoxState(a, CHECKED)))
            .map(HPBooleans::toTrue)
            .defaultIfEmpty(true);
    }
}
