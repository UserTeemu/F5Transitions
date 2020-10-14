package io.github.tivj.f5transitions.perspectives;

public class FrontPerspective implements Perspective {
    @Override
    public float getCameraYRotation() {
        return -180F;
    }

    @Override
    public float getDistanceMultiplier(float distance) {
        return -1F;
    }

    /**
     * Used to calculate the player's opacity only during transition to this perspective.
     */
    @Override
    public float getPlayerOpacity(float progress) {
        return 1F;
    }

    @Override
    public float getDefaultPlayerOpacity() {
        return 1F;
    }

    @Override
    public int getID() {
        return 2;
    }
}
