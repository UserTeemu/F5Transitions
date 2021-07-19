package dev.userteemu.f5transitions.perspectives;

import club.sk1er.patcher.config.PatcherConfig;
import dev.userteemu.f5transitions.compatibility.CompatibilityHelper;

public class FirstPersonPerspective implements Perspective {
    @Override
    public float getCameraYRotation() {
        return 0F;
    }

    @Override
    public float getCameraDistance(float maxDistance) {
        return -(CompatibilityHelper.isPatcherLoaded && PatcherConfig.parallaxFix ? 0.05F : -0.1F); // in 1st person the camera will be 0.1 blocks behind the player due to EntityRenderer's line 689, Sk1er Patcher changes this to 0.05
    }

    @Override
    public float getPlayerOpacity() {
        return 0F;
    }

    @Override
    public int getID() {
        return 0;
    }
}
