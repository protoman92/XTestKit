package org.swiften.xtestkit.base;

import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.swiften.javautilities.number.NumberUtil;
import org.swiften.javautilities.rx.CustomTestSubscriber;
import org.swiften.javautilities.rx.RxUtil;
import org.swiften.xtestkit.base.element.general.ActionType;
import org.swiften.xtestkit.base.param.AlertParam;
import org.swiften.xtestkit.base.param.NavigateBack;
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
public class ActionTest implements ActionType {
    @NotNull private final ActionType ENGINE;
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
        ENGINE.rxa_acceptAlert().subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(RxUtil.firstNextEvent(subscriber));
        verify(ENGINE).driver();
        verify(ENGINE).rxa_acceptAlert();
        verify(ENGINE).rxa_dismissAlert(any(AlertParam.class));
        verifyNoMoreInteractions(ENGINE);
    }
    //endregion

    //region Navigate Back
    @Test
    @SuppressWarnings("unchecked")
    public void test_navigateBack_shouldSucceed() {
        // Setup
        int times = NumberUtil.randomBetween(1, 5);

        NavigateBack param = NavigateBack.builder()
            .withTimes(times)
            .withDelay(100)
            .build();

        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ENGINE.rxa_navigateBack(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(RxUtil.firstNextEvent(subscriber));
        verify(ENGINE, times(times)).rxa_navigateBackOnce();
        verify(ENGINE).rxa_navigateBack(any());
        verify(ENGINE, atLeastOnce()).driver();
        verify(NAVIGATION, times(times)).back();
        verifyNoMoreInteractions(ENGINE);
    }
    //endregion
}
