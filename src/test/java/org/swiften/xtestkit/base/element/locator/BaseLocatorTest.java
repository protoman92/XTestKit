package org.swiften.xtestkit.base.element.locator;

import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.localizer.Localizer;
import org.swiften.javautilities.localizer.LocalizerType;
import org.swiften.javautilities.rx.CustomTestSubscriber;
import org.swiften.javautilities.rx.RxTestUtil;
import org.swiften.javautilities.rx.RxUtil;
import org.swiften.xtestkit.base.model.MockPlatform;
import org.swiften.xtestkit.base.model.MockPlatformView;
import org.swiften.xtestkit.base.PlatformView;
import org.swiften.xtestkit.base.element.locator.general.type.BaseLocatorType;
import org.swiften.xtestkit.base.element.locator.general.xpath.XPath;
import org.swiften.xtestkit.base.element.locator.general.param.ByXPath;
import org.swiften.xtestkit.base.element.locator.general.param.TextParam;
import org.swiften.xtestkit.base.type.BaseViewType;
import org.swiften.xtestkit.base.type.PlatformType;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Created by haipham on 5/8/17.
 */
public class BaseLocatorTest implements BaseLocatorType {
    @NotNull private final BaseLocatorTest ENGINE;
    @NotNull private final WebDriver DRIVER;
    @NotNull private final Localizer LOCALIZER;
    @NotNull private final String LOCALIZED_TEXT;
    @NotNull private final MockPlatformView PLATFORM_VIEWS;
    private final int ELEMENT_COUNT;

    {
        /* Return this localizer when we call ENGINE.localizer() */
        LOCALIZER = mock(Localizer.class);
        LOCALIZED_TEXT = "Localized Result";

        /* We initialize a driver here in order to access a common mock that
         * stores call counts on its methods. Tests that require the driver()
         * method to throw an Exception should stub the method themselves */
        DRIVER = mock(WebDriver.class);

        /* This PlatformView object will be returned by ENGINE. We return
         * an implementation whose getViews() method returns a list of mock
         * WebElement, instead of a mock PlatformView. This way, we do not
         * have to rewrite view login for PlatformView */
        PLATFORM_VIEWS = spy(new MockPlatformView());

        /* The number of elements to return for a DRIVER.findElement request */
        ELEMENT_COUNT = 2;

        ENGINE = spy(this);
    }

    @BeforeMethod
    public void beforeMethod() {
        doReturn(DRIVER).when(ENGINE).driver();
        doReturn(PLATFORM_VIEWS).when(ENGINE).platformView();
        doReturn(LOCALIZED_TEXT).when(LOCALIZER).localize(anyString());
        doReturn(Flowable.just(LOCALIZED_TEXT)).when(LOCALIZER).rxLocalize(anyString());
        doReturn(LOCALIZER).when(ENGINE).localizer();

        when(DRIVER.findElements(any(By.class))).thenReturn(
            Arrays
                .stream(new Object[ELEMENT_COUNT])
                .map(a -> mock(WebElement.class))
                .collect(Collectors.toList())
        );
    }

    @AfterMethod
    public void afterMethod() {
        reset(DRIVER, ENGINE, PLATFORM_VIEWS);
    }

    @NotNull
    @Override
    public WebDriver driver() {
        return DRIVER;
    }

    @NotNull
    @Override
    public PlatformType platform() {
        return spy(new MockPlatform());
    }

    @NotNull
    @Override
    public PlatformView platformView() {
        return PLATFORM_VIEWS;
    }

    @NotNull
    public LocalizerType localizer() {
        return LOCALIZER;
    }

    //region Element By XPATH
    @Test
    @SuppressWarnings("unchecked")
    public void test_elementsByXPathVarargs_shouldSucceed() {
        // Setup
        ByXPath query1 = mock(ByXPath.class);
        ByXPath query2 = mock(ByXPath.class);
        ByXPath query3 = mock(ByXPath.class);
        WebElement element1 = mock(WebElement.class);
        WebElement element2 = mock(WebElement.class);
        doReturn(RxUtil.error()).when(ENGINE).rx_byXPath(eq(query1));
        doReturn(Flowable.just(element1)).when(ENGINE).rx_byXPath(eq(query2));
        doReturn(Flowable.just(element2)).when(ENGINE).rx_byXPath(eq(query3));
        doReturn(RxUtil.error()).when(ENGINE).rx_xPathQueryFailure(any());

        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ENGINE.rx_byXPath(query1, query2, query3).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_elementsByXPathVarargsWithNoElement_shouldThrow() {
        // Setup
        ByXPath query1 = mock(ByXPath.class);
        ByXPath query2 = mock(ByXPath.class);
        ByXPath query3 = mock(ByXPath.class);
        String error1 = "Error1", error2 = "Error2", error3 = "Error3";
        String aggregatedError = String.join("\n", error1, error2, error3);
        doReturn(error1).when(query1).error();
        doReturn(error2).when(query2).error();
        doReturn(error3).when(query3).error();

        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ENGINE.rx_byXPath(query1, query2, query3).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertErrorMessage(aggregatedError);
        subscriber.assertNotComplete();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_failToFindElements_shouldThrow() {
        // Setup
        doReturn(Collections.emptyList()).when(DRIVER).findElements(any());
        List<BaseViewType> views = PLATFORM_VIEWS.allViews();

        ByXPath param = ByXPath.builder()
            .withClasses(views)
            .withXPath(XPath.EMPTY)
            .withError("")
            .build();

        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ENGINE.rx_byXPath(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertError(Exception.class);
        subscriber.assertNotComplete();
        views.forEach(a -> verify(a, atLeastOnce()).className());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_elementsByXPath_shouldSucceed() {
        // Setup
        List<BaseViewType> views = PLATFORM_VIEWS.allViews();

        ByXPath param = ByXPath.builder()
            .withClasses(views)
            .withXPath(XPath.EMPTY)
            .withError("")
            .build();

        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ENGINE.rx_byXPath(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();

        assertEquals(
            RxTestUtil.nextEvents(subscriber).size(),
            PLATFORM_VIEWS.VIEW_COUNT * ELEMENT_COUNT);

        views.forEach(a -> verify(a).className());
    }
    //endregion

    //region Element With Text
    @Test
    @SuppressWarnings("unchecked")
    public void test_elementsWithText_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();
        TextParam param = mock(TextParam.class);
        doReturn("").when(param).value();

        // When
        ENGINE.rx_withText(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        verify(ENGINE).rx_byXPath(any(ByXPath.class));
    }
    //endregion

    //region Element Containing Text
    @Test
    @SuppressWarnings("unchecked")
    public void test_elementsContainingText_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();
        TextParam param = mock(TextParam.class);
        doReturn("").when(param).value();

        // When
        ENGINE.rx_containsText(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        verify(ENGINE).rx_byXPath(any(ByXPath.class));
    }
    //endregion
}
