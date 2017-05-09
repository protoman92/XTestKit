package org.swiften.xtestkit.base.type;

import io.reactivex.functions.Function;
import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 4/8/17.
 */

/**
 * This should be used with {@link io.reactivex.Flowable#distinct(Function)}.
 * @see io.reactivex.Flowable#distinct(Function)
 */
@FunctionalInterface
public interface DistinctiveType {
    @NotNull Object getComparisonObject();
}
