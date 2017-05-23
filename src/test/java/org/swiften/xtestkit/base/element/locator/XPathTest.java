package org.swiften.xtestkit.base.element.locator;

import org.swiften.javautilities.log.LogUtil;
import org.swiften.xtestkit.base.element.locator.general.xpath.XPath;
import org.swiften.xtestkit.mobile.Platform;
import org.testng.annotations.Test;

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
//            .ofClass("class1")
//            .containsID("id1")
//            .hasText("\"2'11\"")
//            .containsText("Register")
//            .containsText("Te'xt2")
//            .hasHint("Hint1")
//            .containsHint("Hint2")
//            .isEnabled(true)
//            .isClickable(true)
            .setIndex(0)
            .build();

        XPath xPath2 = XPath.builder(Platform.ANDROID)
            .atIndex(0)
            .ofInstance(2)
            .addChildXPath(xPath1)
            .build();

        XPath xPath3 = XPath.builder(Platform.ANDROID)
            .withXPath(xPath2)
            .withXPath(xPath1)
            .isClickable(true)
            .build();

        // When & Then
        LogUtil.println(xPath1.fullAttribute());
        LogUtil.println(xPath2.fullAttribute());
        LogUtil.println(xPath3.fullAttribute());
    }
}
