package org.swiften.xtestkit.ios;

/**
 * Created by haipham on 4/3/17.
 */

import org.swiften.xtestkit.base.PlatformView;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.type.BaseErrorType;
import org.swiften.xtestkit.base.type.BaseViewType;
import org.swiften.xtestkit.mobile.Platform;

/**
 * Provide {@link Platform#IOS} {@link BaseViewType}
 */
public class IOSView extends PlatformView {
    public enum ViewType implements BaseViewType, BaseErrorType {
        UI_BUTTON,
        UI_LABEL,
        UI_LINK,
        UI_TEXTFIELD,
        UI_TABLEVIEW,
        UI_TABLEVIEW_CELL,
        UI_SECURE_TEXTFIELD,
        UI_PICKERWHEEL;

        /**
         * @return {@link String} value.
         * @see BaseViewType#className()
         * @see #NOT_AVAILABLE
         */
        @NotNull
        @Override
        public String className() {
            switch (this) {
                case UI_BUTTON:
                    return "XCUIElementTypeButton";

                case UI_LABEL:
                    return "XCUIElementTypeLabel";

                case UI_LINK:
                    return "XCUIElementTypeLink";

                case UI_TEXTFIELD:
                    return "XCUIElementTypeTextField";

                case UI_SECURE_TEXTFIELD:
                    return "XCUIElementTypeSecureTextField";

                case UI_TABLEVIEW:
                    return "XCUIElementTypeTable";

                case UI_TABLEVIEW_CELL:
                    return "XCUIElementTypeCell";

                case UI_PICKERWHEEL:
                    return "XCUIElementTypePickerWheel";

                default:
                    throw new RuntimeException(NOT_AVAILABLE);
            }
        }

        /**
         * @return {@link String} value.
         * @see BaseViewType#hasText()
         */
        @Override
        public boolean hasText() {
            switch (this) {
                case UI_BUTTON:
                case UI_LABEL:
                case UI_TEXTFIELD:
                case UI_SECURE_TEXTFIELD:
                    return true;

                default:
                    return false;
            }
        }

        /**
         * @return {@link Boolean} value.
         * @see BaseViewType#isClickable()
         */
        @Override
        public boolean isClickable() {
            switch (this) {
                case UI_BUTTON:
                    return true;

                default:
                    return false;
            }
        }

        /**
         * @return {@link Boolean} value.
         * @see BaseViewType#isEditable()
         */
        @Override
        public boolean isEditable() {
            switch (this) {
                case UI_TEXTFIELD:
                case UI_SECURE_TEXTFIELD:
                    return true;

                default:
                    return false;
            }
        }
    }

    /**
     * @return Array of {@link BaseViewType}.
     * @see PlatformView#getViews()
     */
    @NotNull
    @Override
    public BaseViewType[] getViews() {
        return IOSView.ViewType.values();
    }
}
