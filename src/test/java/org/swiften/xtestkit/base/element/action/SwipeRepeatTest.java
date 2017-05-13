package org.swiften.xtestkit.base.element.action;

import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import static org.mockito.Mockito.*;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.rx.CustomTestSubscriber;
import org.swiften.javautilities.rx.RxUtil;
import org.swiften.xtestkit.base.element.action.general.model.Unidirection;
import org.swiften.xtestkit.base.element.action.swipe.type.SwipeRepeatType;
import org.swiften.xtestkit.base.element.action.swipe.type.SwipeType;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Random;

/**
 * Created by haipham on 5/12/17.
 */
public class SwipeRepeatTest implements SwipeRepeatType {
    @NotNull private final SwipeRepeatType ENGINE;
    @NotNull private final WebElement ELEMENT;
    @NotNull private final Random RAND;
    private final int TOTAL_SWIPE = 10;
    private int currentSwipeCount;

    {
        RAND = new Random();
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

        if (currentSwipeCount < TOTAL_SWIPE) {
            return RAND.nextBoolean() ? Flowable.empty() : RxUtil.error("");
        } else {
            return Flowable.just(true);
        }
    }

    @NotNull
    @Override
    public Flowable<WebElement> rxScrollableViewToSwipe() {
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
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        verify(ENGINE).rxRepeatSwipe();
        verify(ENGINE, times(TOTAL_SWIPE)).rxSwipeRecursively();
        verify(ENGINE, times(TOTAL_SWIPE)).rxShouldKeepSwiping();
        verify(ENGINE, times(TOTAL_SWIPE)).rxScrollableViewToSwipe();
        verify(ENGINE, times(TOTAL_SWIPE)).rxDirectionToSwipe();
        verify(ENGINE, times(TOTAL_SWIPE)).delayEveryIteration();
        verify(ENGINE, times(TOTAL_SWIPE - 1)).elementSwipeRatio();
        verify(ENGINE, times(TOTAL_SWIPE - 1)).rxSwipeElement(any(), any(), anyDouble());
        verifyNoMoreInteractions(ENGINE);
    }
}