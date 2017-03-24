package com.swiften.test;

import com.swiften.kit.TestKit;
import com.swiften.util.Log;
import io.reactivex.Completable;
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
public class TestKitRepeat extends RepeatRule implements RepeatRule.Delegate {
    @NotNull
    public static Builder newBuilder() {
        return new Builder();
    }

    @Nullable TestKit testKit;

    //region RepeatRule.Delegate
    @Override
    public void onNewIteration(int iteration) {
        TestSubscriber<Boolean> subscriber = TestSubscriber.create();
        TestKit testKit = testKit();

        testKit.rxAfterClass()
            .flatMapCompletable(a -> Completable.fromAction(testKit::incrementCurrent))
            .toFlowable()
            .flatMap(a -> testKit.rxBeforeClass())
            .subscribe(subscriber);

        subscriber.awaitTerminalEvent();
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
            super(new TestKitRepeat());
        }

        /**
         * Set the {@link #TEST_RULE#testKit} value.
         * @param testKit A {@link TestKit} instance.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withTestKit(@NotNull TestKit testKit) {
            ((TestKitRepeat)TEST_RULE).testKit = testKit;
            return this;
        }

        @NotNull
        public RepeatRule build() {
            withDelegate((TestKitRepeat)TEST_RULE);
            return super.build();
        }
    }
}
