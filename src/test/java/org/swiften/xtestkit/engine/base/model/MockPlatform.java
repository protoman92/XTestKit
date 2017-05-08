package org.swiften.xtestkit.engine.base.model;

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.engine.base.locator.xpath.Attribute;
import org.swiften.xtestkit.engine.base.type.PlatformType;

/**
 * Created by haipham on 5/8/17.
 */
public class MockPlatform implements PlatformType {
    @NotNull
    @Override
    public String value() {
        return "";
    }

    @NotNull
    @Override
    public Attribute idAttribute() {
        return Attribute.withSingleAttribute("");
    }

    @NotNull
    @Override
    public Attribute textAttribute() {
        return Attribute.withSingleAttribute("");
    }

    @NotNull
    @Override
    public Attribute hintAttribute() {
        return Attribute.withSingleAttribute("");
    }
}
