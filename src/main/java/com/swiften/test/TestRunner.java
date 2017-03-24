package com.swiften.test;

import org.jetbrains.annotations.NotNull;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

/**
 * Created by haipham on 3/25/17.
 */
public class TestRunner extends BlockJUnit4ClassRunner {
    public TestRunner(Class<?> cls) throws InitializationError {
        super(cls);
    }

    @Override
    public void run(@NotNull RunNotifier notifier){
        notifier.addListener(new CustomListener());
        super.run(notifier);
    }
}
