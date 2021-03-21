package io.github.tivj.f5transitions.compatibility;

import io.github.tivj.f5transitions.TransitionHelper;
import io.github.tivj.f5transitions.config.PerspectiveModCompatibilityOptions;
import io.github.tivj.f5transitions.perspectives.DJPerspectiveModPerspective;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

import static io.github.tivj.f5transitions.config.EaseProperty.ROTATION;

public class PerspectiveModTransitionHelper {
    private final TransitionHelper transitionHelper;

    public RotationModulusHandler transitionYawRotation = null;

    public PerspectiveModTransitionHelper(TransitionHelper transitionHelper) {
        this.transitionHelper = transitionHelper;
    }

    public void onPerspectiveChanged() {
        this.transitionYawRotation = null;
    }

    @SuppressWarnings("unused") // used in asm
    public float getMultipliedFacingValueForPerspectiveMod(float value, Entity entity, boolean isYaw, boolean isPreviousValue) {
        if (!transitionHelper.isTransitionActive()) return value;

        float normalValue = getNormalValue(entity, isYaw, isPreviousValue);
        float progress = this.transitionHelper.getProgressOfMaxWithPartialTicks(Minecraft.getMinecraft().timer.renderPartialTicks);
        if (this.transitionHelper.from instanceof DJPerspectiveModPerspective) {
            progress = 1F - progress;
        }

        float difference = value - normalValue;

        if (isYaw && (PerspectiveModCompatibilityOptions.INSTANCE.getRemoveFullTurns() || PerspectiveModCompatibilityOptions.INSTANCE.getRotateShortestDirection())) { // Pitch is capped at -90 degrees and +90 degrees so these operations are useless for it.
            if (transitionYawRotation == null) { // recalculates the number of full rotations from the current (not previous) value
                transitionYawRotation = new RotationModulusHandler(value - getNormalValue(entity, true, false));
            }
            difference -= transitionYawRotation.getFullRotations() * 360F;
        }

        return normalValue + (difference * ROTATION.getValue(progress));
    }

    private float getNormalValue(Entity entity, boolean isYaw, boolean isPreviousValue) {
        if (isYaw) {
            if (isPreviousValue) return entity.prevRotationYaw;
            else return entity.rotationYaw;
        } else {
            if (isPreviousValue) return entity.prevRotationPitch;
            else return entity.rotationPitch;
        }
    }

    public static class RotationModulusHandler {
        private int fullRotations = 0;

        public RotationModulusHandler(float input) {
            if (PerspectiveModCompatibilityOptions.INSTANCE.getRemoveFullTurns()) {
                fullRotations = (int)(input / 360F);
            }

            if (PerspectiveModCompatibilityOptions.INSTANCE.getRotateShortestDirection()) {
                if (input >= 180F) fullRotations++;
                else if (-input >= 180F) fullRotations--;
            }
        }

        public int getFullRotations() {
            return fullRotations;
        }
    }
}
