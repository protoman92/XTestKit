package org.swiften.xtestkit.mobile.android;

import org.swiften.xtestkit.base.PlatformView;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.type.BaseViewType;
import org.swiften.xtestkit.mobile.Platform;

/**
 * Created by haipham on 3/20/17.
 */

/**
 * Provide {@link Platform#ANDROID} {@link BaseViewType}.
 */
public class AndroidView extends PlatformView {
    public enum ViewType implements BaseViewType {
        BUTTON,
        EDIT_TEXT,
        LINEAR_LAYOUT,
        LIST_VIEW,
        TEXT_VIEW,
        NUMBER_PICKER;

        @NotNull
        @Override
        public String className() {
            switch (this) {
                case BUTTON:
                    return "android.widget.Button";

                case EDIT_TEXT:
                    return "android.widget.EditText";

                case LINEAR_LAYOUT:
                    return "android.widget.LinearLayout";

                case LIST_VIEW:
                    return "android.widget.ListView";

                case TEXT_VIEW:
                    return "android.widget.TextView";

                case NUMBER_PICKER:
                    return "android.widget.NumberPicker";

                default:
                    throw new RuntimeException();
            }
        }

        @Override
        public boolean hasText() {
            switch (this) {
                case BUTTON:
                case EDIT_TEXT:
                case LINEAR_LAYOUT:
                case TEXT_VIEW:
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public boolean isClickable() {
            switch (this) {
                case BUTTON:
                case EDIT_TEXT:
                case TEXT_VIEW:
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public boolean isEditable() {
            switch (this) {
                case EDIT_TEXT:
                    return true;

                default:
                    return false;
            }
        }
    }

    @NotNull
    @Override
    protected BaseViewType[] getViews() {
        return AndroidView.ViewType.values();
    }
}
