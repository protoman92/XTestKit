package org.swiften.xtestkit.android;

import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.collection.CollectionUtil;
import org.swiften.javautilities.log.LogUtil;
import org.swiften.javautilities.rx.CustomTestSubscriber;
import org.swiften.xtestkit.base.Engine;
import org.swiften.xtestkit.base.element.action.general.Unidirection;
import org.swiften.xtestkit.base.element.locator.general.xpath.XPath;
import org.swiften.xtestkit.base.type.PlatformType;
import org.swiften.xtestkit.mobile.Platform;
import org.swiften.xtestkit.model.AndroidChoiceInputType;
import org.swiften.xtestkit.android.element.action.choice.AndroidChoiceSwipeSelectorType;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

/**
 * Created by haipham on 5/24/17.
 */
public class AndroidChoiceSwipeSelectorTest implements AndroidChoiceSwipeSelectorType {
    private static final int ITEM_COUNT = 100;
    private static final int ITEM_PER_PAGE = 15;

    @NotNull private static final XPath CHOICE_LIST_VIEW_XPATH;

    static {
        CHOICE_LIST_VIEW_XPATH = spy(XPath.class);
    }

    @NotNull private final Random RAND;
    @NotNull private final AndroidChoiceSwipeSelectorType SELECTOR;
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
        doReturn(Flowable.just(true)).when(ENGINE).rxa_click(any());
        doReturn(selected).when(SELECTOR).selectedChoice();

    }

    @AfterMethod
    public void afterMethod() {
        ITEMS.clear();
        reset(SELECTOR, ENGINE);
    }

    @NotNull
    @DataProvider(parallel = false)
    public Iterator<Object[]> dataProvider() {
        List<Object[]> data = new LinkedList<>();

        for (int i = 0; i < 100; i++) {
            data.add(new Object[0]);
        }

        return data.iterator();
    }

    @NotNull
    @Override
    public Unidirection firstElementDirection() {
        LogUtil.println("First direction");
        currentIndex -= 1;
        return AndroidChoiceSwipeSelectorType.super.firstElementDirection();
    }

    @NotNull
    @Override
    public Unidirection lastElementDirection() {
        LogUtil.println("Last direction");
        currentIndex += 1;
        return AndroidChoiceSwipeSelectorType.super.lastElementDirection();
    }

    @NotNull
    @Override
    public Flowable<?> rx_onTargetItemLocated(@NotNull WebElement element) {
        assertEquals(element.getText(), selectedChoice());
        return Flowable.just(true);
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

        return item.map(a -> Flowable.just(a.ELEMENT)).orElseGet(Flowable::empty);
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
    public AndroidChoiceInputType choiceInput() {
        return ChoiceInput.INPUT1;
    }

    @NotNull
    private List<ChoiceInputItem> currentItemRange() {
        int lower = currentIndex * ITEM_PER_PAGE;
        int upper = (currentIndex + 1) * ITEM_PER_PAGE;
        return CollectionUtil.subList(ITEMS, lower, upper);
    }

    @SuppressWarnings("unchecked")
    @Test(dataProvider = "dataProvider")
    public void test_selectChoice_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();
        LogUtil.printf("Searching for %s", selectedChoice());

        // When
        SELECTOR.rx_execute().subscribe(subscriber);

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
    }

    private enum ChoiceInput implements AndroidChoiceInputType {
        INPUT1;

        @NotNull
        @Override
        public XPath choicePickerXPath(@NotNull PlatformType platform) {
            throw new RuntimeException(NOT_AVAILABLE);
        }

        @NotNull
        @Override
        public XPath choicePickerItemXPath(@NotNull PlatformType platform) {
            throw new RuntimeException(NOT_AVAILABLE);
        }

        @NotNull
        @Override
        public XPath androidChoicePickerXPath() {
            return CHOICE_LIST_VIEW_XPATH;
        }

        @NotNull
        @Override
        public XPath androidChoicePickerItemXPath() {
            return XPath.builder(Platform.ANDROID).build();
        }

        @NotNull
        @Override
        public XPath androidTargetChoiceItemXPath(@NotNull String selected) {
            return XPath.builder(Platform.ANDROID).build();
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
