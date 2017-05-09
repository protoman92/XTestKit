package org.swiften.xtestkit.mobile.android.element.action.date.type;

/**
 * Created by haipham on 5/9/17.
 */

import org.jetbrains.annotations.NotNull;

/**
 * This interface provides errors for {@link AndroidDateActionType}
 */
public interface AndroidDateActionErrorType {
    @NotNull String DATES_NOT_MATCHED = "Dates do not match";
    @NotNull String UNKNOWN_DATE_VIEW_TYPE = "Unknown date view type";
}
