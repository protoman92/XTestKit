package org.swiften.xtestkit.util;

import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.localizer.Localizer;
import org.swiften.javautilities.localizer.LocalizerType;
import org.swiften.javautilities.log.LogUtil;
import org.swiften.javautilities.test.TestNGUtil;
import org.swiften.xtestkit.base.model.InputHelperType;
import org.swiften.xtestkit.mobile.Platform;
import org.swiften.xtestkitcomponents.platform.PlatformType;
import org.testng.annotations.DataProvider;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Created by haipham on 5/6/17.
 */
public final class TestHelper {
    /**
     * Provide {@link PlatformType} arguments.
     * @return {@link Iterator} of {@link Object} array.
     * @see TestNGUtil#oneFromEach(Object...)
     * @see Platform#values()
     */
    @NotNull
    @DataProvider(parallel = true)
    public static Iterator<Object[]> platformProvider() {
        return TestNGUtil.oneFromEach((Object[])Platform.values()).iterator();
    }

    /**
     * Provide {@link PlatformType}.
     * @return {@link PlatformType} instance.
     */
    @NotNull
    public static PlatformType mockPlatform() {
        return () -> "value";
    }

    /**
     * Provide {@link InputHelperType}.
     * @param platform {@link PlatformType} instance.
     * @return {@link InputHelperType} instance.
     */
    @NotNull
    public static InputHelperType mockHelper(@NotNull PlatformType platform) {
        InputHelperType helper = mock(InputHelperType.class);
        LocalizerType localizer = Localizer.builder().build();
        doReturn(localizer).when(helper).localizer();
        doReturn(platform).when(helper).platform();
        return helper;
    }

    /**
     * Same as above, but uses a default {@link PlatformType}.
     * @return {@link InputHelperType} instance.
     * @see #mockHelper(PlatformType)
     * @see #mockPlatform()
     */
    @NotNull
    public static InputHelperType mockHelper() {
        PlatformType platform = mockPlatform();
        return mockHelper(platform);
    }

    private TestHelper() {}
}
