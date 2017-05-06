package org.swiften.xtestkit.engine.mobile.ios;

/**
 * Created by haipham on 4/3/17.
 */

import org.swiften.xtestkit.engine.base.PlatformView;
import org.swiften.xtestkit.engine.base.View;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.engine.base.Platform;

/**
 * Provide {@link Platform#IOS} {@link View}
 */
public class IOSView extends PlatformView {
    enum ViewType implements View {
        UI_BUTTON,
        UI_LABEL,
        UI_TEXTFIELD;

        @NotNull
        @Override
        public String className() {
            switch (this) {
                case UI_BUTTON:
                    return "XCUIElementTypeButton";

                case UI_LABEL:
                    return "XCUIElementTypeLabel";

                case UI_TEXTFIELD:
                    return "XCUIElementTypeTextField";

                default:
                    return "";
            }
        }

        @Override
        public boolean hasText() {
            switch (this) {
                case UI_BUTTON:
                case UI_LABEL:
                case UI_TEXTFIELD:
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public boolean isClickable() {
            switch (this) {
                case UI_BUTTON:
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public boolean isEditable() {
            switch (this) {
                case UI_TEXTFIELD:
                    return true;

                default:
                    return false;
            }
        }
    }

    @NotNull
    @Override
    public View[] getViews() {
        return ViewType.values();
    }
}
