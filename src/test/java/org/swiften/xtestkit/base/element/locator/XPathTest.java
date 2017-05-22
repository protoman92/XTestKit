package org.swiften.xtestkit.base.element.locator;

import org.swiften.xtestkit.mobile.Platform;
import org.swiften.xtestkit.base.element.locator.general.xpath.XPath;

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
        XPath xPath1 = XPath.builder(Platform.ANDROID)
            .atIndex(1)
            .ofInstance(1)
            .ofClass("class1")
            .containsID("id1")
            .hasText("\"2'11\"")
            .containsText("Register")
            .containsText("Te'xt2")
            .hasHint("Hint1")
            .containsHint("Hint2")
            .isEnabled(true)
            .isClickable(true)
            .build();

        XPath xPath2 = XPath.builder(Platform.ANDROID)
            .atIndex(0)
            .ofInstance(2)
            .addChildXPath(xPath1)
            .build();

        // When
        String attr1 = xPath1.attribute();
        String attr2 = xPath2.attribute();

        // Then
        LogUtil.println(attr1);
        LogUtil.println(attr2);
    }
}
