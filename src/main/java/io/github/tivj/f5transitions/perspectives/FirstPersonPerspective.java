package io.github.tivj.f5transitions.perspectives;

import club.sk1er.patcher.config.PatcherConfig;
import io.github.tivj.f5transitions.TransitionsMod;

import static io.github.tivj.f5transitions.utils.CalculationHelper.ease;

public class FirstPersonPerspective implements Perspective {
    @Override
    public float getCameraYRotation() {
        return 0F;
    }

    @Override
    public float getDistanceMultiplier(float distance) {
        return (TransitionsMod.patcherLoadedInClasspath && PatcherConfig.parallaxFix ? 0.05F : 0.1F) / distance; // in 1st person the camera will be 0.1 blocks behind the player due to EntityRenderer's line 689
    }

    /**
     * Used to calculate the player's opacity only during transition to this perspective.
     */
    @Override
    public float getPlayerOpacity(float progress) {
        return ease(1F - progress);
    }

    @Override
    public float getDefaultPlayerOpacity() {
        return 0F;
    }

    @Override
    public int getID() {
        return 0;
    }
}
