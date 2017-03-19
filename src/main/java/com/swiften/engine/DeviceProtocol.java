package com.swiften.engine;

import com.swiften.engine.param.NavigateBack;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 3/19/17.
 */
public interface DeviceProtocol {
    /**
     * Navigate backwards for certain number of times.
     * @param param A {@link NavigateBack} object.
     * @return A {@link Flowable} instance.
     */
    Flowable<Boolean> rxNavigateBack(@NotNull NavigateBack param);
}
