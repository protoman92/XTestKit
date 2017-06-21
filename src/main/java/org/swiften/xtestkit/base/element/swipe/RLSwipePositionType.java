package org.swiften.xtestkit.base.element.swipe;

/**
 * Created by haipham on 29/5/17.
 */

/**
 * This interface provides ratios to determine the swipe's relative position
 * on the screen.
 */
public interface RLSwipePositionType {
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

    /**
     * This ratio determines where the metaphorical finger is placed.
     * Depending on the supplied
     * {@link org.swiften.xtestkit.navigation.ScreenType.Direction}, this can
     * be used for vertical/horizontal dimension.
     * @return {@link Double} value.
     */
    default double anchorRatio() {
        return 0.5d;
    }
}
