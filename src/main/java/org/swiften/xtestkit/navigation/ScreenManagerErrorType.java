package org.swiften.xtestkit.navigation;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * Created by haipham on 5/20/17.
 */
public interface ScreenManagerErrorType {
    /**
     * Format a non-reachable error.
     * @param screens Varargs {@link ScreenType} instances.
     * @return {@link String} value.
     */
    @NotNull
    default String notReachable(@NotNull ScreenType...screens) {
        String body = Arrays.stream(screens)
            .map(ScreenType::toString)
            .reduce("", (a, b) -> String.join("-"));

        return String.format("Not reachable: %s", body);
    }
}
