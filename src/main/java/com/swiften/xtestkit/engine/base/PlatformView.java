package com.swiften.xtestkit.engine.base;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by haipham on 3/20/17.
 */

/**
 * Subclass this class to provide platform-specific {@link View}.
 */
public abstract class PlatformView {
    @NotNull
    public List<View> anyView() {
        return Collections.singletonList(View.ANY_VIEW);
    }

    @NotNull
    public List<View> allViews() {
        return Arrays.asList(getViews());
    }

    /**
     * Get all {@link View} that {@link View#hasText()}.
     * @return A {@link List} of {@link View}.
     */
    @NotNull
    public List<? extends View> hasText() {
        return allViews().stream()
            .filter(View::hasText)
            .collect(Collectors.toList());
    }

    /**
     * Get all {@link View} that {@link View#isClickable()}.
     * @return A {@link List} of {@link View}.
     */
    public List<? extends View> isClickable() {
        return allViews().stream()
            .filter(View::isClickable)
            .collect(Collectors.toList());
    }

    /**
     * Get all {@link View} that {@link View#isEditable()}.
     * @return A {@link List} of {@link View}.
     */
    @NotNull
    public List<? extends View> isEditable() {
        return allViews().stream()
            .filter(View::isEditable)
            .collect(Collectors.toList());
    }

    /**
     * We return an array here because we will probably be using enums to
     * store the {@link View} types.
     * @return An array of {@link View}
     */
    @NotNull
    protected abstract View[] getViews();
}
