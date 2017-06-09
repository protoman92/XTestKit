package org.swiften.xtestkit.base;

import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.rx.CustomTestSubscriber;
import org.swiften.javautilities.rx.RxUtil;
import org.swiften.xtestkitcomponents.direction.Direction;
import org.swiften.xtestkit.base.element.swipe.MultiSwipeComparisonType;
import org.swiften.xtestkit.base.element.swipe.SwipeType;
import org.testng.annotations.Test;

import java.util.Random;

import static org.mockito.Mockito.*;

/**
 * Created by haipham on 5/12/17.
 */
public class MultiSwipeComparisonTest implements MultiSwipeComparisonType {
    @NotNull private final MultiSwipeComparisonType ENGINE;
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

    @NotNull
    @Override
    public Flowable<Double> rxe_elementSwipeRatio() {
        return Flowable.just(0.9d);
    }

    @NotNull
    @Override
    public Flowable<Integer> rxe_initialDifference(@NotNull WebElement element) {
        return Flowable.just(INITIAL_SWIPE);
    }

    @NotNull
    @Override
    public Flowable<WebElement> rxe_scrollableViewToSwipe() {
        return Flowable.just(ELEMENT);
    }

    @NotNull
    @Override
    public Flowable<WebElement> rxe_scrollViewChildItems() {
        return Flowable.just(ELEMENT);
    }

    @NotNull
    @Override
    public Flowable<Boolean> rxv_shouldKeepSwiping() {
        currentSwipeCount += 1;

        if (currentSwipeCount < TOTAL_SWIPE) {
            return RAND.nextBoolean() ? Flowable.empty() : RxUtil.error();
        } else {
            return Flowable.just(true);
        }
    }

    @NotNull
    @Override
    public Flowable<Boolean> rxa_compareLast(@NotNull WebElement element) {
        return Flowable.just(true);
    }

    @NotNull
    @Override
    public Flowable<Boolean> rxa_compareFirst(@NotNull WebElement element) {
        return Flowable.just(true);
    }

    @NotNull
    @Override
    public Flowable<Boolean> rxa_swipeOnce(@NotNull SwipeType param) {
        return Flowable.just(true);
    }

    @NotNull
    @Override
    public Flowable<Boolean> rxa_swipeElement(
        @NotNull WebElement element,
        @NotNull Direction direction,
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
        ENGINE.rxa_performAction().subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        verify(ENGINE, times(TOTAL_SWIPE + 1)).rxe_elementSwipeRatio();
        verify(ENGINE, times(TOTAL_SWIPE + 1)).defaultDirection();
        verify(ENGINE, times(TOTAL_SWIPE)).firstElementDirection();
//        verify(ENGINE, times(TOTAL_SWIPE)).lastElementDirection();
        verify(ENGINE).rxa_performAction();
        verify(ENGINE).rxa_swipeInitially();
        verify(ENGINE).rxa_swipeInitially(any(), any(), anyInt());
        verify(ENGINE).rxe_initialSwipesCount();
        verify(ENGINE, times(TOTAL_SWIPE)).rxa_swipeRecursively();
        verify(ENGINE, times(TOTAL_SWIPE)).rxv_shouldKeepSwiping();
        verify(ENGINE, times(TOTAL_SWIPE + 1)).rxe_scrollableViewToSwipe();
        verify(ENGINE, times(TOTAL_SWIPE + 1)).rxe_swipeDirection();
        verify(ENGINE, times(TOTAL_SWIPE + 2)).rxe_firstVisibleChild();
        verify(ENGINE, times(TOTAL_SWIPE + 1)).rxe_lastVisibleChild();

        verify(ENGINE, times(TOTAL_SWIPE * 2 + INITIAL_SWIPE - 1))
            .rxe_scrollViewChildItems();

        verify(ENGINE, times(TOTAL_SWIPE + INITIAL_SWIPE - 1))
            .rxa_swipeElement(any(), any(), anyDouble());
    }
}
