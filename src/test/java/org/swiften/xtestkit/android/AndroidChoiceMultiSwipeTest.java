package org.swiften.xtestkit.android;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.collection.HPIterables;
import org.swiften.javautilities.rx.CustomTestSubscriber;
import org.swiften.javautilities.util.HPLog;
import org.swiften.xtestkit.android.element.choice.AndroidChoiceMultiSwipeType;
import org.swiften.xtestkit.android.model.AndroidChoiceInputType;
import org.swiften.xtestkit.base.Engine;
import org.swiften.xtestkit.base.model.InputHelperType;
import org.swiften.xtestkitcomponents.direction.Direction;
import org.swiften.xtestkitcomponents.platform.PlatformType;
import org.swiften.xtestkitcomponents.xpath.XPath;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.*;

import static org.mockito.Mockito.*;

/**
 * Created by haipham on 5/24/17.
 */
public class AndroidChoiceMultiSwipeTest implements AndroidChoiceMultiSwipeType {
    private static final int ITEM_COUNT = 100;
    private static final int ITEM_PER_PAGE = 15;

    @NotNull private static final XPath CHOICE_LIST_VIEW_XPATH;

    static {
        CHOICE_LIST_VIEW_XPATH = spy(XPath.class);
    }

    @NotNull private final Random RAND;
    @NotNull private final AndroidChoiceMultiSwipeType SELECTOR;
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
        doReturn(Flowable.just(true)).when(ENGINE).rxa_swipeOnce(any());
        doReturn(Flowable.just(true)).when(ENGINE).clickFn();
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
    public Direction firstElementDirection() {
        HPLog.println("First direction");
        currentIndex -= 1;
        return AndroidChoiceMultiSwipeType.super.firstElementDirection();
    }

    @NotNull
    @Override
    public Direction lastElementDirection() {
        HPLog.println("Last direction");
        currentIndex += 1;
        return AndroidChoiceMultiSwipeType.super.lastElementDirection();
    }

    @NotNull
    @Override
    public FlowableTransformer<WebElement, ?> targetItemLocatedFn() {
        return upstream -> upstream.cast(Object.class);
    }

    @NotNull
    public Flowable<Boolean> rxa_swipeElement(@NotNull WebElement element,
                                              @NotNull Direction direction,
                                              double scrollRatio) {
        return Flowable.just(true);
    }

    @NotNull
    @Override
    public Flowable<WebElement> rxe_scrollableViewToSwipe() {
        return Flowable.just(SCROLL_VIEW);
    }

    @NotNull
    @Override
    public Flowable<WebElement> rxe_scrollViewChildItems() {
        List<ChoiceInputItem> items = currentItemRange();
        return Flowable.fromIterable(items).map(a -> a.ELEMENT);
    }

    @NotNull
    @Override
    public Flowable<WebElement> rxe_targetChoiceItem() {
        List<ChoiceInputItem> items = currentItemRange();
        final String SELECTED = selectedChoice();

        Optional<ChoiceInputItem> item = items.stream()
            .filter(a -> a.stringChoice().equals(SELECTED))
            .findFirst();

        return item.map(a -> Flowable.just(a.ELEMENT)).orElseGet(Flowable::empty);
    }

    @NotNull
    @Override
    public Engine<?> choiceHelper() {
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
        return HPIterables.subList(ITEMS, lower, upper);
    }

    @SuppressWarnings("unchecked")
    @Test(dataProvider = "dataProvider")
    public void test_selectChoice_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = CustomTestSubscriber.create();
        HPLog.printf("Searching for %s", selectedChoice());

        // When
        SELECTOR.rxa_performAction().subscribe(subscriber);

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
    }

    private enum ChoiceInput implements AndroidChoiceInputType {
        INPUT1;

        @NotNull
        @Override
        public XPath choicePickerXP(@NotNull InputHelperType helper) {
            throw new RuntimeException(NOT_AVAILABLE);
        }

        @NotNull
        @Override
        public XPath choicePickerItemXP(@NotNull InputHelperType helper) {
            throw new RuntimeException(NOT_AVAILABLE);
        }

        @NotNull
        @Override
        public XPath androidChoicePickerXP(@NotNull InputHelperType helper) {
            return CHOICE_LIST_VIEW_XPATH;
        }

        @NotNull
        @Override
        public XPath androidChoicePickerItemXP(@NotNull InputHelperType helper) {
            return XPath.builder().build();
        }

        @NotNull
        @Override
        public XPath androidTargetItemXP(@NotNull InputHelperType helper,
                                         @NotNull String selected) {
            return XPath.builder().build();
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
