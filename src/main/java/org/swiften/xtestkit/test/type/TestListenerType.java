package org.swiften.xtestkit.test.type;

import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.test.RepeatRunner;

/**
 * Created by haipham on 4/6/17.
 */
public interface TestListenerType {
    /**
     * Cal this method when test are first started.
     * @return {@link Flowable} instance.
     */
    @NotNull
    Flowable<Boolean> rx_onFreshStart();

    /**
     * Call this method when a batch of test is started.
     * @param indexes The indexes of batch items. An Array of {@link Integer}.
     * @return {@link Flowable} instance.
     */
    @NotNull
    Flowable<Boolean> rx_onBatchStarted(@NotNull int[] indexes);

    /**
     * Call this method when a batch of test is finished.
     * @param indexes The indexes for batch items. An Array of {@link Integer}.
     * @return {@link Flowable} instance.
     */
    @NotNull
    Flowable<Boolean> rx_onBatchFinished(@NotNull int[] indexes);

    /**
     * Call this method when all test have finished, typically at the
     * end of {@link RepeatRunner#run()}, after all iterations have been
     * executed.
     * @return {@link Flowable} instance.
     */
    @NotNull
    Flowable<Boolean> rx_onAllTestsFinished();
}
