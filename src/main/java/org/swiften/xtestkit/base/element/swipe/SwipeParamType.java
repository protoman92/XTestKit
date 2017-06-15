package org.swiften.xtestkit.base.element.swipe;

/**
 * Created by haipham on 5/8/17.
 */

/**
 * This interface provides the methods necessary for a swipe action.
 */
public interface SwipeParamType {
    int startX();
    int startY();
    int endX();
    int endY();

    default int duration() {
        return 1000;
    }
}
