package io.github.tivj.f5transitions.perspectives;

import io.github.tivj.f5transitions.TransitionPhase;

public interface Perspective {
    float getCameraYRotation(TransitionPhase transitionPhase);

    /**
     * Calculates the camera distance in 2nd and 3rd person views.
     * @param maxDistance Max distance in vanilla
     * @return Distance that should be used with current perspective
     */
    float getCameraDistance(float maxDistance);
    int getID();

    /**
     * Used to calculate the player's opacity only during transition to this perspective.
     */
    float getPlayerOpacity(float progress);
}
