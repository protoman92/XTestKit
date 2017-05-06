package com.swiften.xtestkit.engine.base;

import com.swiften.xtestkit.engine.base.xpath.XPath;

import com.swiften.xtestkit.engine.base.Platform;
import static org.testng.Assert.*;

import org.swiften.javautilities.log.LogUtil;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Created by haipham on 3/19/17.
 */
public final class XPathTest {
    @Test
    public void test_buildXPath_shouldSucceed() {
        // Setup
        XPath xPath = XPath.builder(Platform.ANDROID)
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
        LogUtil.println(attribute);
    }
}
