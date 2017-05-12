package org.swiften.xtestkit.base.element.action;

import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import static org.mockito.Mockito.*;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.log.LogUtil;
import org.swiften.javautilities.rx.CustomTestSubscriber;
import org.swiften.javautilities.rx.RxTestUtil;
import org.swiften.xtestkit.base.element.action.general.model.Unidirection;
import org.swiften.xtestkit.base.element.action.swipe.type.SwipeRepeatableType;
import org.swiften.xtestkit.base.element.action.swipe.type.SwipeType;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Created by haipham on 5/12/17.
 */
public class SwipeRepeatableTest implements SwipeRepeatableType {
    @NotNull private final SwipeRepeatableType ENGINE;
    @NotNull private final WebElement ELEMENT;
    private int totalSwipeCount = 10, currentSwipeCount;

    {
        ELEMENT = mock(WebElement.class);
        ENGINE = spy(this);
    }

    @BeforeMethod
    public void beforeMethod() {
        currentSwipeCount = 0;
    }

    @Override
    public double elementSwipeRatio() {
        return 0.9d;
    }

    @NotNull
    @Override
    public Flowable<Unidirection> rxDirectionToSwipe() {
        return Flowable.just(Unidirection.UP_DOWN);
    }

    @NotNull
    @Override
    public Flowable<Boolean> rxShouldKeepSwiping() {
        currentSwipeCount += 1;

        if (currentSwipeCount < totalSwipeCount) {
            return Flowable.empty();
        } else {
            return Flowable.just(true);
        }
    }

    @NotNull
    @Override
    public Flowable<WebElement> rxScrollableElementToSwipe() {
        return Flowable.just(ELEMENT);
    }

    @NotNull
    @Override
    public Flowable<Boolean> rxSwipeOnce(@NotNull SwipeType param) {
        return Flowable.just(true);
    }

    @NotNull
    @Override
    public Flowable<Boolean> rxSwipeElement(
        @NotNull WebElement element,
        @NotNull Unidirection direction,
        double scrollRatio
    ) {
        return Flowable.just(true);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_swipeRepeatedly_shouldWork() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ENGINE.rxRepeatSwipe().subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        verify(ENGINE, times(totalSwipeCount)).rxRepeatSwipe();
        verify(ENGINE, times(totalSwipeCount)).rxShouldKeepSwiping();
        verify(ENGINE, times(totalSwipeCount)).rxScrollableElementToSwipe();
        verify(ENGINE, times(totalSwipeCount)).rxDirectionToSwipe();
        verify(ENGINE, times(totalSwipeCount)).delayEveryIteration();
        verify(ENGINE, times(totalSwipeCount - 1)).elementSwipeRatio();
        verify(ENGINE, times(totalSwipeCount - 1)).rxSwipeElement(any(), any(), anyDouble());
        verifyNoMoreInteractions(ENGINE);
    }
}
