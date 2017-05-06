package com.swiften.xtestkit.engine.base;

import com.swiften.xtestkit.system.PortProtocol;
import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 4/5/17.
 */

/**
 * This class shall take care of Appium's server address. It provides some
 * convenience methods for when {@link Mode#LOCAL} is used.
 */
public class ServerAddress implements
    PortProtocol,
    RetryProtocol,
    ServerAddressError {
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
    public static Builder builder() {
        return new Builder();
    }

    public static ServerAddress localInstanceWithPort(int port) {
        return ServerAddress.builder().withMode(Mode.LOCAL).build();
    }

    @NotNull private static final String LOCAL_URI_FORMAT;

    private static final int BASE_PORT;

    static {
        LOCAL_URI_FORMAT = "http://localhost:%d/wd/hub";
        BASE_PORT = 4723;
    }

    /**
     * We should not have a static DEFAULT instance, because we will be
     * changing {@link ServerAddress#port} quite often. A singleton instance
     * will override necessary changes and produce bugs.
     * @return A {@link ServerAddress} instance.
     */
    @NotNull
    public static ServerAddress defaultInstance() {
        return new ServerAddress();
    }

    @NotNull private Mode mode;
    @NotNull private String uri;
    private int port;

    ServerAddress() {
        mode = Mode.LOCAL;
        uri = "";
        port = BASE_PORT;
    }

    @NotNull
    public String toString() {
        return uri();
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
    public String uri() {
        if (uri.isEmpty()) {
            return defaultLocalUri(port());
        }

        return uri;
    }

    /**
     * Return {@link #port).
     * @return An {@link Integer} value.
     */
    public int port() {
        return port;
    }

    /**
     * Set {@link #port}.
     * @param port An {@link Integer} value.
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Check whether Appium should be started locally.
     * @return A {@link Boolean} value.
     */
    public boolean isLocalInstance() {
        return mode().isLocalInstance();
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

        /**
         * Set the {@link #SERVER#port} value.
         * @param port An {@link Integer} value.
         * @return THe current {@link Builder} instance.
         */
        @NotNull
        public Builder withPort(int port) {
            SERVER.port = port;
            return this;
        }

        @NotNull
        public ServerAddress build() {
            return SERVER;
        }
    }
}
