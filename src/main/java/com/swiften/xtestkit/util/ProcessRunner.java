package com.swiften.xtestkit.util;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.jetbrains.annotations.NotNull;

import java.io.*;

/**
 * Created by haipham on 3/22/17.
 */
public class ProcessRunner {
    @NotNull
    public static Builder newBuilder() {
        return new Builder();
    }

    /**
     * Execute a command using the command line.
     * @param args The command line arguments to run.
     * @return The output of the operation.
     * @throws IOException If the operation fails, an {@link IOException} will
     * be thrown.
     */
    @NotNull
    public String execute(@NotNull String args) throws IOException {
        Log.println(String.format("Executing command '%s'", args));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CommandLine commandLine = CommandLine.parse(args);
        DefaultExecutor executor = new DefaultExecutor();

        /* Capture the output in a separate outputStream. Error messages
         * will also be redirected there */
        PumpStreamHandler handler = new PumpStreamHandler(outputStream);
        executor.setStreamHandler(handler);

        try {
            executor.execute(commandLine);
            return outputStream.toString();
        } catch (IOException e) {
            String error = outputStream.toString();
            throw new IOException(error);
        }
    }

    /**
     * Execute a command using the command line and wrap the result in a
     * {@link Flowable}.
     * @param ARGS The command line arguments to run.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<String> rxExecute(@NotNull final String ARGS) {
        return Flowable.create(observer -> {
            try {
                String output = execute(ARGS);
                observer.onNext(output);
                observer.onComplete();
            } catch (IOException e) {
                observer.onError(e);
            }
        }, BackpressureStrategy.BUFFER);
    }

    public static final class Builder {
        @NotNull private final ProcessRunner INSTANCE;

        Builder() {
            INSTANCE = new ProcessRunner();
        }

        @NotNull
        public ProcessRunner build() {
            return INSTANCE;
        }
    }
}
