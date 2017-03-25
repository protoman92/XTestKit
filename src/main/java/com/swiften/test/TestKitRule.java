package com.swiften.test;

import com.swiften.kit.TestKit;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by haipham on 3/25/17.
 */

/**
 * Provide a custom implementation for {@link TestKit} so that
 * {@link TestKit#rxBeforeClass()} and {@link TestKit#rxAfterClass()} are
 * automatically taken care of.
 */
public class TestKitRule extends RepeatRule implements RepeatRule.Delegate {
    @NotNull
    public static Builder newBuilder() {
        return new Builder();
    }

    @Nullable TestKit testKit;

    //region RepeatRule.Delegate
    @Override
    public void onIterationStarted(int iteration) {
        TestKit testKit = testKit();
        testKit.incrementCurrent();
        testKit.beforeClass();
    }

    @Override
    public void onIterationFinished(int iteration) {
        testKit().afterClass();
    }
    //endregion

    @NotNull
    private TestKit testKit() {
        if (testKit != null) {
            return testKit;
        }

        throw new RuntimeException("TestKit cannot be null");
    }

    public static final class Builder extends RepeatRule.Builder {
        Builder() {
            super(new TestKitRule());
        }

        /**
         * Set the {@link #TEST_RULE#testKit} value.
         * @param testKit A {@link TestKit} instance.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withTestKit(@NotNull TestKit testKit) {
            ((TestKitRule)TEST_RULE).testKit = testKit;
            withRetries(testKit.engines().size());
            return this;
        }

        @NotNull
        public RepeatRule build() {
            withDelegate((TestKitRule)TEST_RULE);
            return super.build();
        }
    }
}
