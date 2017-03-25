package com.swiften.xtestkit.localizer;

import com.swiften.localizer.Localizer;
import com.swiften.localizer.protocol.LocalizeErrorProtocol;
import com.swiften.util.Log;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created by haipham on 3/26/17.
 */
public class LocalizerTest implements LocalizeErrorProtocol {
    @NotNull private final Localizer LOCALIZER;
    @NotNull private final String[] TEST_STRINGS;

    private int BUNDLE_COUNT = 3;

    {
        LOCALIZER = spy(Localizer.newBuilder().build());

        TEST_STRINGS = new String[] {
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
    public void mock_localizeWithNoResult_shouldThrow() {
        // Setup
        doReturn("").when(LOCALIZER).getString(any(), anyString());
        TestSubscriber subscriber = TestSubscriber.create();

        // When
        Flowable.fromArray(TEST_STRINGS)
            .flatMap(a -> LOCALIZER
                .rxLocalize(a)
                .doOnNext(b -> assertEquals(a, b)))
            .subscribe(subscriber);

        // Then
        subscriber.assertSubscribed();
        subscriber.assertNoErrors();
        subscriber.assertComplete();
        verify(LOCALIZER);
    }
}
