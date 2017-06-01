package org.swiften.xtestkit.android.element.search;

import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.xtestkit.base.element.search.BaseSearchActionType;
import org.swiften.xtestkit.base.type.BaseErrorType;

/**
 * Created by haipham on 1/6/17.
 */

/**
 * This interface provides methods to handle search for
 * {@link org.swiften.xtestkit.mobile.Platform#ANDROID}.
 */
public interface AndroidSearchActionType extends BaseErrorType, BaseSearchActionType {
    /**
     * Override this method to provide default implementation.
     * @return {@link Flowable} instance.
     */
    @NotNull
    @Override
    default Flowable<WebElement> rxe_textClear() {
        throw new RuntimeException(NOT_AVAILABLE);
    }
}
