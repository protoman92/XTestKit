package org.swiften.xtestkit.base.model;

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.PlatformView;
import org.swiften.xtestkitcomponents.view.ViewType;

import java.util.Arrays;
import java.util.Random;

import static org.mockito.Mockito.spy;

/**
 * Created by haipham on 5/8/17.
 */
public class MockPlatformView extends PlatformView {
    @NotNull private final Random RAND;

    public final int VIEW_COUNT;

    {
        RAND = new Random();

            /* The number of ViewType to pass to PlatformView */
        VIEW_COUNT = 1000;
    }

    @NotNull
    @Override
    protected ViewType[] getViews() {
        return Arrays
            .stream(new Object[VIEW_COUNT])
            .map(a -> spy(new MockView(RAND)))
            .toArray(ViewType[]::new);
    }

    static class MockView implements ViewType {
        @NotNull private final Random RAND;

        MockView(@NotNull Random rand) {
            RAND = rand;
        }

        @NotNull
        @Override
        public String className() {
            return getClass().getSimpleName();
        }

        @Override
        public boolean hasText() {
            return RAND.nextBoolean();
        }

        @Override
        public boolean isEditable() {
            return RAND.nextBoolean();
        }

        @Override
        public boolean isClickable() {
            return RAND.nextBoolean();
        }

        @NotNull
        @Override
        public String toString() {
            String base = "";
            base += ("\nhasText: " + hasText());
            base += ("\nisClickable: " + isClickable());
            base += ("\nisEditable: " + isEditable());
            return base;
        }
    }
}
