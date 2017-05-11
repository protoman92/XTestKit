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
import org.swiften.xtestkit.base.model.MockPlatform;
import org.swiften.xtestkit.base.model.MockPlatformView;
import org.swiften.xtestkit.base.PlatformView;
import org.swiften.xtestkit.base.element.locator.general.type.BaseLocatorType;
import org.swiften.xtestkit.base.element.locator.general.xpath.XPath;
import org.swiften.xtestkit.base.param.ByXPath;
import org.swiften.xtestkit.base.param.HintParam;
import org.swiften.xtestkit.base.param.TextParam;
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
    @NotNull private final BaseLocatorTest LOCATOR;
    @NotNull private final WebDriver DRIVER;
    @NotNull private final Localizer LOCALIZER;
    @NotNull private final String LOCALIZED_TEXT;
    @NotNull private final MockPlatformView PLATFORM_VIEWS;
    private final int ELEMENT_COUNT;

    {
        /* Return this localizer when we call LOCATOR.localizer() */
        LOCALIZER = mock(Localizer.class);
        LOCALIZED_TEXT = "Localized Result";

        /* We initialize a driver here in order to access a common mock that
         * stores call counts on its methods. Tests that require the driver()
         * method to throw an Exception should stub the method themselves */
        DRIVER = mock(WebDriver.class);

        /* This PlatformView object will be returned by LOCATOR. We return
         * an implementation whose getViews() method returns a list of mock
         * WebElement, instead of a mock PlatformView. This way, we do not
         * have to rewrite view login for PlatformView */
        PLATFORM_VIEWS = spy(new MockPlatformView());

        /* The number of elements to return for a DRIVER.findElement request */
        ELEMENT_COUNT = 2;

        LOCATOR = spy(this);
    }

    @BeforeMethod
    public void beforeMethod() {
        doReturn(DRIVER).when(LOCATOR).driver();
        doReturn(PLATFORM_VIEWS).when(LOCATOR).platformView();
        doReturn(LOCALIZED_TEXT).when(LOCALIZER).localize(anyString());
        doReturn(Flowable.just(LOCALIZED_TEXT)).when(LOCALIZER).rxLocalize(anyString());
        doReturn(LOCALIZER).when(LOCATOR).localizer();

        when(DRIVER.findElements(any(By.class))).thenReturn(
            Arrays
                .stream(new Object[ELEMENT_COUNT])
                .map(a -> mock(WebElement.class))
                .collect(Collectors.toList())
        );
    }

    @AfterMethod
    public void afterMethod() {
        reset(DRIVER, LOCATOR, PLATFORM_VIEWS);
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
    public void test_failToFindElements_shouldThrow() {
        // Setup
        when(DRIVER.findElements(any(By.ByXPath.class)))
            .thenThrow(new RuntimeException());

        List<BaseViewType> views = PLATFORM_VIEWS.allViews();

        ByXPath param = ByXPath.builder()
            .withClasses(views)
            .withXPath(XPath.EMPTY)
            .withError("")
            .build();

        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        LOCATOR.rxElementsByXPath(param).subscribe(subscriber);
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
        LOCATOR.rxElementsByXPath(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();

        assertEquals(
            RxTestUtil.getNextEvents(subscriber).size(),
            PLATFORM_VIEWS.VIEW_COUNT * ELEMENT_COUNT);

        views.forEach(a -> {
            verify(a).className();
        });
    }
    //endregion

    //region Element With Text
    @Test
    @SuppressWarnings("unchecked")
    public void test_elementsWithText_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();
        TextParam param = mock(TextParam.class);
        doReturn(mock(TextParam.class)).when(param).withNewText(any());
        doReturn("").when(param).value();

        // When
        LOCATOR.rxElementsWithText(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        verify(LOCATOR).rxElementsByXPath(any(ByXPath.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_elementWithTextWithNoElement_shouldThrow() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();
        TextParam param = mock(TextParam.class);
        doReturn(mock(TextParam.class)).when(param).withNewText(any());
        doReturn("").when(param).value();
        doReturn(Collections.emptyList()).when(DRIVER).findElements(any());

        when(DRIVER.findElements(any(By.ByXPath.class)))
            .thenReturn(Collections.emptyList());

        // When
        LOCATOR.rxElementWithText(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertErrorMessage(noElementsWithText(LOCALIZED_TEXT));
        subscriber.assertNotComplete();
        verify(LOCATOR).rxElementsByXPath(any(ByXPath.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_elementWithText_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();
        TextParam param = mock(TextParam.class);
        doReturn(mock(TextParam.class)).when(param).withNewText(any());
        doReturn("").when(param).value();

        // When
        LOCATOR.rxElementWithText(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(RxTestUtil.getFirstNextEvent(subscriber) instanceof WebElement);
        verify(LOCATOR).rxElementsByXPath(any(ByXPath.class));
    }
    //endregion

    //region Element Containing Text
    @Test
    @SuppressWarnings("unchecked")
    public void test_elementsContainingText_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();
        TextParam param = mock(TextParam.class);
        doReturn(mock(TextParam.class)).when(param).withNewText(any());
        doReturn("").when(param).value();

        // When
        LOCATOR.rxElementsContainingText(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        verify(LOCATOR).rxElementsByXPath(any(ByXPath.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_elementContainingTextWithNoElement_shouldThrow() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();
        TextParam param = mock(TextParam.class);
        doReturn(mock(TextParam.class)).when(param).withNewText(any());
        doReturn("").when(param).value();
        doReturn(Collections.emptyList()).when(DRIVER).findElements(any());

        // When
        LOCATOR.rxElementContainingText(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertErrorMessage(noElementsContainingText(LOCALIZED_TEXT));
        subscriber.assertNotComplete();
        verify(LOCATOR).rxElementsByXPath(any(ByXPath.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_elementContainingText_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();
        TextParam param = mock(TextParam.class);
        doReturn(mock(TextParam.class)).when(param).withNewText(any());
        doReturn("").when(param).value();

        // When
        LOCATOR.rxElementContainingText(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(RxTestUtil.getFirstNextEvent(subscriber) instanceof WebElement);
        verify(LOCATOR).rxElementsByXPath(any(ByXPath.class));
    }
    //endregion

    //region Element With Hint
    @Test
    @SuppressWarnings("unchecked")
    public void test_elementsWithHint_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();
        HintParam param = mock(HintParam.class);
        doReturn(mock(HintParam.class)).when(param).withNewText(any());
        doReturn("").when(param).value();

        // When
        LOCATOR.rxElementsWithHint(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        verify(LOCATOR).rxElementsByXPath(any(ByXPath.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_elementWithHintWithNoElement_shouldThrow() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();
        HintParam param = mock(HintParam.class);
        doReturn(mock(HintParam.class)).when(param).withNewText(any());
        doReturn("").when(param).value();
        doReturn(Collections.emptyList()).when(DRIVER).findElements(any());

        // When
        LOCATOR.rxElementWithHint(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertErrorMessage(noElementsWithHint(LOCALIZED_TEXT));
        subscriber.assertNotComplete();
        verify(LOCATOR).rxElementsByXPath(any(ByXPath.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_elementWithHint_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();
        HintParam param = mock(HintParam.class);
        doReturn(mock(HintParam.class)).when(param).withNewText(any());
        doReturn("").when(param).value();

        // When
        LOCATOR.rxElementWithHint(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(RxTestUtil.getFirstNextEvent(subscriber) instanceof WebElement);
        verify(LOCATOR).rxElementsByXPath(any(ByXPath.class));
    }
    //endregion

    //region Element Containing Hint
    @Test
    @SuppressWarnings("unchecked")
    public void test_elementsContainingHint_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();
        HintParam param = mock(HintParam.class);
        doReturn(mock(HintParam.class)).when(param).withNewText(any());
        doReturn("").when(param).value();

        // When
        LOCATOR.rxElementsContainingHint(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        verify(LOCATOR).rxElementsByXPath(any(ByXPath.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_elementContainingHintWithNoElement_shouldThrow() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();
        HintParam param = mock(HintParam.class);
        doReturn(mock(HintParam.class)).when(param).withNewText(any());
        doReturn("").when(param).value();
        doReturn(Collections.emptyList()).when(DRIVER).findElements(any());

        // When
        LOCATOR.rxElementContainingHint(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertErrorMessage(noElementsContainingHint(LOCALIZED_TEXT));
        subscriber.assertNotComplete();
        verify(LOCATOR).rxElementsByXPath(any(ByXPath.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_elementContainingHint_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();
        HintParam param = mock(HintParam.class);
        doReturn(mock(HintParam.class)).when(param).withNewText(any());
        doReturn("").when(param).value();

        // When
        LOCATOR.rxElementContainingHint(param).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        assertTrue(RxTestUtil.getFirstNextEvent(subscriber) instanceof WebElement);
        verify(LOCATOR).rxElementsByXPath(any(ByXPath.class));
    }
    //endregion
}
