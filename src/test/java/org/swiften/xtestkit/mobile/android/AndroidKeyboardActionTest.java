package org.swiften.xtestkit.mobile.android;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.rx.CustomTestSubscriber;
import org.swiften.xtestkit.base.param.NavigateBack;
import org.swiften.xtestkit.mobile.android.adb.ADBHandler;
import org.swiften.xtestkit.mobile.android.element.action.input.type.AndroidKeyboardActionType;
import org.swiften.xtestkit.system.ProcessRunner;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by haipham on 5/15/17.
 */
public class AndroidKeyboardActionTest implements AndroidKeyboardActionType {
    @NotNull private final AndroidKeyboardActionType ENGINE;
    @NotNull private final AndroidInstance ANDROID_INSTANCE;
    @NotNull private final ADBHandler ADB_HANDLER;
    @NotNull private final ProcessRunner PROCESS_RUNNER;

    {
        /* We return this androidInstance when calling
         * ENGINE.androidInstance() */
        ANDROID_INSTANCE = mock(AndroidInstance.class);

        /* We return this adbHandler when calling ENGINE.adbHandler() */
        ADB_HANDLER = spy(ADBHandler.builder().build());

        /* We spy this class to check for method calls */
        PROCESS_RUNNER = spy(ProcessRunner.builder().build());

        ENGINE = spy(this);
    }

    @NotNull
    @Override
    public AndroidInstance androidInstance() {
        return ANDROID_INSTANCE;
    }

    @NotNull
    @Override
    public ADBHandler adbHandler() {
        return ADB_HANDLER;
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public AndroidDriver<AndroidElement> driver() {
        return mock(AndroidDriver.class);
    }

    @BeforeMethod
    public void beforeMethod() {
        doReturn(ANDROID_INSTANCE).when(ENGINE).androidInstance();
        doReturn(ADB_HANDLER).when(ENGINE).adbHandler();

        /* Shorten the delay for testing */
        doReturn(100L).when(ADB_HANDLER).emulatorBootRetryDelay();
    }

    @AfterMethod
    public void afterMethod() {
        reset(ENGINE, ADB_HANDLER, PROCESS_RUNNER);
    }

    //region Dismiss Keyboard
    @Test
    @SuppressWarnings("unchecked")
    public void test_dismissHiddenKeyboard_shouldDoNothing() {
        // Setup
        doReturn(Flowable.just(false)).when(ADB_HANDLER).rxCheckKeyboardOpen(any());
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ENGINE.rxHideKeyboard().subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        verify(ADB_HANDLER).rxCheckKeyboardOpen(any());
        verify(ENGINE).adbHandler();
        verify(ENGINE).androidInstance();
        verify(ENGINE).rxHideKeyboard();
        verify(ENGINE, never()).rxNavigateBack(any(NavigateBack.class));
        verifyNoMoreInteractions(ENGINE);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_dismissKeyboard_shouldSucceed() {
        // Setup
        doReturn(Flowable.just(true)).when(ADB_HANDLER).rxCheckKeyboardOpen(any());
        doReturn(Flowable.just(true)).when(ENGINE).rxNavigateBackOnce();
        TestSubscriber subscriber = CustomTestSubscriber.create();

        // When
        ENGINE.rxHideKeyboard().subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        verify(ADB_HANDLER).rxCheckKeyboardOpen(any());
        verify(ENGINE).adbHandler();
        verify(ENGINE).androidInstance();
        verify(ENGINE).rxNavigateBackOnce();
        verify(ENGINE).rxHideKeyboard();
        verifyNoMoreInteractions(ENGINE);
    }
    //endregion
}
