package com.swiften.xtestkit.engine;

import com.swiften.engine.Platform;
import com.swiften.engine.XPath;
import com.swiften.util.Log;
import static org.junit.Assert.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by haipham on 3/19/17.
 */
public final class XPathTest {
    @Test
    public void test_buildXPath_shouldWorkCorrectly() {
        // Setup
        XPath xPath = XPath.newBuilder(Platform.ANDROID)
            .hasText("Text1")
            .containsText("Text2")
            .hasHint("Hint1")
            .containsHint("Hint2")
            .isEnabled(true)
            .build();

        // When
        int groupCount = 5;
        String attribute = xPath.getAttribute();
        String trimmed = attribute.substring(1, attribute.length() - 1);
        List<String> groups = Arrays.asList(trimmed.split("\\]\\["));

        // Then
        assertEquals(groupCount, groups.size());
    }
}
