package org.swiften.xtestkit.engine.base;

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.engine.base.type.ViewType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by haipham on 3/20/17.
 */

/**
 * Subclass this class to provide platform-specific {@link ViewType}.
 */
public abstract class PlatformView {
    @NotNull
    public List<ViewType> anyView() {
        return Collections.singletonList(ViewType.ANY_VIEW);
    }

    @NotNull
    public List<ViewType> allViews() {
        return Arrays.asList(getViews());
    }

    /**
     * Get all {@link ViewType} that {@link ViewType#hasText()}.
     * @return A {@link List} of {@link ViewType}.
     */
    @NotNull
    public List<? extends ViewType> hasText() {
        return allViews().stream()
            .filter(ViewType::hasText)
            .collect(Collectors.toList());
    }

    /**
     * Get all {@link ViewType} that {@link ViewType#isClickable()}.
     * @return A {@link List} of {@link ViewType}.
     */
    public List<? extends ViewType> isClickable() {
        return allViews().stream()
            .filter(ViewType::isClickable)
            .collect(Collectors.toList());
    }

    /**
     * Get all {@link ViewType} that {@link ViewType#isEditable()}.
     * @return A {@link List} of {@link ViewType}.
     */
    @NotNull
    public List<? extends ViewType> isEditable() {
        return allViews().stream()
            .filter(ViewType::isEditable)
            .collect(Collectors.toList());
    }

    /**
     * We return an array here because we will probably be using enums to
     * store the {@link ViewType} types.
     * @return An array of {@link ViewType}
     */
    @NotNull
    protected abstract ViewType[] getViews();
}
