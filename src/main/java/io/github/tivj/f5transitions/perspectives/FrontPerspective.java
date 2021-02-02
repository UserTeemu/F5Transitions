package io.github.tivj.f5transitions.perspectives;

import io.github.tivj.f5transitions.TransitionPhase;
import io.github.tivj.f5transitions.config.TransitionsConfig;

public class FrontPerspective implements Perspective {
    @Override
    public float getCameraYRotation(TransitionPhase transitionPhase) {
        return TransitionsConfig.rotateCameraClockwise ? 180F : -180F;
    }

    @Override
    public float getCameraDistance(float maxDistance) {
        return -maxDistance;
    }

    /**
     * Used to calculate the player's opacity only during transition to this perspective.
     */
    @Override
    public float getPlayerOpacity(float progress) {
        return 1F;
    }

    @Override
    public int getID() {
        return 2;
    }
}
