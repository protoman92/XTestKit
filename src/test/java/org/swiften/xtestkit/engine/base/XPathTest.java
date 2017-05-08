package org.swiften.xtestkit.engine.base;

import org.swiften.xtestkit.engine.mobile.Platform;
import org.swiften.xtestkit.engine.base.locator.xpath.XPath;

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
        XPath xPath = XPath.builder(Platform.IOS)
            .containsID("id1")
            .hasText("Text1")
            .containsText("Text2")
            .hasHint("Hint1")
            .containsHint("Hint2")
            .isEnabled(true)
            .isClickable(true)
            .build();

        // When
        String attribute = xPath.getAttribute();
        String trimmed = attribute.substring(1, attribute.length() - 1);
        List<String> groups = Arrays.asList(trimmed.split("\\]\\["));

        // Then
        LogUtil.println(attribute);
    }
}
