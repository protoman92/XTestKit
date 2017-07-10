package org.swiften.xtestkit.mobile;

import io.appium.java_client.remote.MobilePlatform;
import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.collection.CollectionUtil;
import org.swiften.xtestkitcomponents.common.ErrorProviderType;
import org.swiften.xtestkitcomponents.platform.PlatformType;
import org.swiften.xtestkitcomponents.property.base.ValueType;

import java.util.*;

/**
 * Created by haipham on 3/20/17.
 */
public enum Platform implements ErrorProviderType, PlatformType, ValueType<String> {
    ANDROID,
    IOS;

    /**
     * Get {@link Platform} from {@link String} value.
     * @param value {@link String} value.
     * @return {@link Platform} instance.
     * @see #NOT_AVAILABLE
     */
    @NotNull
    public static Platform fromValue(@NotNull String value) {
        Optional<Platform> ops = Arrays.stream(values())
            .filter(a -> a.value().equalsIgnoreCase(value))
            .findFirst();

        if (ops.isPresent()) {
            return ops.get();
        } else {
            throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link String} value.
     * @see PlatformType#value()
     * @see MobilePlatform#ANDROID
     * @see MobilePlatform#IOS
     * @see #ANDROID
     * @see #IOS
     * @see #NOT_AVAILABLE
     */
    @NotNull
    @Override
    public String value() {
        switch (this) {
            case ANDROID:
                return MobilePlatform.ANDROID;

            case IOS:
                return MobilePlatform.IOS;

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Collection} of {@link String}.
     * @see PlatformType#classAttribute()
     */
    @NotNull
    @Override
    public Collection<String> classAttribute() {
        Collection<String> clsAttributes = PlatformType.super.classAttribute();

        switch (this) {
            case IOS:
                List<String> attrs = new ArrayList<>();
                attrs.addAll(clsAttributes);
                attrs.add("type");
                return attrs;

            default:
                return clsAttributes;
        }
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Collection} of {@link String}.
     * @see PlatformType#idAttribute()
     * @see CollectionUtil#asList(Object[])
     * @see #ANDROID
     * @see #IOS
     * @see #NOT_AVAILABLE
     */
    @NotNull
    @Override
    public Collection<String> idAttribute() {
        switch (this) {
            case ANDROID:
                return CollectionUtil.asList("resource-id");

            case IOS:
                return CollectionUtil.asList("name");

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Collection} of {@link String}.
     * @see PlatformType#textAttribute()
     * @see CollectionUtil#asList(Object[])
     * @see #ANDROID
     * @see #IOS
     * @see #NOT_AVAILABLE
     */
    @NotNull
    @Override
    public Collection<String> textAttribute() {
        switch (this) {
            case ANDROID:
                return CollectionUtil.asList("text");

            case IOS:
                return CollectionUtil.asList(
                    "title",
                    "text",
                    "value",
                    "label");

            default:
                throw new RuntimeException(NOT_AVAILABLE);
        }
    }
}
