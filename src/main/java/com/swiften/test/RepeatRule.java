package com.swiften.test;

import com.swiften.engine.base.param.protocol.RetryProtocol;
import com.swiften.util.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Created by haipham on 3/24/17.
 */

/**
 * This {@link TestRule} is used to repeat test cases. We can use this in
 * conjunction with {@link com.swiften.kit.TestKit}: for e.g., we can iterate
 * through {@link com.swiften.kit.TestKit#}
 */
public class RepeatRule implements TestRule, RetryProtocol {
    public static Builder newBuilder() {
        return new Builder();
    }

    @Nullable Delegate delegate;

    int retries;

    RepeatRule() {}

    @NotNull
    @SuppressWarnings("WeakerAccess")
    Delegate delegate() {
        if (delegate != null) {
            return delegate;
        }

        throw new RuntimeException("Delegate cannot be null");
    }

    @Override
    public int retries() {
        return retries;
    }

    @NotNull
    @Override
    public Statement apply(@NotNull final Statement BASE,
                           @NotNull Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                Delegate delegate = delegate();
                Throwable caughtException = null;

                for (int i = 0, retries = retries(); i < retries; i++) {
                    delegate.onIterationStarted(i);

                    try {
                        BASE.evaluate();
                    } catch (Throwable t) {
                        caughtException = t;
                    } finally {
                        delegate.onIterationFinished(i);
                    }
                }

                if (caughtException != null) {
                    throw caughtException;
                };
            }
        };
    }

    public static class Builder {
        @NotNull protected RepeatRule TEST_RULE;

        protected Builder(@NotNull RepeatRule rule) {
            TEST_RULE = rule;
        }

        protected Builder() {
            this(new RepeatRule());
        }

        /**
         * Set the {@link #TEST_RULE#delegate} value.
         * @param delegate A {@link Delegate} instance.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withDelegate(@NotNull Delegate delegate) {
            TEST_RULE.delegate = delegate;
            return this;
        }

        /**
         * Set the {@link #TEST_RULE#retries} value.
         * @param retries An {@link Integer} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withRetries(int retries) {
            TEST_RULE.retries = retries;
            return this;
        }

        @NotNull
        public RepeatRule build() {
            return TEST_RULE;
        }
    }

    public interface Delegate {
        /**
         * Call this method when a new loop is run.
         * @param iteration The iteration index.
         */
        void onIterationStarted(int iteration);

        /**
         * Call this method when an iteration is finished.
         * @param iteration The iteration index.
         */
        void onIterationFinished(int iteration);
    }
}
