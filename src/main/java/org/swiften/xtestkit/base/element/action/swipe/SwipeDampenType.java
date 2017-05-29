package org.swiften.xtestkit.base.element.action.swipe;

/**
 * Created by haipham on 29/5/17.
 */

/**
 * This interface provides dampening ratios to reduce the effect of a swipe
 * motion.
 */
public interface SwipeDampenType {
    /**
     * This ratio increases the origin of the swipe.
     * @return {@link Double} value.
     */
    default double startRatio() {
        return 0.2d;
    }

    /**
     * This ratio decreases the end point of the swipe.
     * @return {@link Double} value.
     */
    default double endRatio() {
        return 0.8d;
    }
}
