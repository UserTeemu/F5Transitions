package io.github.tivj.f5transitions.perspectives;

import io.github.tivj.f5transitions.TransitionPhase;

import static io.github.tivj.f5transitions.config.AnimationEasingConfiguration.EaseUse.OPACITY;

public class BehindPlayerPerspective implements Perspective {
    @Override
    public float getCameraYRotation(TransitionPhase transitionPhase) {
        return 0F;
    }

    @Override
    public float getCameraDistance(float maxDistance) {
        return maxDistance;
    }

    /**
     * Used to calculate the player's opacity only during transition to this perspective.
     */
    @Override
    public float getPlayerOpacity(float progress) {
        return OPACITY.getValue(progress);
    }

    @Override
    public int getID() {
        return 1;
    }
}
