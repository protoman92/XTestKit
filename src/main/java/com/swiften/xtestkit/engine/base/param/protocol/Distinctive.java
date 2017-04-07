package com.swiften.xtestkit.engine.base.param.protocol;

import io.reactivex.functions.Function;
import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 4/8/17.
 */

/**
 * This should be used with {@link io.reactivex.Flowable#distinct(Function)}.
 * @see io.reactivex.Flowable#distinct(Function)
 */
public interface Distinctive {
    @NotNull Object getComparisonObject();
}
