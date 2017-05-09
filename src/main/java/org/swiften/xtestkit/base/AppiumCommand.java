package org.swiften.xtestkit.base;

import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 4/6/17.
 */
public class AppiumCommand {
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @NotNull private String command;

    AppiumCommand() {
        command = "";
    }

    /**
     * Return {@link #command}.
     * @return A {@link String} value.
     */
    @NotNull
    public String command() {
        return command;
    }

    /**
     * Set the base command i.e. path to Appium CLI.
     * @param base A {@link String} value.
     */
    void prependBase(@NotNull String base) {
        if (command.isEmpty()) {
            command = base;
        } else {
            command = String.format("%1$s %2$s", base, command);
        }
    }

    /**
     * Append a flag value to the existing {@link #command}.
     * @param flag The flag element to be appended.
     * @param value The flag value to be appended.
     */
    void appendFlag(@NotNull String flag, @NotNull Object value) {
        String vString = String.valueOf(value);

        if (command.isEmpty()) {
            command = String.format("-%1$s %2$s", false, vString);
        } else {
            command = String.format("%1$s -%2$s %3$s", command, flag, vString);
        }
    }

    public static class Builder {
        @NotNull private final AppiumCommand COMMAND;

        Builder() {
            COMMAND = new AppiumCommand();
        }

        /**
         * Append -p PORT to {@link #COMMAND#command}.
         * @param port An {@link Integer} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withPort(int port) {
            COMMAND.appendFlag("p", port);
            return this;
        }

        /**
         * Prepend a base path to {@link #COMMAND#command}.
         * @param base A {@link String} value.
         * @return The current {@link Builder} instance.
         */
        public Builder withBase(@NotNull String base) {
            COMMAND.prependBase(base);
            return this;
        }

        @NotNull
        public AppiumCommand build() {
            return COMMAND;
        }
    }
}
