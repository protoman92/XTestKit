package org.swiften.xtestkit.base.element.popup;

/**
 * Created by haipham on 6/19/17.
 */

/**
 * This interface provides delay durations for {@link PopupActionType}.
 */
public interface PopupDelayType {
    /**
     * Get the delay for popup dismissal.
     * @return {@link Long} value.
     */
    default long popupDismissDelay() {
        return 1000;
    }

    /**
     * Get the interval duration for popup polling.
     * @return {@link Long} value.
     */
    default long popupPollDuration() {
        return 1000;
    }
}
