package com.swiften.util;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * Created by haipham on 3/22/17.
 */
public class ProcessUtil {
    /**
     * Execute a command using the command line.
     * @param args The command line arguments.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public static Flowable<Boolean> executeCommand(@NotNull String...args) {
        final ProcessBuilder BUILDER = new ProcessBuilder(args);

        return Flowable
            .create(observer -> {
                try {
                    Process PROCESS = BUILDER.start();
                    observer.onNext(true);
                    observer.onComplete();
                } catch (IOException e) {
                    observer.onError(e);
                }
            }, BackpressureStrategy.BUFFER);
    }

    /**
     * Convert the contents of an {@link InputStream} to {@link String}.
     * {@link Scanner} iterates over tokens in the stream, and in this case
     * we separate tokens using "beginning of the input boundary" (\A).
     * @param stream The {@link InputStream} to be inspected.
     * @return A {@link String} value.
     */
    @NotNull
    static String inputStreamToString(@NotNull InputStream stream) {
        Scanner s = new Scanner(stream).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
