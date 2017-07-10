package org.swiften.xtestkit.ios;

/**
 * Created by haipham on 4/3/17.
 */

import org.swiften.xtestkit.base.PlatformView;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkitcomponents.common.ErrorProviderType;
import org.swiften.xtestkitcomponents.view.ViewType;
import org.swiften.xtestkit.mobile.Platform;

/**
 * Provide {@link Platform#IOS} {@link ViewType}
 */
public class IOSView extends PlatformView {
    public enum Type implements ViewType, ErrorProviderType {
        UI_BUTTON,
        UI_COLLECTION_VIEW,
        UI_COLLECTION_VIEW_CELL,
        UI_IMAGE_VIEW,
        UI_LABEL,
        UI_LINK,
        UI_PICKER,
        UI_PICKER_WHEEL,
        UI_SEARCH_BAR,
        UI_SECURE_TEXT_FIELD,
        UI_SEGMENTED_CONTROL,
        UI_SCROLL_VIEW,
        UI_STATIC_TEXT,
        UI_STATUS_BAR,
        UI_SWITCH,
        UI_TABLE_VIEW,
        UI_TABLE_VIEW_CELL,
        UI_TEXT_FIELD,
        UI_TEXT_VIEW,
        UI_TOOLBAR,
        UI_WINDOW,
        UNDEFINED;

        /**
         * @return {@link String} value.
         * @see ViewType#className()
         * @see #NOT_AVAILABLE
         */
        @NotNull
        @Override
        public String className() {
            switch (this) {
                case UI_BUTTON:
                    return "XCUIElementTypeButton";

                case UI_COLLECTION_VIEW:
                    return "XCUIElementTypeCollectionView";

                case UI_IMAGE_VIEW:
                    return "XCUIElementTypeImage";

                case UI_LABEL:
                    return "XCUIElementTypeLabel";

                case UI_LINK:
                    return "XCUIElementTypeLink";

                case UI_PICKER:
                    return "XCUIElementTypePicker";

                case UI_PICKER_WHEEL:
                    return "XCUIElementTypePickerWheel";

                case UI_SEARCH_BAR:
                    return "XCUIElementTypeSearchField";

                case UI_SECURE_TEXT_FIELD:
                    return "XCUIElementTypeSecureTextField";

                case UI_SCROLL_VIEW:
                    return "XCUIElementTypeScrollView";

                case UI_SEGMENTED_CONTROL:
                    return "XCUIElementTypeSegmentedControl";

                case UI_STATIC_TEXT:
                    return "XCUIElementTypeStaticText";

                case UI_STATUS_BAR:
                    return "XCUIElementTypeStatusBar";

                case UI_SWITCH:
                    return "XCUIElementTypeSwitch";

                case UI_TABLE_VIEW:
                    return "XCUIElementTypeTable";

                case UI_TABLE_VIEW_CELL:
                case UI_COLLECTION_VIEW_CELL:
                    return "XCUIElementTypeCell";

                case UI_TEXT_FIELD:
                    return "XCUIElementTypeTextField";

                case UI_TOOLBAR:
                    return "XCUIElementTypeToolbar";

                case UI_TEXT_VIEW:
                    return "XCUIElementTypeTextView";

                case UI_WINDOW:
                    return "XCUIElementTypeWindow";

                case UNDEFINED:
                    return "XCUIElementTypeOther";

                default:
                    throw new RuntimeException(NOT_AVAILABLE);
            }
        }

        /**
         * @return {@link String} value.
         * @see ViewType#hasText()
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
         * @see ViewType#isClickable()
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
         * @see ViewType#isEditable()
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
     * @return Array of {@link ViewType}.
     * @see PlatformView#getViews()
     */
    @NotNull
    @Override
    public ViewType[] getViews() {
        return Type.values();
    }
}
