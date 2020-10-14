package io.github.tivj.f5transitions.perspectives;

import static io.github.tivj.f5transitions.utils.CalculationHelper.ease;

public class BehindPlayerPerspective implements Perspective {
    @Override
    public float getCameraYRotation() {
        return 0F;
    }

    @Override
    public float getDistanceMultiplier(float distance) {
        return 1F;
    }

    /**
     * Used to calculate the player's opacity only during transition to this perspective.
     */
    @Override
    public float getPlayerOpacity(float progress) {
        return ease(progress);
    }

    @Override
    public float getDefaultPlayerOpacity() {
        return 1F;
    }

    @Override
    public int getID() {
        return 1;
    }
}
