package com.swiften.xtestkit.localizer.mock;

import com.swiften.xtestkit.localizer.Localizer;
import com.swiften.xtestkit.localizer.protocol.LocalizeErrorProtocol;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created by haipham on 3/26/17.
 */
public class LocalizerTest implements LocalizeErrorProtocol {
    @NotNull private final Localizer LOCALIZER;
    @NotNull private final String[] STRINGS;

    private int BUNDLE_COUNT = 3;

    {
        LOCALIZER = spy(Localizer.newBuilder().build());

        STRINGS = new String[] {
            "helloWorld",
            "goodbyeWorld"
        };
    }

    @Before
    public void before() {
        ResourceBundle bundle = mock(ResourceBundle.class);
        Locale locale = Locale.US;
        List<ResourceBundle> bundles = new ArrayList<>();
        List<Locale> locales = new ArrayList<>();

        for (int i = 0, count = BUNDLE_COUNT; i < count; i++) {
            bundles.add(bundle);
            locales.add(locale);
        }

        doReturn(bundles).when(LOCALIZER).bundles();
        doReturn(locales).when(LOCALIZER).locales();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mock_rxLocalizeWithNoResult_shouldEmitOriginal() {
        // Setup
        int times = (int)(Math.pow(BUNDLE_COUNT, 2) * STRINGS.length);
        doReturn("").when(LOCALIZER).getString(any(), anyString());
        TestSubscriber subscriber = TestSubscriber.create();

        // When
        Flowable.fromArray(STRINGS)
            .flatMap(a -> LOCALIZER
                .rxLocalize(a)
                .doOnNext(b -> assertEquals(a, b)))
            .subscribe(subscriber);

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        verify(LOCALIZER, times(times)).getString(any(), anyString());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mock_rxLocalizeWithResult_shouldEmitImmediately() {
        // Setup
        final String CORRECT = "Correct Result";
        doReturn(CORRECT).when(LOCALIZER).getString(any(), anyString());
        TestSubscriber subscriber = TestSubscriber.create();

        // When
        Flowable.fromArray(STRINGS)
            .flatMap(a -> LOCALIZER
                .rxLocalize(a)
                .doOnNext(b -> assertEquals(b, CORRECT)))
            .subscribe(subscriber);

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        verify(LOCALIZER, times(STRINGS.length)).getString(any(), anyString());
    }

    @Test
    public void mock_localizeWithNoResult_shouldReturnOriginal() {
        // Setup
        int times = (int)(Math.pow(BUNDLE_COUNT, 2) * STRINGS.length);
        doReturn("").when(LOCALIZER).getString(any(), anyString());

        // When
        Arrays.stream(STRINGS)
            .map(LOCALIZER::localize)
            .forEach(a -> {
                assertTrue(Arrays.asList(STRINGS).contains(a));
            });

        // Then
        verify(LOCALIZER, times(times)).getString(any(), anyString());
    }

    @Test
    public void mock_localizeWithResult_shouldReturnImmediately() {
        // Setup
        final String CORRECT = "Correct Result";
        doReturn(CORRECT).when(LOCALIZER).getString(any(), anyString());

        // When
        Arrays.stream(STRINGS)
            .map(LOCALIZER::localize)
            .forEach(a -> {
                assertEquals(a, CORRECT);
            });

        // Then
        verify(LOCALIZER, times(STRINGS.length)).getString(any(), anyString());
    }
}
