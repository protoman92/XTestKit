package org.swiften.xtestkit.base.element.search;

import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.object.ObjectUtil;
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
     * @see ObjectUtil#nonNull(Object)
     * @see #middleCoordinate(WebElement)
     * @see #rxa_click(WebElement)
     * @see #rxa_tap(Point)
     * @see #rxe_textClear()
     */
    @NotNull
    default Flowable<Boolean> rxa_clearSearchBar() {
        final SearchActionType<?> THIS = this;

        return Flowable.concatArray(
            rxe_textClear().flatMap(THIS::rxa_click),
            rxe_textClear().map(THIS::middleCoordinate).flatMap(THIS::rxa_tap)
        ).all(ObjectUtil::nonNull).toFlowable();
    }
}
