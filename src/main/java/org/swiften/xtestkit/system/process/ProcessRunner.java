package org.swiften.xtestkit.system.process;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.log.LogUtil;

import java.io.*;
import java.util.Objects;

/**
 * Created by haipham on 3/22/17.
 */
public class ProcessRunner {
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Execute a command using the command line.
     * @param args The command line arguments to run.
     * @return The output of the operation.
     * @throws IOException If the operation fails, {@link IOException} will
     * be thrown.
     */
    @NotNull
    public String execute(@NotNull String args) throws IOException {
        LogUtil.printfThread("Executing '%s'", args);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CommandLine commandLine = CommandLine.parse(args);
        DefaultExecutor executor = new DefaultExecutor();

        /* Capture the output in a separate outputStream. Error messages
         * will also be redirected there */
        PumpStreamHandler handler = new PumpStreamHandler(outputStream);
        executor.setStreamHandler(handler);

        try {
            executor.execute(commandLine);
            String output = outputStream.toString();
            LogUtil.printfThread("Successfully executed '%s'", args);
            return Objects.nonNull(output) ? output : "";
        } catch (IOException e) {
            String error = outputStream.toString();

            LogUtil.printfThread(
                "Execution error while running '%1$s': '%2$s', output: %3$s",
                args, e.getMessage(), error);

            throw new IOException(error);
        }
    }

    /**
     * Execute a command using the command line and wrap the result in a
     * {@link Flowable}.
     * @param ARGS The command line arguments to run.
     * @return {@link Flowable} instance.
     */
    @NotNull
    public Flowable<String> rxExecute(@NotNull final String ARGS) {
        return Flowable
            .<String>create(observer -> {
                try {
                    String output = execute(ARGS);
                    observer.onNext(output);
                    observer.onComplete();
                } catch (IOException e) {
                    observer.onError(e);
                }
            }, BackpressureStrategy.BUFFER)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io());
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
