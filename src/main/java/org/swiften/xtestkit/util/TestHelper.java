package org.swiften.xtestkit.util;

import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.localizer.LocalizerType;
import org.swiften.xtestkit.base.model.InputHelperType;
import org.swiften.xtestkit.mobile.Platform;
import org.swiften.xtestkitcomponents.platform.PlatformType;
import org.testng.annotations.DataProvider;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Created by haipham on 5/6/17.
 */
public final class TestHelper {
    /**
     * Provide {@link PlatformType} arguments.
     * @return {@link Iterator} of {@link Object} array.
     * @see Platform#values()
     */
    @NotNull
    @DataProvider(parallel = true)
    public static Iterator<Object[]> platformProvider() {
        Platform[] platforms = Platform.values();
        List<Object[]> data = new LinkedList<>();

        for (Platform platform : platforms) {
            data.add(new Object[] { platform });
        }

        return data.iterator();
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
     * @return {@link InputHelperType} instance.
     */
    @NotNull
    public static InputHelperType mockHelper() {
        InputHelperType helper = mock(InputHelperType.class);
        PlatformType platform = mockPlatform();
        LocalizerType localizer = mock(LocalizerType.class);
        doReturn(localizer).when(helper).localizer();
        doReturn("").when(localizer).localize(anyString());
        doReturn(platform).when(helper).platform();
        return helper;
    }

    private TestHelper() {}
}
