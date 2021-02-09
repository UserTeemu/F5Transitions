package io.github.tivj.f5transitions.perspectives;

import club.sk1er.patcher.config.PatcherConfig;
import io.github.tivj.f5transitions.TransitionPhase;
import io.github.tivj.f5transitions.TransitionsMod;
import io.github.tivj.f5transitions.config.TransitionsConfig;

import static io.github.tivj.f5transitions.config.EaseProperty.OPACITY;

public class FirstPersonPerspective implements Perspective {
    @Override
    public float getCameraYRotation(TransitionPhase transitionPhase) {
        if (TransitionsConfig.INSTANCE.getSameCameraRotationDirection() && transitionPhase == TransitionPhase.TO) return TransitionsConfig.INSTANCE.getRotateCameraClockwise() ? 360F : -360F;
        else return 0F;
    }

    @Override
    public float getCameraDistance(float maxDistance) {
        return -(TransitionsMod.patcherLoadedInClasspath && PatcherConfig.parallaxFix ? 0.05F : -0.1F); // in 1st person the camera will be 0.1 blocks behind the player due to EntityRenderer's line 689, Sk1er Patcher changes this to 0.05
    }

    /**
     * Used to calculate the player's opacity only during transition to this perspective.
     */
    @Override
    public float getPlayerOpacity(float progress) {
        return OPACITY.getValue(1F - progress);
    }

    @Override
    public int getID() {
        return 0;
    }
}
