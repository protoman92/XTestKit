package com.swiften.xtestkit.engine;

import com.swiften.engine.PlatformEngine;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Created by haipham on 3/20/17.
 */
public final class PlatformEngineTest {
    @Test
    public void test() {
    }

    static final class MockPlatformEngine extends PlatformEngine<WebDriver> {
        @NotNull
        @Override
        protected WebDriver createDriverInstance() {
            return null;
        }
    }
}
