package org.swiften.xtestkit.engine.base.action;

import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.swiften.javautilities.log.LogUtil;
import org.swiften.javautilities.number.NumberTestUtil;
import org.swiften.javautilities.rx.CustomTestSubscriber;
import org.swiften.javautilities.rx.RxTestUtil;
import org.swiften.xtestkit.engine.base.BaseEngineTest;
import org.swiften.xtestkit.engine.base.PlatformView;
import org.swiften.xtestkit.engine.base.action.general.type.BaseActionType;
import org.swiften.xtestkit.engine.base.capability.CapType;
import org.swiften.xtestkit.engine.base.param.AlertParam;
import org.swiften.xtestkit.engine.base.param.NavigateBack;
import org.swiften.xtestkit.engine.base.param.SwipeGestureParam;
import org.swiften.xtestkit.engine.base.type.RetryType;
import org.swiften.xtestkit.system.NetworkHandler;
import org.swiften.xtestkit.system.ProcessRunner;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.testng.Assert.assertTrue;

/**
 * Created by haipham on 5/8/17.
 */
public class BaseActionTest implements BaseActionType {
    @NotNull private final BaseActionType ENGINE;
    @NotNull private final WebDriver DRIVER;
    @NotNull private final Alert ALERT;
    @NotNull private final WebDriver.Navigation NAVIGATION;
    @NotNull private final WebDriver.TargetLocator TARGET_LOCATOR;

    {
        /* We initialize a driver here in order to access a common mock that
         * stores call counts on its methods. Tests that require the driver()
         * method to throw an Exception should stub the method themselves */
        DRIVER = mock(WebDriver.class);

        /* We initialize a Navigate object and ask the driver to return it
         * every time driver.navigate() is called */
        NAVIGATION = mock(WebDriver.Navigation.class);

        /* Return this mock when the driver requests switchTo() */
        TARGET_LOCATOR = mock(WebDriver.TargetLocator.class);

        /* Return this mock when we call TARGET_LOCATOR.alert() */
        ALERT = mock(Alert.class);

        ENGINE = spy(this);
    }

    @BeforeMethod
    public void beforeMethod() {
        when(DRIVER.navigate()).thenReturn(NAVIGATION);
        when(DRIVER.switchTo()).thenReturn(TARGET_LOCATOR);
        when(TARGET_LOCATOR.alert()).thenReturn(ALERT);
    }

    @AfterMethod
    public void afterMethod() {
        reset(DRIVER, ENGINE, NAVIGATION);
    }

    @NotNull
    @Override
    public WebDriver driver() {
        return DRIVER;
    }

    //region Alert
    @Test
    @SuppressWarnings("unchecked")
    public void test_acceptAlert_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ENGINE.rxAcceptAlert().subscribe(subscriber);

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(RxTestUtil.getFirstNextEvent(subscriber));
        verify(ENGINE).driver();
        verify(ENGINE).rxAcceptAlert();
        verify(ENGINE).rxDismissAlert(any(AlertParam.class));
        verifyNoMoreInteractions(ENGINE);
    }
    //endregion

    //region Navigate Back
    @Test
    @SuppressWarnings("unchecked")
    public void test_navigateBack_shouldSucceed() {
        // Setup
        int times = NumberTestUtil.randomBetween(1, 5);

        NavigateBack param = NavigateBack.builder()
            .withTimes(times)
            .withDelay(100)
            .build();

        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ENGINE.rxNavigateBack(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(RxTestUtil.getFirstNextEvent(subscriber));
        verify(ENGINE).rxNavigateBack(any());
        verify(ENGINE).driver();
        verify(NAVIGATION, times(times)).back();
        verifyNoMoreInteractions(ENGINE);
    }
    //endregion

    //region Swipe
    @Test
    @SuppressWarnings("unchecked")
    public void test_swipe_shouldSucceed() {
        // Setup
        int times = NumberTestUtil.randomBetween(1, 5);

        SwipeGestureParam param = SwipeGestureParam.builder()
            .withDelay(100)
            .withTimes(times)
            .build();

        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ENGINE.rxSwipe(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(RxTestUtil.getFirstNextEvent(subscriber));
        verify(ENGINE).rxSwipe(any());
        verify(ENGINE).rxSwipeOnce(any());
        verifyNoMoreInteractions(ENGINE);
    }
    //endregion
}
