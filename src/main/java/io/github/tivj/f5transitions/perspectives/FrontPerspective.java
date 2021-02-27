package io.github.tivj.f5transitions.perspectives;

public class FrontPerspective implements Perspective {
    @Override
    public float getCameraYRotation() {
        return 180F;
    }

    @Override
    public float getCameraDistance(float maxDistance) {
        return -maxDistance;
    }

    @Override
    public float getPlayerOpacity() {
        return 1F;
    }

    @Override
    public int getID() {
        return 2;
    }
}
