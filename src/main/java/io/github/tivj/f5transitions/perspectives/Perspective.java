package io.github.tivj.f5transitions.perspectives;

public interface Perspective {
    float getCameraYRotation();

    /**
     * Calculates the camera distance in 2nd and 3rd person views.
     * @param maxDistance Max distance in vanilla
     * @return Distance that should be used with current perspective
     */
    float getCameraDistance(float maxDistance);
    int getID();

    float getPlayerOpacity();
}
