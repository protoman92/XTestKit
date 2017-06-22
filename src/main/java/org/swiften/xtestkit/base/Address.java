package org.swiften.xtestkit.base;

import org.swiften.javautilities.protocol.RetryType;
import org.swiften.javautilities.util.Constants;
import org.swiften.xtestkitcomponents.system.network.type.MaxPortType;
import org.swiften.xtestkitcomponents.system.network.type.PortStepType;
import org.swiften.xtestkitcomponents.system.network.type.PortType;
import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 4/5/17.
 */

/**
 * This class shall take care of Appium's server address. It provides some
 * convenience methods for when {@link Mode#LOCAL} is used.
 */
public class Address implements PortType, MaxPortType, PortStepType, RetryType {
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

    /**
     * Get {@link Builder} instance.
     * @return {@link Builder} instance.
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @NotNull private static final String LOCAL_URI_FORMAT;

    private static final int BASE_PORT;

    static {
        LOCAL_URI_FORMAT = "http://localhost:%d/wd/hub";
        BASE_PORT = 4723;
    }

    /**
     * We should not have a static DEFAULT instance, because we will be
     * changing {@link Address#port} quite often. A singleton instance
     * will override necessary changes and produce bugs.
     * @return {@link Address} instance.
     */
    @NotNull
    public static Address defaultInstance() {
        return new Address();
    }

    @NotNull private Mode mode;
    @NotNull private String uri;
    private int port;

    Address() {
        mode = Mode.LOCAL;
        uri = "";
        port = BASE_PORT;
    }

    @NotNull
    public String toString() {
        return uri();
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Integer} value.
     * @see RetryType#retries()
     * @see Constants#DEFAULT_RETRIES
     */
    @Override
    public int retries() {
        return Constants.DEFAULT_RETRIES;
    }

    /**
     * Get a default uri based on {@link #LOCAL_URI_FORMAT}.
     * @param port {@link Integer} value.
     * @return {@link String} value.
     */
    @NotNull
    public String defaultLocalUri(int port) {
        return String.format(LOCAL_URI_FORMAT, port);
    }

    /**
     * Return {@link #mode}.
     * @return {@link Mode} instance.
     */
    @NotNull
    public Mode mode() {
        return mode;
    }

    /**
     * Return {@link #uri}.
     * @return {@link String} value.
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
     * @return {@link Integer} value.
     */
    public synchronized int port() {
        return port;
    }

    /**
     * Set {@link #port}.
     * @param port {@link Integer} value.
     */
    public synchronized void setPort(int port) {
        this.port = port;
    }

    /**
     * Check whether Appium should be started locally.
     * @return {@link Boolean} value.
     */
    public boolean isLocalInstance() {
        return mode().isLocalInstance();
    }

    public static final class Builder {
        @NotNull private final Address SERVER;

        Builder() {
            SERVER = new Address();
        }

        /**
         * Set the {@link #SERVER#mode} instance.
         * @param mode {@link Mode} instance.
         * @return {@link Builder} instance.
         */
        @NotNull
        public Builder withMode(@NotNull Mode mode) {
            SERVER.mode = mode;
            return this;
        }

        /**
         * Set the {@link #SERVER#uri} value.
         * @param uri {@link String} value.
         * @return {@link Builder} instance.
         */
        @NotNull
        public Builder withUri(@NotNull String uri) {
            SERVER.uri = uri;
            return this;
        }

        /**
         * Set the {@link #SERVER#port} value.
         * @param port {@link Integer} value.
         * @return {@link Builder} instance.
         */
        @NotNull
        public Builder withPort(int port) {
            SERVER.port = port;
            return this;
        }

        @NotNull
        public Address build() {
            return SERVER;
        }
    }
}
