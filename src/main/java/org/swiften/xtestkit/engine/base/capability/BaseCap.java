package org.swiften.xtestkit.engine.base.capability;

import org.apache.commons.collections.map.HashedMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.swiften.javautilities.object.ObjectUtil;
import org.swiften.javautilities.string.StringUtil;
import org.swiften.xtestkit.engine.base.Platform;
import org.swiften.xtestkit.engine.base.TestMode;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by haipham on 5/7/17.
 */
public abstract class BaseCap implements
    TestCapabilityType,
    TestCapabilityErrorType
{
    @Nullable Platform platform;
    @Nullable TestMode testMode;

    protected BaseCap() {}

    //region TestCapabilityType.
    @NotNull
    @Override
    public Platform platform() {
        if (ObjectUtil.nonNull(platform)) {
            return platform;
        } else {
            throw new RuntimeException(PLATFORM_UNAVAILABLE);
        }
    }

    @NotNull
    @Override
    public TestMode testMode() {
        if (ObjectUtil.nonNull(testMode)) {
            return testMode;
        } else {
            throw new RuntimeException(TEST_MODE_UNAVAILABLE);
        }
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
            .allMatch(StringUtil::isNotNullOrEmpty);
    }

    @NotNull
    @Override
    public Map<String,Object> distill(@NotNull Map<String,Object> capabilities) {
        return new HashMap<>(capabilities);
    }
    //endregion

    //region Builder
    /**
     * Builder class for {@link BaseCap}.
     * @param <C> Generics that extends {@link BaseCap}.
     */
    public static class Builder<C extends BaseCap> implements
        TestCapabilityType.Builder
    {
        @NotNull private final C CAPABILITY;

        protected Builder(@NotNull C capability) {
            CAPABILITY = capability;
        }

        @NotNull
        public Builder<C> withPlatform(@NotNull Platform platform) {
            CAPABILITY.platform = platform;
            return this;
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
