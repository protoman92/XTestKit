package org.swiften.xtestkit.base.capability;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.swiften.javautilities.object.HPObjects;
import org.swiften.javautilities.string.HPStrings;
import org.swiften.xtestkit.base.TestMode;
import org.swiften.xtestkitcomponents.platform.PlatformType;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by haipham on 5/7/17.
 */
public abstract class EngineCapability implements EngineCapabilityType {
    @Nullable TestMode testMode;

    protected EngineCapability() {}

    //region EngineCapabilityType
    /**
     * Override this method to provide specific {@link PlatformType}.
     * @return {@link PlatformType} instance.
     */
    @NotNull
    @Override
    public abstract PlatformType platform();

    @NotNull
    @Override
    @SuppressWarnings("ConstantConditions")
    public TestMode testMode() {
        HPObjects.requireNotNull(testMode, NOT_AVAILABLE);
        return testMode;
    }

    @NotNull
    @Override
    public Collection<String> requiredCapabilities() {
        return Collections.emptyList();
    }

    @Override
    public boolean isComplete(@NotNull Map<String,Object> information) {
        Collection<String> required = requiredCapabilities();

        return required.stream()
            .map(information::get)
            .map(String::valueOf)
            .allMatch(HPStrings::isNotNullOrEmpty);
    }

    @NotNull
    @Override
    public Map<String,Object> distill(@NotNull Map<String,Object> capabilities) {
        return new HashMap<>(capabilities);
    }
    //endregion

    //region Builder
    /**
     * Builder class for {@link EngineCapability}.
     * @param <C> Generics that extends {@link EngineCapability}.
     */
    public static class Builder<C extends EngineCapability> implements
        EngineCapabilityType.Builder
    {
        @NotNull private final C CAPABILITY;

        protected Builder(@NotNull C capability) {
            CAPABILITY = capability;
        }

        @NotNull
        public Builder<C> withTestMode(@NotNull TestMode testMode) {
            CAPABILITY.testMode = testMode;
            return this;
        }

        @NotNull
        public C build() {
            return CAPABILITY;
        }
    }
    //endregion
}
