package org.swiften.xtestkit.scrap;

import org.mockito.Mockito;
import org.swiften.javautilities.log.LogUtil;
import org.testng.TestNG;
import org.testng.annotations.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by haipham on 5/17/17.
 */
public final class ScrapTest {
    @Test
    @SuppressWarnings("EmptyCatchBlock")
    public void test_multipleThreadsAccessingResource_shouldWork() {
        // Setup
        class UnsafeBooleanRef {
            private boolean ref = false;

            private boolean get() {
                return ref;
            }

            private void getAndSet(boolean b) {
                ref = b;
            }
        }

        final AtomicBoolean ATOMIC = new AtomicBoolean(false);
        final UnsafeBooleanRef UNSAFE = new UnsafeBooleanRef();
        List<Thread> threads = new LinkedList<>();

        for (int i = 0; i < 10; i++) {
            final int INDEX = i;

            threads.add(new Thread(() -> {
                LogUtil.println(INDEX);

                for (;;) {
                    if (!UNSAFE.get()) {
                        UNSAFE.getAndSet(true);

                        try {
                            TimeUnit.MILLISECONDS.sleep(100);
                        } catch (InterruptedException e) {
                            UNSAFE.getAndSet(false);
                        }

                        UNSAFE.getAndSet(false);
                        break;
                    }
                }
            }));
        }

        // When & Then
        threads.forEach(Thread::run);

        try {
            TimeUnit.MILLISECONDS.sleep(2000);
        } catch (Exception e) {}
    }
}
