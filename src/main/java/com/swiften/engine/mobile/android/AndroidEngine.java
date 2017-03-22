package com.swiften.engine.mobile.android;

import com.swiften.engine.base.PlatformEngine;
import com.swiften.engine.base.param.StartEnvParam;
import com.swiften.engine.mobile.MobileEngine;
import com.swiften.engine.mobile.android.protocol.AndroidDelay;
import com.swiften.engine.mobile.android.protocol.AndroidEngineError;
import com.swiften.util.Log;
import com.swiften.util.ProcessRunner;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.reactivex.Flowable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.schedulers.Schedulers;
import org.apache.bcel.generic.RET;
import org.apache.commons.collections.FastHashMap;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.regexp.RE;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.reactivestreams.Publisher;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * Created by haipham on 3/22/17.
 */
public class AndroidEngine extends MobileEngine<
    AndroidElement,
    AndroidDriver<AndroidElement>
    >
    implements
    AndroidDelay,
    AndroidEngineError {
    @NotNull
    public static Builder newBuilder() {
        return new Builder();
    }

    @NotNull String appActivity;

    AndroidEngine() {
        super();
        appActivity = "";
    }

    /**
     * @return A {@link Map} of capabilities.
     * @see MobileEngine#capabilities()
     */
    @NotNull
    @Override
    public Map<String,Object> capabilities() {
        Map<String,Object> capabilities = super.capabilities();
        capabilities.put(AndroidMobileCapabilityType.APP_PACKAGE, appPackage);
        return capabilities;
    }

    /**
     * @return An {@link AndroidDriver} instance.
     * @see PlatformEngine#createDriverInstance()
     */
    @NotNull
    @Override
    protected AndroidDriver<AndroidElement> createDriverInstance() {
        try {
            URL url = new URL(serverUrl);
            DesiredCapabilities capabilities = desiredCapabilities();
            return new AndroidDriver<>(url, capabilities);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get ${ANDROID_HOME} from Environment variables.
     * @return A {@link String} value.
     */
    @NotNull
    public String androidHome() {
        String androidHome = System.getenv("ANDROID_HOME");

        if (Objects.isNull(androidHome) || androidHome.isEmpty()) {
            throw new RuntimeException(ANDROID_HOME_NOT_SET);
        }

        return androidHome;
    }

    /**
     * Get path to adb.
     * @return A {@link String} value.
     */
    @NotNull
    public String adb() {
        return String.format("%s/platform-tools/adb", androidHome());
    }

    /**
     * Get path to adb shell CLI.
     * @return A {@link String} value.
     */
    @NotNull
    public String adbShell() {
        return String.format("%s shell", adb());
    }

    /**
     * Get path to Android emulator CLI.
     * @return A {@link String} value.
     */
    @NotNull
    public String emulator() {
        return String.format("%s/tools/emulator", androidHome());
    }

    //region Device Methods
    @NotNull
    public Flowable<Boolean> rxStartApp() {
        String start = String.format("%1$s am start -m %2$s/%3$s",
            androidHome(),
            appPackage,
            appActivity);

        return processRunner().rxExecute(start).map(a -> true);
    }

    /**
     * Command to start an emulator whose name is {@link #deviceName}.
     * @return A {@link String} value.
     */
    @NotNull
    public String startEmulator() {
        return String.format("%1$s -avd %2$s", emulator(), deviceName);
    }

    /**
     * Command to check bootanim status from adb shell.
     * We can check whether the emulator is fully started by checking its
     * bootanim. If this value is 'stopped', the emulator has booted up
     * completely.
     * @return A {@link String} value.
     */
    @NotNull
    public String bootAnim() {
        return String.format("%s getprop init.svc.bootanim", adbShell());
    }

    /**
     * Start the emulator with the specified settings, mainly
     * {@link #deviceName} and {@link #testMode}. Detect when bootanim is
     * 'closed' and then emit value.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    public Flowable<Boolean> rxStartEmulator(@NotNull StartEnvParam param) {
        final ProcessRunner PROCESS_RUNNER = processRunner();
        final int RETRIES = param.retriesOnError();

        @SuppressWarnings("WeakerAccess")
        final long DELAY = emulatorBootRetryDelay();

        /* Append any error when starting up the emulator to this list, and
         * have retryWhen read it to determine whether to continue retrying */
        final List<Exception> ERRORS = new ArrayList<>();

        /* We need to start the emulator on a new Thread, or else it will
         * block the rest of the operations */
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PROCESS_RUNNER.execute(startEmulator());
                } catch (Exception e) {
                    ERRORS.add(e);
                }
            }
        }).start();

        return PROCESS_RUNNER
            .rxExecute(bootAnim())
            .filter(Objects::nonNull)
            .map(String::trim)

            /* There are two errors that can be thrown here:
             * When the emulator is first started: no devices/emulators found
             * When the emulator is booting up: device unauthorized
             * Afterwards, when the emulator has booted up fully, a value
             * 'stopped' will be emitted */
            .filter(a -> a.equals("stopped"))
            .switchIfEmpty(Flowable.error(new Exception()))
            .retryWhen(a -> a
                /* We add one to the retry count, or else we won't be able
                 * to catch when the count is exceeded */
                .zipWith(Flowable.range(1, RETRIES + 1), (e, i) -> {
                    if (i > RETRIES) {
                        throw Exceptions.propagate(e);
                    } else {
                        return e;
                    }
                })
                .flatMap(e -> {
                    if (ERRORS.isEmpty()) {
                        return Flowable.timer(DELAY, TimeUnit.MILLISECONDS);
                    } else {
                        return Flowable.<Long>error(e);
                    }
                }))
            .map(a -> true);
    }

    /**
     * @return A {@link Flowable} instance.
     * @see PlatformEngine#rxStartTestEnvironment(StartEnvParam)
     */
    @NotNull
    @Override
    public Flowable<Boolean> rxStartTestEnvironment(@NotNull StartEnvParam param) {
        switch (testMode()) {
            case EMULATOR:
                return rxStartEmulator(param);

            default:
                return Flowable.error(new Exception(PLATFORM_UNAVAILABLE));
        }
    }
    //endregion

    public static final class Builder extends MobileEngine.Builder<AndroidEngine> {
        @NotNull
        @Override
        protected AndroidEngine createEngineInstance() {
            return new AndroidEngine();
        }

        /**
         * Set the {@link #ENGINE#appActivity} value. This value is used to
         * determine which Activity is started first.
         * @param appActivity A {@link String} value.
         * @return A {@link Builder} instance.
         */
        @NotNull
        public Builder withAppActivity(@NotNull String appActivity) {
            ENGINE.appActivity = appActivity;
            return this;
        }
    }
}
