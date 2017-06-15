package org.swiften.xtestkit.base.element.search;

import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.swiften.xtestkit.base.element.click.ClickActionType;

/**
 * Created by haipham on 1/6/17.
 */

/**
 * This interface provides methods to handle search.
 * @param <D> Generics parameter.
 */
public interface SearchActionType<D extends WebDriver> extends ClickActionType<D> {
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
    default Flowable<WebElement> rxa_clearSearchBar() {
        final SearchActionType<?> THIS = this;
        return rxe_textClear().flatMap(THIS::rxa_click);
    }
}
