package com.swiften.xtestkit.test.protocol;

import com.swiften.xtestkit.test.RepeatRunner;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 4/6/17.
 */
public interface TestListener {
    /**
     * Cal this method when test are first started.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    Flowable<Boolean> rxOnFreshStart();

    /**
     * Call this method when a batch of test is started.
     * @param indexes The indexes of batch items. An Array of {@link Integer}.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    Flowable<Boolean> rxOnBatchStarted(@NotNull int[] indexes);

    /**
     * Call this method when a batch of test is finished.
     * @param indexes The indexes for batch items. An Array of {@link Integer}.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    Flowable<Boolean> rxOnBatchFinished(@NotNull int[] indexes);

    /**
     * Call this method when all test have finished, typically at the
     * end of {@link RepeatRunner#run()}, after all iterations have been
     * executed.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    Flowable<Boolean> rxOnAllTestsFinished();
}
