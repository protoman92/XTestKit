package org.swiften.xtestkit.mobile.ios;

/**
 * Created by haipham on 4/3/17.
 */

import org.swiften.xtestkit.base.PlatformView;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.type.BaseViewType;
import org.swiften.xtestkit.mobile.Platform;

/**
 * Provide {@link Platform#IOS} {@link BaseViewType}
 */
public class IOSView extends PlatformView {
    enum ViewType implements BaseViewType {
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
    public BaseViewType[] getViews() {
        return IOSView.ViewType.values();
    }
}
