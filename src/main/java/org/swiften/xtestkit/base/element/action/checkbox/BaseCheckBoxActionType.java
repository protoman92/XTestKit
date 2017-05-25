package org.swiften.xtestkit.base.element.action.checkbox;

/**
 * Created by haipham on 5/15/17.
 */

import io.reactivex.Completable;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.xtestkit.base.element.action.click.BaseClickActionType;

/**
 * This interface provides methods to work with checkboxes.
 */
public interface BaseCheckBoxActionType extends BaseClickActionType {
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
     * @see BaseClickActionType#click(WebElement)
     */
    default void setCheckBoxState(@NotNull WebElement element, boolean checked) {
        if (isCheckBoxChecked(element) != checked) {
            click(element);
        }
    }

    /**
     * Toggle a checkbox to be checked/unchecked.
     * @param ELEMENT {@link WebElement} instance.
     * @param CHECKED {@link Boolean} value.
     * @return {@link Flowable} instance.
     * @see #setCheckBoxState(WebElement, boolean)
     */
    @NotNull
    default Flowable<WebElement> toggleCheckBox(
        @NotNull final WebElement ELEMENT, final boolean CHECKED)
    {
        final BaseCheckBoxActionType THIS = this;

        return Completable
            .fromAction(() -> THIS.setCheckBoxState(ELEMENT, CHECKED))
            .<WebElement>toFlowable()
            .defaultIfEmpty(ELEMENT);
    }
}
