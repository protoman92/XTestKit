package com.swiften.xtestkit.engine.base;

import com.swiften.xtestkit.util.Log;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by haipham on 4/5/17.
 */

/**
 * This class shall take care of Appium's server address. It provides some
 * convenience methods for when {@link Mode#LOCAL} is used.
 */
public class ServerAddress {
    public enum Mode {
        LOCAL;
    }

    @NotNull
    public static Builder newBuilder() {
        return new Builder();
    }

    @NotNull public static final ServerAddress DEFAULT;
    @NotNull private static final List<Integer> USED_PORTS;
    @NotNull private static final String LOCAL_URI_FORMAT;
    private static final int BASE_PORT;

    static {
        USED_PORTS = new ArrayList<>();
        LOCAL_URI_FORMAT = "http://localhost:%d/wd/hub";
        BASE_PORT = 4723;
        DEFAULT = new ServerAddress();
    }

    /**
     * Get a default uri based on {@link #LOCAL_URI_FORMAT}.
     * @param port An {@link Integer} value.
     * @return A {@link String} value.
     */
    @NotNull
    private static String defaultLocalUri(int port) {
        return String.format(LOCAL_URI_FORMAT, port);
    }

    /**
     * If {@link #USED_PORTS} is empty, use {@link #BASE_PORT}. Otherwise,
     * use the last used port, incremented by 1.
     * @return A {@link String} value.
     */
    @NotNull
    private static String defaultLocalUri() {
        List<Integer> usedPorts = USED_PORTS;

        if (usedPorts.isEmpty()) {
            int port = BASE_PORT;
            usedPorts.add(port);
            return defaultLocalUri(port);
        }

        int newPort = usedPorts.get(usedPorts.size() - 1) + 1;
        usedPorts.add(newPort);
        return defaultLocalUri(newPort);
    }

    @NotNull private Mode mode;
    @NotNull private String uri;

    ServerAddress() {
        mode = Mode.LOCAL;
        uri = "";
    }

    /**
     * Return {@link #uri}.
     * @return A {@link String} value.
     */
    @NotNull
    public String uri() {
        if (uri.isEmpty()) {
            return defaultLocalUri();
        }

        return uri;
    }

    public static final class Builder {
        @NotNull private final ServerAddress SERVER;

        Builder() {
            SERVER = new ServerAddress();
        }

        /**
         * Set the {@link #SERVER#mode} instance.
         * @param mode A {@link Mode} instance.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withMode(@NotNull Mode mode) {
            SERVER.mode = mode;
            return this;
        }

        /**
         * Set the {@link #SERVER#uri} value.
         * @param uri A {@link String} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withUri(@NotNull String uri) {
            SERVER.uri = uri;
            return this;
        }

        @NotNull
        public ServerAddress build() {
            return SERVER;
        }
    }
}
