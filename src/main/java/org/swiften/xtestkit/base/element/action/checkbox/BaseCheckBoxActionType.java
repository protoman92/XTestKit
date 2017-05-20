package org.swiften.xtestkit.base.element.action.checkbox;

/**
 * Created by haipham on 5/15/17.
 */

import io.reactivex.Completable;
import io.reactivex.Flowable;
import org.intellij.lang.annotations.Flow;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.log.LogUtil;
import org.swiften.xtestkit.base.element.action.click.BaseClickActionType;
import org.swiften.xtestkit.base.element.property.type.base.BooleanType;

/**
 * This interface provides methods to work with checkboxes.
 */
public interface BaseCheckBoxActionType extends BaseClickActionType {
    /**
     * Check if a check box is checked.
     * @param element A {@link WebElement} instance.
     * @return A {@link Boolean} value.
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
     * @param element A {@link WebElement} instance.
     * @param checked A {@link Boolean} value.
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
     * @param ELEMENT A {@link WebElement} instance.
     * @param CHECKED A {@link Boolean} value.
     * @return A {@link Flowable} instance.
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
