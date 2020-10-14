package io.github.tivj.f5transitions.perspectives;

import io.github.tivj.f5transitions.TransitionPhase;

public interface Perspective {
    float getCameraYRotation(TransitionPhase transitionPhase);

    /**
     * Calculates the distance multiplier
     * @param distance Distance in blocks, when distance multiplier is 1
     * @return multiplier
     */
    float getDistanceMultiplier(float distance);
    int getID();

    /**
     * Used to calculate the player's opacity only during transition to this perspective.
     */
    float getPlayerOpacity(float progress);
    float getDefaultPlayerOpacity();
}
