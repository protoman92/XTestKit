package org.swiften.xtestkit.base.element.action.search;

import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.xtestkit.base.element.action.click.BaseClickActionType;

/**
 * Created by haipham on 1/6/17.
 */

/**
 * This interface provides methods to handle search.
 */
public interface BaseSearchActionType extends BaseClickActionType {
    /**
     * Get the clear text button, a press upon which deletes the displayed
     * search query.
     * @return {@link Flowable} instance.
     */
    @NotNull
    Flowable<WebElement> rxe_textClear();

    /**
     * Clear the currently displayed query in the search bar.
     * @return {@link Flowable} instance.
     * @see #rxa_click(WebElement)
     * @see #rxe_textClear()
     */
    @NotNull
    default Flowable<WebElement> rxa_clearText() {
        final BaseClickActionType THIS = this;
        return rxe_textClear().flatMap(THIS::rxa_click);
    }
}
