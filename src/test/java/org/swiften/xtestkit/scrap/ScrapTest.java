package org.swiften.xtestkit.scrap;

import org.swiften.javautilities.log.LogUtil;
import org.swiften.xtestkit.android.element.date.AndroidDatePickerType;
import org.swiften.xtestkit.android.element.date.UnitNumberPickerWrapper;
import org.swiften.xtestkit.base.element.date.CalendarUnit;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by haipham on 5/17/17.
 */
@SuppressWarnings("UndeclaredTests")
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

    @Test
    public void test_dateFormat_shouldWork() {
        // Setup
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        Date date = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");

        // When && Then
        LogUtil.println(formatter.format(date));
    }
}
