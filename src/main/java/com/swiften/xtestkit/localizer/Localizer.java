package com.swiften.xtestkit.localizer;

import com.swiften.xtestkit.localizer.protocol.LocalizeErrorProtocol;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Created by haipham on 3/25/17.
 */
public class Localizer implements LocalizeErrorProtocol {
    @NotNull
    public static Builder newBuilder() {
        return new Builder();
    }

    @NotNull private final List<ResourceBundle> BUNDLES;
    @NotNull private final List<Locale> LOCALES;

    Localizer() {
        BUNDLES = new ArrayList<>();
        LOCALES = new ArrayList<>();
    }

    /**
     * Get {@link #BUNDLES}.
     * @return A {@link List} of {@link ResourceBundle}.
     */
    @NotNull
    public List<ResourceBundle> bundles() {
        return BUNDLES;
    }

    /**
     * Get {@link #LOCALES}.
     * @return A {@link List} of {@link Locale}.
     */
    @NotNull
    public List<Locale> locales() {
        return LOCALES;
    }

    /**
     * Localize a text with the specified {@link #BUNDLES} and
     * {@link #LOCALES}.
     * @param TEXT The {@link String} to be localized.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<String> rxLocalize(@NotNull final String TEXT) {
        final List<ResourceBundle> BUNDLES = bundles();
        final List<Locale> LOCALES = locales();
        final int LENGTH = LOCALES.size();

        if (LENGTH > 0) {
            class Localize {
                @NotNull
                @SuppressWarnings("WeakerAccess")
                Flowable<String> localize(final int INDEX) {
                    if (INDEX < LENGTH) {
                        Locale.setDefault(LOCALES.get(INDEX));

                        return Flowable
                            .fromIterable(BUNDLES)
                            .map(a -> getString(a, TEXT))
                            .filter(a -> Objects.nonNull(a) && !a.isEmpty())
                            .firstElement()
                            .toFlowable()
                            .switchIfEmpty(new Localize().localize(INDEX + 1));
                    }

                    return Flowable.empty();
                }
            }

            return new Localize()
                .localize(0)
                .filter(a -> Objects.nonNull(a) && !a.isEmpty())
                .defaultIfEmpty(TEXT);
        } else {
            return Flowable.just(TEXT);
        }
    }

    /**
     * Same as above, but blocks.
     * @param text The {@link String} to be localized.
     * @return A {@link String} value.
     */
    @NotNull
    public String localize(@NotNull String text) {
        String result = rxLocalize(text).blockingSingle();

        if (result != null && !result.isEmpty()) {
            return result;
        } else {
            return text;
        }
    }

    @NotNull
    public String getString(@NotNull ResourceBundle bundle,
                            @NotNull String text) {
        return bundle.getString(text);
    }

    public static final class Builder {
        @NotNull private final Localizer LOCALIZER;

        Builder() {
            LOCALIZER = new Localizer();
        }

        /**
         * Add a {@link ResourceBundle} to {@link #LOCALIZER#BUNDLES}.
         * @param name The name of the {@link ResourceBundle}.
         * @param locale The {@link Locale} of the {@link ResourceBundle}.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder addBundleName(@NotNull String name,
                                     @NotNull Locale locale) {
            ResourceBundle bundle = ResourceBundle.getBundle(name, locale);

            if (Objects.nonNull(bundle)) {
                LOCALIZER.BUNDLES.add(bundle);
                LOCALIZER.LOCALES.add(locale);
            }

            return this;
        }

        @NotNull
        public Localizer build() {
            return LOCALIZER;
        }
    }
}
