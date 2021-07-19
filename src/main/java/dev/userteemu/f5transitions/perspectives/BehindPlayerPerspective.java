package dev.userteemu.f5transitions.perspectives;

public class BehindPlayerPerspective implements Perspective {
    @Override
    public float getCameraYRotation() {
        return 0F;
    }

    @Override
    public float getCameraDistance(float maxDistance) {
        return maxDistance;
    }

    @Override
    public float getPlayerOpacity() {
        return 1F;
    }

    @Override
    public int getID() {
        return 1;
    }
}
