package org.swiften.xtestkit.engine.mobile.android;

import org.swiften.xtestkit.engine.base.PlatformView;
import org.swiften.xtestkit.engine.base.View;
import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.engine.base.Platform;

/**
 * Created by haipham on 3/20/17.
 */

/**
 * Provide {@link Platform#ANDROID} {@link View}.
 */
public class AndroidView extends PlatformView {
    enum ViewType implements View {
        BUTTON,
        EDIT_TEXT,
        LINEAR_LAYOUT,
        TEXT_VIEW;

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

                case TEXT_VIEW:
                    return "android.widget.TextView";

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
    protected View[] getViews() {
        return ViewType.values();
    }
}
