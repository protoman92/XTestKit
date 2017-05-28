package org.swiften.xtestkit.emitter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.swiften.javautilities.log.LogUtil;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Created by haipham on 5/28/17.
 */
@SuppressWarnings("UndeclaredTests")
public final class EmitterLoopTest {
    @NotNull private static final Random RAND;

    static {
        RAND = new Random();
    }

    @Test
    public void test_valueEmitter_shouldWork() {
        // Setup
        final Consumer<String> CS = LogUtil::println;
        List<Thread> threads = new LinkedList<>();

        // When & Then
        for (int i = 0; i < 1000; i++) {
            final String I = String.valueOf(i);

            threads.add(new Thread(() -> {
                new ValueListEmitterLoop<>(CS).emit(I);
            }));
        }

        threads.forEach(Thread::start);
    }

    static class ValueListEmitterLoop<T> {
        @NotNull final Consumer<? super T> CONSUMER;
        @Nullable List<T> queue;
        boolean emitting;

        ValueListEmitterLoop(@NotNull Consumer<? super T> consumer) {
            this.CONSUMER = consumer;
        }

        private void emit(@NotNull T value) {
            synchronized (this) {
                if (emitting) {
                    List<T> q = queue;

                    if (q == null) {
                        q = new ArrayList<T>();
                        queue = q;
                    }

                    q.add(value);
                    return;
                }

                emitting = true;
            }

            boolean skipFinal = false;

            try {
                CONSUMER.accept(value);

                for (;;) {
                    List<T> q;

                    synchronized (this) {
                        q = queue;

                        if (q == null) {
                            emitting = false;
                            skipFinal = true;
                            return;
                        }

                        queue = null;
                    }

                    q.forEach(CONSUMER);
                }
            } finally {
                if (!skipFinal) {
                    synchronized (this) {
                        emitting = false;
                    }
                }
            }
        }
    }
}
