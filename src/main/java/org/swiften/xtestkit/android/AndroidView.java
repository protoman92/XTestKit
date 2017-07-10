package org.swiften.xtestkit.android;

import org.swiften.xtestkit.base.PlatformView;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkitcomponents.common.ErrorProviderType;
import org.swiften.xtestkitcomponents.view.ViewType;
import org.swiften.xtestkit.mobile.Platform;

/**
 * Created by haipham on 3/20/17.
 */

/**
 * Provide {@link Platform#ANDROID} {@link ViewType}.
 */
public class AndroidView extends PlatformView implements ErrorProviderType {
    public enum Type implements ViewType {
        BUTTON,
        CHECKED_TEXT_VIEW,
        EDIT_TEXT,
        DATE_PICKER,
        FRAME_LAYOUT,
        IMAGE_VIEW,
        LINEAR_LAYOUT,
        LIST_VIEW,
        NUMBER_PICKER,
        PROGRESS_BAR,
        SWITCH,
        TEXT_VIEW,
        TIME_PICKER,
        VIEW_PAGER,
        VIEW_PAGER_I;

        @NotNull
        @Override
        public String className() {
            switch (this) {
                case BUTTON:
                    return "android.widget.Button";

                case CHECKED_TEXT_VIEW:
                    return "android.widget.CheckedTextView";

                case DATE_PICKER:
                    return "android.widget.DatePicker";

                case EDIT_TEXT:
                    return "android.widget.EditText";

                case FRAME_LAYOUT:
                    return "android.widget.FrameLayout";

                case IMAGE_VIEW:
                    return "android.widget.ImageView";

                case LINEAR_LAYOUT:
                    return "android.widget.LinearLayout";

                case LIST_VIEW:
                    return "android.widget.ListView";

                case NUMBER_PICKER:
                    return "android.widget.NumberPicker";

                case PROGRESS_BAR:
                    return "android.widget.ProgressBar";

                case SWITCH:
                    return "android.widget.Switch";

                case TEXT_VIEW:
                    return "android.widget.TextView";

                case TIME_PICKER:
                    return "android.widget.TimePicker";

                case VIEW_PAGER:
                    return "android.support.v4.view.ViewPager";

                case VIEW_PAGER_I:
                    return "com.android.internal.widget.ViewPager";

                default:
                    throw new RuntimeException(NOT_AVAILABLE);
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
    protected ViewType[] getViews() {
        return Type.values();
    }
}
