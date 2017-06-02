package org.swiften.xtestkit.android;

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
        EDITTEXT,
        IMAGEVIEW,
        LINEAR_LAYOUT,
        LIST_VIEW,
        NUMBER_PICKER,
        TEXTVIEW,
        SWITCH;

        @NotNull
        @Override
        public String className() {
            switch (this) {
                case BUTTON:
                    return "android.widget.Button";

                case EDITTEXT:
                    return "android.widget.EditText";

                case IMAGEVIEW:
                    return "android.widget.ImageView";

                case LINEAR_LAYOUT:
                    return "android.widget.LinearLayout";

                case LIST_VIEW:
                    return "android.widget.ListView";

                case NUMBER_PICKER:
                    return "android.widget.NumberPicker";

                case SWITCH:
                    return "android.widget.Switch";

                case TEXTVIEW:
                    return "android.widget.TextView";

                default:
                    throw new RuntimeException();
            }
        }

        @Override
        public boolean hasText() {
            switch (this) {
                case BUTTON:
                case EDITTEXT:
                case LINEAR_LAYOUT:
                case TEXTVIEW:
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public boolean isClickable() {
            switch (this) {
                case BUTTON:
                case EDITTEXT:
                case TEXTVIEW:
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public boolean isEditable() {
            switch (this) {
                case EDITTEXT:
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
