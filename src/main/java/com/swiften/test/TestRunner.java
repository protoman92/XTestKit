package com.swiften.test;

import org.jetbrains.annotations.NotNull;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

/**
 * Created by haipham on 3/26/17.
 */
public class TestRunner extends BlockJUnit4ClassRunner {
    public TestRunner(@NotNull Class<?> cls) throws InitializationError {
        super(cls);
    }

    @Override
    public void run(@NotNull RunNotifier notifier) {
        notifier.addListener(new TestRunListener());
        super.run(notifier);
    }
}
