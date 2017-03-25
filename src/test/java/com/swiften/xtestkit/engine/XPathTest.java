package com.swiften.xtestkit.engine;

import com.swiften.engine.base.XPath;

import static org.junit.Assert.*;

import com.swiften.engine.mobile.Platform;
import com.swiften.util.Log;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Created by haipham on 3/19/17.
 */
public final class XPathTest {
    @Test
    public void test_buildXPath_shouldSucceed() {
        // Setup
        XPath xPath = XPath.newBuilder(Platform.ANDROID)
            .hasText("Text1")
            .containsText("Text2")
            .hasHint("Hint1")
            .containsHint("Hint2")
            .isEnabled(true)
            .isClickable(true)
            .build();

        // When
        int groupCount = 6;
        String attribute = xPath.getAttribute();
        String trimmed = attribute.substring(1, attribute.length() - 1);
        List<String> groups = Arrays.asList(trimmed.split("\\]\\["));

        // Then
        assertEquals(groupCount, groups.size());
        Log.println(attribute);
    }
}
