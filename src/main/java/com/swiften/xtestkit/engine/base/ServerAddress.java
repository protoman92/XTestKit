package com.swiften.xtestkit.engine.base;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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

        boolean isLocalInstance() {
            switch (this) {
                case LOCAL:
                    return true;

                default:
                    return false;
            }
        }
    }

    @NotNull
    public static Builder newBuilder() {
        return new Builder();
    }

    @NotNull public static final ServerAddress DEFAULT;
    @NotNull private static final List<Integer> USED_PORTS;
    @NotNull public static final String LOCAL_URI_FORMAT;
    public static final int BASE_PORT;

    static {
        USED_PORTS = new ArrayList<>();
        LOCAL_URI_FORMAT = "http://localhost:%d/wd/hub";
        BASE_PORT = 4723;
        DEFAULT = new ServerAddress();
    }

    @NotNull private Mode mode;
    @NotNull private String uri;
    private int port;

    ServerAddress() {
        mode = Mode.LOCAL;
        uri = "";
        port = BASE_PORT;
    }

    /**
     * Get a default uri based on {@link #LOCAL_URI_FORMAT}.
     * @param port An {@link Integer} value.
     * @return A {@link String} value.
     */
    @NotNull
    public String defaultLocalUri(int port) {
        return String.format(LOCAL_URI_FORMAT, port);
    }

    /**
     * Return {@link #mode}.
     * @return A {@link Mode} instance.
     */
    @NotNull
    public Mode mode() {
        return mode;
    }

    /**
     * Return {@link #uri}.
     * @return A {@link String} value.
     */
    @NotNull
    public synchronized String uri() {
        if (uri.isEmpty()) {
            return defaultLocalUri(port());
        }

        return uri;
    }

    /**
     * Return {@link #port).
     * @return An {@link Integer} value.
     */
    public synchronized int port() {
        return port;
    }

    /**
     * Check whether Appium should be started locally.
     * @return A {@link Boolean} value.
     */
    public boolean isLocalInstance() {
        return mode().isLocalInstance();
    }

    /**
     * Return {@link #port}. If it is not available, return a default port
     * for local environment.
     * @return An {@link Integer} value.
     */
    public synchronized int newPort() {
        List<Integer> usedPorts = USED_PORTS;

        if (port == 0 || usedPorts.contains(port)) {
            if (usedPorts.isEmpty()) {
                port = BASE_PORT;
            } else {
                port = usedPorts.get(usedPorts.size() - 1) + 1;
            }
        }

        if (!usedPorts.contains(port)) {
            usedPorts.add(port);
        }

        return port;
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
