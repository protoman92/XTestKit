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
        UI_IMAGEVIEW,
        UI_LABEL,
        UI_LINK,
        UI_PICKERWHEEL,
        UI_SEARCHBAR,
        UI_SECURETEXTFIELD,
        UI_SCROLLVIEW,
        UI_STATICTEXT,
        UI_STATUSBAR,
        UI_SWITCH,
        UI_TABLEVIEW,
        UI_TABLEVIEW_CELL,
        UI_TEXTFIELD,
        UI_TEXTVIEW,
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

                case UI_IMAGEVIEW:
                    return "XCUIElementTypeImage";

                case UI_LABEL:
                    return "XCUIElementTypeLabel";

                case UI_LINK:
                    return "XCUIElementTypeLink";

                case UI_PICKERWHEEL:
                    return "XCUIElementTypePickerWheel";

                case UI_SEARCHBAR:
                    return "XCUIElementTypeSearchField";

                case UI_SECURETEXTFIELD:
                    return "XCUIElementTypeSecureTextField";

                case UI_SCROLLVIEW:
                    return "XCUIElementTypeScrollView";

                case UI_STATICTEXT:
                    return "XCUIElementTypeStaticText";

                case UI_STATUSBAR:
                    return "XCUIElementTypeStatusBar";

                case UI_SWITCH:
                    return "XCUIElementTypeSwitch";

                case UI_TABLEVIEW:
                    return "XCUIElementTypeTable";

                case UI_TABLEVIEW_CELL:
                    return "XCUIElementTypeCell";

                case UI_TEXTFIELD:
                    return "XCUIElementTypeTextField";

                case UI_TOOLBAR:
                    return "XCUIElementTypeToolbar";

                case UI_TEXTVIEW:
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
                case UI_TEXTFIELD:
                case UI_SECURETEXTFIELD:
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
                case UI_SECURETEXTFIELD:
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
