package com.swiften.xtestkit.localizer.actual;

import com.swiften.localizer.Localizer;
import com.swiften.util.Log;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.Locale;

import static org.mockito.Mockito.spy;

/**
 * Created by haipham on 3/26/17.
 */
public class LocalizerTest {
    @NotNull private final Localizer LOCALIZER;
    @NotNull private final String[] STRINGS;

    private int BUNDLE_COUNT = 3;

    {
        LOCALIZER = spy(Localizer.newBuilder()
            .addBundleName("Strings", Locale.US)
            .build());

        STRINGS = new String[] {
            "auth_title_email",
            "auth_title_password",
            "auth_title_signInOrRegister",
        };
    }

    @Test
    @SuppressWarnings("unchecked")
    public void actual_rxLocalizeText_shouldSucceed() {
        // Setup
        TestSubscriber subscriber = TestSubscriber.create();

        // When
        Flowable.fromArray(STRINGS)
            .flatMap(LOCALIZER::rxLocalize)
            .subscribe(subscriber);

        // Then
        subscriber.assertSubscribed();
    }
}
