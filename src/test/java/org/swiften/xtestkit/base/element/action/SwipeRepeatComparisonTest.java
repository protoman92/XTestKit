package org.swiften.xtestkit.base.element.action;

import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import static org.mockito.Mockito.*;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.number.NumberTestUtil;
import org.swiften.javautilities.rx.CustomTestSubscriber;
import org.swiften.javautilities.rx.RxUtil;
import org.swiften.xtestkit.base.element.action.general.model.Unidirection;
import org.swiften.xtestkit.base.element.action.swipe.type.SwipeRepeatComparisonType;
import org.swiften.xtestkit.base.element.action.swipe.type.SwipeType;
import org.testng.annotations.Test;

import java.util.Random;

/**
 * Created by haipham on 5/12/17.
 */
public class SwipeRepeatComparisonTest implements SwipeRepeatComparisonType {
    @NotNull private final SwipeRepeatComparisonType ENGINE;
    @NotNull private final Random RAND;
    @NotNull private final WebElement ELEMENT;
    private final int INITIAL_SWIPE = 5;
    private final int TOTAL_SWIPE = 3;
    private int currentSwipeCount;

    {
        ELEMENT = mock(WebElement.class);
        RAND = new Random();
        ENGINE = spy(this);
    }

    @Override
    public double elementSwipeRatio() {
        return 0.9d;
    }

    @NotNull
    @Override
    public Flowable<Integer> rxInitialDifference(@NotNull WebElement element) {
        return Flowable.just(INITIAL_SWIPE);
    }

    @NotNull
    @Override
    public Flowable<WebElement> rxScrollableViewToSwipe() {
        return Flowable.just(ELEMENT);
    }

    @NotNull
    @Override
    public Flowable<WebElement> rxScrollViewChildItems() {
        return Flowable.just(ELEMENT);
    }

    @NotNull
    @Override
    public Flowable<Boolean> rxShouldKeepSwiping() {
        currentSwipeCount += 1;

        if (currentSwipeCount < TOTAL_SWIPE) {
            return RAND.nextBoolean() ? Flowable.empty() : RxUtil.error();
        } else {
            return Flowable.just(true);
        }
    }

    @NotNull
    @Override
    public Flowable<Boolean> rxCompareLast(@NotNull WebElement element) {
        return Flowable.just(true);
    }

    @NotNull
    @Override
    public Flowable<Boolean> rxCompareFirst(@NotNull WebElement element) {
        return Flowable.just(true);
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
        verify(ENGINE, times(TOTAL_SWIPE)).delayEveryIteration();
        verify(ENGINE, times(TOTAL_SWIPE + 1)).elementSwipeRatio();
        verify(ENGINE, times(TOTAL_SWIPE + 1)).defaultDirection();
        verify(ENGINE, times(TOTAL_SWIPE)).firstElementDirection();
//        verify(ENGINE, times(TOTAL_SWIPE)).lastElementDirection();
        verify(ENGINE).rxRepeatSwipe();
        verify(ENGINE).rxPerformInitialSwipes();
        verify(ENGINE).rxPerformInitialSwipes(any(), any(), anyDouble(), anyInt());
        verify(ENGINE).rxInitialSwipesCount();
        verify(ENGINE, times(TOTAL_SWIPE)).rxSwipeRecursively();
        verify(ENGINE, times(TOTAL_SWIPE)).rxShouldKeepSwiping();
        verify(ENGINE, times(TOTAL_SWIPE + 1)).rxScrollableViewToSwipe();
        verify(ENGINE, times(TOTAL_SWIPE + 1)).rxDirectionToSwipe();
        verify(ENGINE, times(TOTAL_SWIPE + 2)).rxFirstVisibleChildElement();
        verify(ENGINE, times(TOTAL_SWIPE + 1)).rxLastVisibleChildElement();

        verify(ENGINE, times(TOTAL_SWIPE * 2 + INITIAL_SWIPE - 1))
            .rxScrollViewChildItems();

        verify(ENGINE, times(TOTAL_SWIPE + INITIAL_SWIPE - 1))
            .rxSwipeElement(any(), any(), anyDouble());
    }
}
