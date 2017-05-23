package org.swiften.xtestkit.base.element.action;

import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.collection.CollectionUtil;
import org.swiften.javautilities.log.LogUtil;
import org.swiften.javautilities.rx.CustomTestSubscriber;
import org.swiften.xtestkit.base.Engine;
import org.swiften.xtestkit.base.element.action.choice.type.ChoiceSelectorSwipeType;
import org.swiften.xtestkit.base.element.action.general.model.Unidirection;
import org.swiften.xtestkit.base.element.action.input.type.ChoiceInputType;
import org.swiften.xtestkit.base.element.locator.general.xpath.XPath;
import org.swiften.xtestkit.base.type.PlatformType;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.mockito.Mockito.*;

/**
 * Created by haipham on 5/24/17.
 */
public class ChoiceSelectorSwipeTest implements ChoiceSelectorSwipeType {
    private static final int ITEM_COUNT = 100;
    private static final int ITEM_PER_PAGE = 15;

    @NotNull private static final XPath CHOICE_LIST_VIEW_XPATH;

    static {
        CHOICE_LIST_VIEW_XPATH = spy(XPath.class);
    }

    private static int iterations() {
        return (int)Math.ceil((double)ITEM_COUNT / ITEM_PER_PAGE);
    }

    @NotNull private final Random RAND;
    @NotNull private final ChoiceSelectorSwipeType SELECTOR;
    @NotNull private final Engine<?> ENGINE;
    @NotNull private final PlatformType PLATFORM;
    @NotNull private final List<ChoiceInputItem> ITEMS;
    @NotNull private final WebElement SCROLL_VIEW;
    @Nullable private String selected;
    private int currentIndex;

    {
        RAND = new Random();
        ENGINE = mock(Engine.class);
        PLATFORM = mock(PlatformType.class);
        SCROLL_VIEW = mock(WebElement.class);
        ITEMS = new LinkedList<>();
        SELECTOR = spy(this);
    }

    @BeforeMethod
    public void beforeMethod() {
        for (int i = 0; i < ITEM_COUNT; i++) {
            ChoiceInputItem item = new ChoiceInputItem(i);
            ITEMS.add(item);
            doReturn(item.ELEMENT.getText()).when(ENGINE).getText(eq(item.ELEMENT));
        }

        selected = String.valueOf(ITEMS.get(RAND.nextInt(ITEMS.size())).INDEX);
        doReturn(PLATFORM).when(ENGINE).platform();
        doReturn(Flowable.just(true)).when(ENGINE).rx_swipeOnce(any());
        doReturn(selected).when(SELECTOR).selectedChoice();

    }

    public void afterMethod() {
        ITEMS.clear();
        reset(SELECTOR, ENGINE);
    }

    @NotNull
    public Flowable<Boolean> rx_swipeElement(@NotNull WebElement element,
                                             @NotNull Unidirection direction,
                                             double scrollRatio) {
        return Flowable.just(true);
    }

    @NotNull
    @Override
    public Flowable<WebElement> rx_scrollableViewToSwipe() {
        return Flowable.just(SCROLL_VIEW);
    }

    @NotNull
    @Override
    public Flowable<WebElement> rx_scrollViewChildItems() {
        List<ChoiceInputItem> items = currentItemRange();
        return Flowable.fromIterable(items).map(a -> a.ELEMENT);
    }

    @NotNull
    @Override
    public Flowable<WebElement> rx_targetChoiceItem() {
        List<ChoiceInputItem> items = currentItemRange();
        final String SELECTED = selectedChoice();

        Optional<ChoiceInputItem> item = items.stream()
            .filter(a -> a.stringChoice().equals(SELECTED))
            .findFirst();

        if (item.isPresent()) {
            return Flowable.just(item.get().ELEMENT);
        } else {
            return Flowable.empty();
        }
    }

    @NotNull
    @Override
    public Flowable<Boolean> rx_swipeRecursively() {
        currentIndex += 1;
        return ChoiceSelectorSwipeType.super.rx_swipeRecursively();
    }

    @NotNull
    @Override
    public Flowable<Boolean> rx_initialSwipes(@NotNull WebElement element,
                                              @NotNull Unidirection direction,
                                              int times,
                                              int currentIndex) {
        this.currentIndex += 1;

        return ChoiceSelectorSwipeType.super.rx_initialSwipes(
            element,
            direction,
            times,
            currentIndex
        );
    }

    @NotNull
    @Override
    public Engine<?> engine() {
        return ENGINE;
    }

    @NotNull
    @Override
    @SuppressWarnings("ConstantConditions")
    public String selectedChoice() {
        return selected;
    }

    @NotNull
    @Override
    @SuppressWarnings("ConstantConditions")
    public ChoiceInputType choiceInput() {
        return ChoiceInput.INPUT1;
    }

    @NotNull
    private List<ChoiceInputItem> currentItemRange() {
        int lower = currentIndex * ITEM_PER_PAGE;
        int upper = (currentIndex + 1) * ITEM_PER_PAGE;
        return CollectionUtil.subList(ITEMS, lower, upper);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_selectChoice_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        SELECTOR.rx_repeatSwipe().subscribe(subscriber);

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
    }

    private enum ChoiceInput implements ChoiceInputType {
        INPUT1;

        @NotNull
        @Override
        public XPath choicePickerScrollViewXPath(@NotNull PlatformType platform) {
            return CHOICE_LIST_VIEW_XPATH;
        }

        @NotNull
        @Override
        public XPath choicePickerScrollViewItemXPath(@NotNull PlatformType platform) {
            return XPath.builder(platform).build();
        }

        @NotNull
        @Override
        public XPath targetChoiceItemXPath(@NotNull PlatformType platform,
                                           @NotNull String selected) {
            return XPath.builder(platform).build();
        }
    }

    private static final class ChoiceInputItem {
        @NotNull final WebElement ELEMENT;
        final int INDEX;

        ChoiceInputItem(int index) {
            INDEX = index;
            ELEMENT = mock(WebElement.class);
            doReturn(String.valueOf(index)).when(ELEMENT).getText();
        }

        @NotNull
        String stringChoice() {
            return String.valueOf(INDEX);
        }
    }
}
