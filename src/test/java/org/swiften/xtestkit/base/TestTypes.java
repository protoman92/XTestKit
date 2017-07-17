package org.swiften.xtestkit.base;

import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.localizer.Localizer;
import org.swiften.javautilities.localizer.LocalizerProviderType;
import org.swiften.javautilities.localizer.LocalizerType;
import org.swiften.javautilities.protocol.RetryProviderType;
import org.swiften.xtestkit.base.element.choice.ChoiceSelectorType;
import org.swiften.xtestkit.base.element.choice.ChoiceType;
import org.swiften.xtestkit.base.element.date.CalendarUnit;
import org.swiften.xtestkit.base.element.date.DateActionType;
import org.swiften.xtestkit.base.element.date.DateProviderType;
import org.swiften.xtestkit.base.element.input.InputActionType;
import org.swiften.xtestkit.base.element.input.KeyboardActionType;
import org.swiften.xtestkit.base.element.locator.LocatorType;
import org.swiften.xtestkit.base.element.password.PasswordActionType;
import org.swiften.xtestkit.base.element.search.SearchActionType;
import org.swiften.xtestkit.base.element.swipe.SwipeOnceActionType;
import org.swiften.xtestkit.base.element.swipe.SwipeParamType;
import org.swiften.xtestkit.base.element.switcher.SwitcherActionType;
import org.swiften.xtestkit.base.element.tap.TapParamType;
import org.swiften.xtestkit.base.element.tap.TapType;
import org.swiften.xtestkit.base.type.PlatformViewProviderType;
import org.swiften.xtestkitcomponents.platform.PlatformProviderType;
import org.swiften.xtestkitcomponents.platform.PlatformType;

import static org.mockito.Mockito.mock;

/**
 * Created by haipham on 22/6/17.
 */
public final class TestTypes {
    public interface TestLocalizerProviderType extends LocalizerProviderType {
        @NotNull
        @Override
        default LocalizerType localizer() {
            return Localizer.builder().build();
        }
    }

    public interface TestPlatformViewProviderType extends PlatformViewProviderType {
        @NotNull
        @Override
        default PlatformView platformView() {
            return mock(PlatformView.class);
        }
    }

    public interface TestLocatorType extends
        LocatorType<WebDriver>,
        TestLocalizerProviderType,
        TestPlatformViewProviderType,
        TestPlatformProviderType
    {
        @NotNull
        @Override
        default Flowable<WebElement> rxe_statusBar() {
            throw new RuntimeException(NO_SUCH_ELEMENT);
        }

        @NotNull
        @Override
        default Flowable<WebElement> rxe_imageViews() {
            throw new RuntimeException(NOT_AVAILABLE);
        }

        @NotNull
        @Override
        default Flowable<WebElement> rxe_window() {
            throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    public interface TestDateActionType extends DateActionType<WebDriver> {
        @NotNull
        @Override
        default Flowable<Boolean> rxa_openPicker(@NotNull DateProviderType param,
                                                 @NotNull CalendarUnit unit) {
            throw new RuntimeException(NOT_AVAILABLE);
        }

        @NotNull
        @Override
        default Flowable<Boolean> rxa_select(@NotNull DateProviderType param,
                                             @NotNull CalendarUnit unit) {
            throw new RuntimeException(NOT_AVAILABLE);
        }

        @NotNull
        @Override
        default Flowable<Integer> rxe_displayedUnit(@NotNull DateProviderType param,
                                                    @NotNull CalendarUnit unit) {
            throw new RuntimeException(NOT_AVAILABLE);
        }

        @NotNull
        @Override
        default Flowable<WebElement> rxe_elementLabel(@NotNull DateProviderType param,
                                                      @NotNull CalendarUnit unit) {
            throw new RuntimeException(NOT_AVAILABLE);
        }

        @NotNull
        @Override
        default String valueString(@NotNull DateProviderType param, @NotNull CalendarUnit unit) {
            throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    public interface TestPlatformProviderType extends PlatformProviderType {
        @NotNull
        @Override
        default PlatformType platform() {
            throw new RuntimeException(NOT_AVAILABLE);
        }

        @NotNull
        @Override
        default String platformName() {
            throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    public interface TestChoiceSelectorType extends ChoiceSelectorType<WebDriver> {
        @NotNull
        @Override
        default Flowable<Boolean> rxa_selectGeneralChoice(@NotNull ChoiceType param) {
            throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    public interface TestInputActionType extends InputActionType<WebDriver> {
        @Override
        default void toggleNextInput(@NotNull WebElement element) {
            throw new RuntimeException(NOT_AVAILABLE);
        }

        @Override
        default void finishInput(@NotNull WebElement element) {
            throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    public interface TestKeyboardActionType extends KeyboardActionType<WebDriver> {
        @Override
        default void hideKeyboard() {
            throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    public interface TestPasswordActionType extends PasswordActionType<WebDriver> {
        @Override
        default void togglePasswordMask(@NotNull WebElement element) {
            throw new RuntimeException();
        }
    }

    public interface TestSearchActionType extends SearchActionType<WebDriver> {
        @NotNull
        @Override
        default Flowable<WebElement> rxe_textClear() {
            throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    public interface TestSwitcherActionType extends SwitcherActionType {
        @NotNull
        @Override
        default String switcherOnValue() {
            return "1";
        }

        @NotNull
        @Override
        default String switcherOffValue() {
            return "0";
        }

        @NotNull
        @Override
        default String switcherValue(@NotNull WebElement element) {
            return element.getAttribute("value");
        }
    }

    public interface TestSwipeOnceType extends SwipeOnceActionType {
        @Override
        default void swipeOnce(@NotNull SwipeParamType param) {}
    }

    public interface TestTapType extends TapType<WebDriver> {
        @Override
        default <P extends TapParamType & RetryProviderType> void tap(@NotNull P param) {}
    }
}
