package org.swiften.xtestkit.ios;

/**
 * Created by haipham on 4/3/17.
 */

import org.swiften.xtestkit.base.PlatformView;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkitcomponents.common.BaseErrorType;
import org.swiften.xtestkit.base.type.BaseViewType;
import org.swiften.xtestkit.mobile.Platform;

/**
 * Provide {@link Platform#IOS} {@link BaseViewType}
 */
public class IOSView extends PlatformView {
    public enum ViewType implements BaseViewType, BaseErrorType {
        UI_BUTTON,
        UI_IMAGE_VIEW,
        UI_LABEL,
        UI_LINK,
        UI_PICKER_WHEEL,
        UI_SEARCH_BAR,
        UI_SECURE_TEXT_FIELD,
        UI_SCROLL_VIEW,
        UI_STATIC_TEXT,
        UI_STATUS_BAR,
        UI_SWITCH,
        UI_TABLE_VIEW,
        UI_TABLE_VIEW_CELL,
        UI_TEXT_FIELD,
        UI_TEXT_VIEW,
        UI_TOOLBAR,
        UI_WINDOW;

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

                case UI_IMAGE_VIEW:
                    return "XCUIElementTypeImage";

                case UI_LABEL:
                    return "XCUIElementTypeLabel";

                case UI_LINK:
                    return "XCUIElementTypeLink";

                case UI_PICKER_WHEEL:
                    return "XCUIElementTypePickerWheel";

                case UI_SEARCH_BAR:
                    return "XCUIElementTypeSearchField";

                case UI_SECURE_TEXT_FIELD:
                    return "XCUIElementTypeSecureTextField";

                case UI_SCROLL_VIEW:
                    return "XCUIElementTypeScrollView";

                case UI_STATIC_TEXT:
                    return "XCUIElementTypeStaticText";

                case UI_STATUS_BAR:
                    return "XCUIElementTypeStatusBar";

                case UI_SWITCH:
                    return "XCUIElementTypeSwitch";

                case UI_TABLE_VIEW:
                    return "XCUIElementTypeTable";

                case UI_TABLE_VIEW_CELL:
                    return "XCUIElementTypeCell";

                case UI_TEXT_FIELD:
                    return "XCUIElementTypeTextField";

                case UI_TOOLBAR:
                    return "XCUIElementTypeToolbar";

                case UI_TEXT_VIEW:
                    return "XCUIElementTypeTextView";

                case UI_WINDOW:
                    return "XCUIElementTypeWindow";

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
                case UI_TEXT_FIELD:
                case UI_SECURE_TEXT_FIELD:
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
                case UI_TEXT_FIELD:
                case UI_SECURE_TEXT_FIELD:
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
