package org.swiften.xtestkit.base;

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.type.BaseViewType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by haipham on 3/20/17.
 */

/**
 * Subclass this class to provide platform-specific {@link BaseViewType}.
 */
public abstract class PlatformView {
    @NotNull
    public List<BaseViewType> anyView() {
        return Collections.singletonList(BaseViewType.ANY_VIEW);
    }

    @NotNull
    public List<BaseViewType> allViews() {
        return Arrays.asList(getViews());
    }

    /**
     * Get all {@link BaseViewType} that {@link BaseViewType#hasText()}.
     * @return A {@link List} of {@link BaseViewType}.
     */
    @NotNull
    public List<? extends BaseViewType> hasText() {
        return allViews().stream()
            .filter(BaseViewType::hasText)
            .collect(Collectors.toList());
    }

    /**
     * Get all {@link BaseViewType} that {@link BaseViewType#isClickable()}.
     * @return A {@link List} of {@link BaseViewType}.
     */
    public List<? extends BaseViewType> isClickable() {
        return allViews().stream()
            .filter(BaseViewType::isClickable)
            .collect(Collectors.toList());
    }

    /**
     * Get all {@link BaseViewType} that {@link BaseViewType#isEditable()}.
     * @return A {@link List} of {@link BaseViewType}.
     */
    @NotNull
    public List<? extends BaseViewType> isEditable() {
        return allViews().stream()
            .filter(BaseViewType::isEditable)
            .collect(Collectors.toList());
    }

    /**
     * We return an array here because we will probably be using enums to
     * store the {@link BaseViewType} types.
     * @return An array of {@link BaseViewType}
     */
    @NotNull
    protected abstract BaseViewType[] getViews();
}
