package com.swiften.sample.test;

import com.swiften.test.RepeatTestRunner;
import com.swiften.sample.Config;
import org.jetbrains.annotations.NotNull;
import org.junit.runners.model.InitializationError;

/**
 * Created by haipham on 3/26/17.
 */

/**
 * We subclass {@link RepeatTestRunner} and provide a custom retry count,
 * based on {@link Config#runCount()}, which, in turn, is based on the number
 * of {@link com.swiften.engine.base.PlatformEngine} registered.
 */
public final class TestApplicationRunner extends RepeatTestRunner {
    public TestApplicationRunner(@NotNull Class<?> cls) throws InitializationError {
        super(cls);
    }

    @Override
    public int retries() {
        return Config.runCount();
    }
}
