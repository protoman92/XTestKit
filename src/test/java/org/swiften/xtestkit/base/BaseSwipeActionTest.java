package org.swiften.xtestkit.base;

import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;
import org.swiften.javautilities.number.NumberUtil;
import org.swiften.javautilities.rx.CustomTestSubscriber;
import org.swiften.javautilities.rx.RxTestUtil;
import org.swiften.xtestkit.base.element.swipe.BaseSwipeType;
import org.swiften.xtestkit.base.element.swipe.SwipeType;
import org.swiften.xtestkit.base.element.swipe.SwipeParam;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.testng.Assert.assertTrue;

/**
 * Created by haipham on 5/11/17.
 */
public class BaseSwipeActionTest implements BaseSwipeType {
    @NotNull private final BaseSwipeType ENGINE;
    @NotNull private final WebDriver DRIVER;

    {
        /* We initialize a driver here in order to access a common mock that
         * stores call counts on its methods. Tests that require the driver()
         * method to throw an Exception should stub the method themselves */
        DRIVER = mock(WebDriver.class);

        ENGINE = spy(this);
    }

    @BeforeMethod
    public void beforeMethod() {}

    @AfterMethod
    public void afterMethod() {
        reset(DRIVER, ENGINE);
    }

    @NotNull
    @Override
    public WebDriver driver() {
        return DRIVER;
    }

    @Override
    public void swipeOnce(@NotNull SwipeType param) {}

    //region Swipe
    @Test
    @SuppressWarnings("unchecked")
    public void test_swipe_shouldSucceed() {
        // Setup
        int times = NumberUtil.randomBetween(1, 5);

        SwipeParam param = SwipeParam.builder()
            .withDelay(100)
            .withTimes(times)
            .build();

        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ENGINE.rxa_swipe(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(RxTestUtil.firstNextEvent(subscriber));
        verify(ENGINE).rxa_swipe(any());
        verify(ENGINE, times(times)).swipeOnce(any());
        verify(ENGINE, times(times)).rxa_swipeOnce(any());
        verifyNoMoreInteractions(ENGINE);
    }
    //endregion
}
