package org.swiften.xtestkit.base.element.search;

import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.object.HPObjects;
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
     * Clear the currently displayed query in the search bar. We click and tap
     * at the same time to ensure the action goes through - since both actions
     * won't induce any additional side effect aside from the search bar being
     * cleared.
     * @return {@link Flowable} instance.
     * @see #middleCoordinate(WebElement)
     * @see #clickFn()
     * @see #tapMiddleFn()
     * @see #rxe_textClear()
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default Flowable<Boolean> rxa_clearSearchBar() {
        return Flowable.concatArray(
            rxe_textClear().compose(clickFn()),
            rxe_textClear().compose(tapMiddleFn())
        ).all(HPObjects::nonNull).toFlowable();
    }
}
