package com.swiften.xtestkit.test;

import com.swiften.xtestkit.engine.base.param.protocol.RetryProtocol;
import com.swiften.xtestkit.kit.TestKit;
import org.jetbrains.annotations.NotNull;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

/**
 * Created by haipham on 3/26/17.
 */

/**
 * We need to use this {@link org.junit.runner.Runner} to ensure
 * {@link com.swiften.xtestkit.kit.TestKit} based tests are repeated as long
 * as there are more than 1
 * {@link com.swiften.xtestkit.engine.base.PlatformEngine} registered. Since
 * runners are set via {@link org.junit.runner.RunWith}, we must subclass this
 * to provide different retry count values. Each test suite should have one
 * static {@link com.swiften.xtestkit.kit.TestKit} so that
 * {@link org.junit.BeforeClass} and {@link org.junit.AfterClass} methods can
 * access it and call {@link TestKit#beforeClass()},
 * {@link TestKit#afterClass()} and {@link TestKit#incrementCurrent()}.
 * We should call {@link TestKit#incrementCurrent()} in
 * {@link org.junit.BeforeClass} to make sure the test cases being run has
 * access to the current {@link com.swiften.xtestkit.engine.base.PlatformEngine}
 * using {@link TestKit#currentEngine()}.
 */
public class RepeatTestRunner extends BlockJUnit4ClassRunner implements RetryProtocol {
    public RepeatTestRunner(@NotNull Class<?> cls) throws InitializationError {
        super(cls);
    }

    /**
     * Override this method to provide custom retry count.
     * @return An {@link Integer} value.
     */
    @Override
    public int minRetries() {
        return 1;
    }

    @Override
    public void run(@NotNull RunNotifier notifier) {
        /* For each iteration of run(), @BeforeClass and @AfterClass methods
         * will be run once. We can switch PlatformEngine then to provide
         * different settings for test cases */
        for (int i = 0, tries = minRetries(); i < tries; i++) {
            super.run(notifier);
        }
    }
}
