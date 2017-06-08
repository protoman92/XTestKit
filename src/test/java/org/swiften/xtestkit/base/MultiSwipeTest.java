package org.swiften.xtestkit.base;

import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.rx.CustomTestSubscriber;
import org.swiften.javautilities.rx.RxUtil;
import org.swiften.xtestkitcomponents.direction.Unidirection;
import org.swiften.xtestkit.base.element.swipe.MultiSwipeType;
import org.swiften.xtestkit.base.element.swipe.SwipeType;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Random;

import static org.mockito.Mockito.*;

/**
 * Created by haipham on 5/12/17.
 */
public class MultiSwipeTest implements MultiSwipeType {
    @NotNull private final MultiSwipeType ENGINE;
    @NotNull private final WebElement ELEMENT;
    @NotNull private final Random RAND;
    private final int TOTAL_SWIPE = 20;
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

    @NotNull
    @Override
    public Flowable<Double> rxe_elementSwipeRatio() {
        return Flowable.just(0.9d);
    }

    @NotNull
    @Override
    public Flowable<Unidirection> rxe_swipeDirection() {
        return Flowable.just(Unidirection.UP_DOWN);
    }

    @NotNull
    @Override
    public Flowable<Boolean> rxv_shouldKeepSwiping() {
        currentSwipeCount += 1;

        if (currentSwipeCount < TOTAL_SWIPE) {
            return RAND.nextBoolean() ? Flowable.empty() : RxUtil.error("");
        } else {
            return Flowable.just(true);
        }
    }

    @NotNull
    @Override
    public Flowable<WebElement> rxe_scrollableViewToSwipe() {
        return Flowable.just(ELEMENT);
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
        ENGINE.rxa_performAction().subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        verify(ENGINE).rxa_performAction();
        verify(ENGINE, times(TOTAL_SWIPE)).rxa_swipeRecursively();
        verify(ENGINE, times(TOTAL_SWIPE)).rxv_shouldKeepSwiping();
        verify(ENGINE, times(TOTAL_SWIPE)).rxe_scrollableViewToSwipe();
        verify(ENGINE, times(TOTAL_SWIPE)).rxe_swipeDirection();
        verify(ENGINE, times(TOTAL_SWIPE)).rxe_elementSwipeRatio();
        verify(ENGINE, times(TOTAL_SWIPE - 1)).rxa_swipeElement(any(), any(), anyDouble());
        verifyNoMoreInteractions(ENGINE);
    }
}
