package com.swiften.xtestkit.engine;

import com.swiften.engine.Platform;
import com.swiften.engine.XPath;
import com.swiften.util.Log;
import org.junit.Test;

/**
 * Created by haipham on 3/19/17.
 */
public class XPathTest {
    @Test
    public void test_buildXPath_shouldWorkCorrectly() {
        XPath xPath = XPath.newBuilder(Platform.ANDROID)
                .hasText("Text1")
                .containsText("Text2")
                .isEnabled(true)
                .build();

        String attribute = xPath.getAttribute();
        Log.println(attribute);
    }
}
