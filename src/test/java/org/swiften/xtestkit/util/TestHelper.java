package org.swiften.xtestkit.util;

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.mobile.Platform;
import org.swiften.xtestkitcomponents.platform.PlatformType;
import org.testng.annotations.DataProvider;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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

    private TestHelper() {}
}
