package com.swiften.xtestkit.engine;

import com.swiften.xtestkit.engine.base.AppiumCommand;
import com.swiften.xtestkit.util.LogUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Objects;

/**
 * Created by haipham on 4/6/17.
 */
public class AppiumCommandTest {
    @Nullable private AppiumCommand.Builder builder;

    @BeforeMethod
    public void beforeMethod() {
        builder = AppiumCommand.builder();
    }

    @Test
    public void mock_commandConstruction_shouldSucceed() {
        // Setup

        // When
        AppiumCommand command = builder()
            .withBase("Base")
            .withPort(4327)
            .build();

        // Then
        LogUtil.println(command.command());
    }

    @NotNull
    private AppiumCommand.Builder builder() {
        if (Objects.nonNull(builder)) {
            return builder;
        }

        throw new RuntimeException();
    }
}
