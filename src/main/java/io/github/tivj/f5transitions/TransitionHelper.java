package io.github.tivj.f5transitions;

import io.github.tivj.f5transitions.config.TransitionsConfig;
import io.github.tivj.f5transitions.perspectives.BehindPlayerPerspective;
import io.github.tivj.f5transitions.perspectives.FirstPersonPerspective;
import io.github.tivj.f5transitions.perspectives.FrontPerspective;
import io.github.tivj.f5transitions.perspectives.Perspective;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.util.MathHelper;

import java.util.HashSet;

import static io.github.tivj.f5transitions.config.EaseProperty.*;

public class TransitionHelper {
    public boolean transitionActive = false;
    public float progress = 0F;
    public float progressOfMax = 0F;

    public Perspective from;
    public Perspective to;
    private float fromYRotation; // holds a pre-calculated y rotation value to use in transitions
    private float toYRotation; // holds a pre-calculated y rotation value to use in transitions

    private final Minecraft mc;
    private final EntityRenderer entityRenderer;

    public TransitionHelper(EntityRenderer entityRenderer, Minecraft mc) {
        this.entityRenderer = entityRenderer;
        this.mc = mc;
        this.to = getPerspectiveFromID(mc.gameSettings.thirdPersonView);
        DebuggingGateway.transition = this;
    }

    private static final Perspective[] perspectives = new Perspective[]{new FirstPersonPerspective(), new BehindPlayerPerspective(), new FrontPerspective()};
    public static Perspective getPerspectiveFromID(int id) {
        if (id > 2 || id < 0) id = 0;
        return perspectives[id];
    }

    @SuppressWarnings("unused") // used in asm
    public void beforePerspectiveChanged() {
        this.changePerspective(getPerspectiveFromID(this.mc.gameSettings.thirdPersonView+1), true);
    }

    public void changePerspective(Perspective to, boolean transitionToPerspective) {
        if (transitionToPerspective) {
            this.from = this.to;
            this.progress = 0F;
            this.progressOfMax = 0F;
            calculateYRotations(this.from, to);
        } else {
            this.from = null;
            this.progress = TransitionsConfig.INSTANCE.getMaxPerpectiveTimer();
            this.progressOfMax = 1F;
        }

        this.transitionActive = transitionToPerspective;
        this.to = to;
    }

    private void calculateYRotations(Perspective from, Perspective to) {
        this.toYRotation = to.getCameraYRotation() * (TransitionsConfig.INSTANCE.getRotateCameraClockwise() ? -1F : 1F);
        this.fromYRotation = from.getCameraYRotation() * (TransitionsConfig.INSTANCE.getRotateCameraClockwise() ? -1F : 1F);
        if (TransitionsConfig.INSTANCE.getSameCameraRotationDirection()) {
            if (TransitionsConfig.INSTANCE.getRotateCameraClockwise()) {
                this.toYRotation += 360F;
                while (this.toYRotation - this.fromYRotation >= 360F) {
                    this.toYRotation -= 360F;
                }
            } else {
                this.toYRotation -= 360F;
                while (this.toYRotation - this.fromYRotation <= -360F) {
                    this.toYRotation += 360F;
                }
            }
        }
    }

    private float getProgressOfMaxWithPartialTicks(float partialTicks) {
        return MathHelper.clamp_float((progress + partialTicks) / TransitionsConfig.INSTANCE.getMaxPerpectiveTimer(), 0F, 1F);
    }

    @SuppressWarnings("unused") // used in asm
    public void updatePerspectiveTimer() {
        if (transitionActive) {
            progress++;
            if (progress >= TransitionsConfig.INSTANCE.getMaxPerpectiveTimer() + 1F) {
                transitionActive = false;
                progress = TransitionsConfig.INSTANCE.getMaxPerpectiveTimer();
                progressOfMax = 1F;
            } else {
                progressOfMax = progress / TransitionsConfig.INSTANCE.getMaxPerpectiveTimer();
            }
            this.mc.renderGlobal.setDisplayListEntitiesDirty();
        }
    }

    @SuppressWarnings("unused") // used in asm
    public double getCameraDistance(float partialTicks) {
        float toCameraDistance = to.getCameraDistance(this.entityRenderer.thirdPersonDistance);
        if (from == null || !transitionActive) return toCameraDistance;
        else {
            float fromCameraDistance = from.getCameraDistance(this.entityRenderer.thirdPersonDistance);
            return fromCameraDistance + (DISTANCE.getValue(getProgressOfMaxWithPartialTicks(partialTicks)) * (toCameraDistance - fromCameraDistance));
        }
    }

    @SuppressWarnings("unused") // used in asm
    public float getYRotationBonus(float partialTicks) {
        if (from == null || !transitionActive) return this.to.getCameraYRotation();
        else {
            return fromYRotation + (ROTATION.getValue(getProgressOfMaxWithPartialTicks(partialTicks)) * (toYRotation - fromYRotation));
        }
    }

    @SuppressWarnings("unused") // used in asm
    public float getPlayerOpacity() {
        if (from == null || !transitionActive) return this.to.getPlayerOpacity();
        else return this.from.getPlayerOpacity() + OPACITY.getValue(progressOfMax) * (this.to.getPlayerOpacity() - this.from.getPlayerOpacity());
    }

    @SuppressWarnings("unused") // used in asm
    public boolean shouldDisableDepthMask() {
        return this.getPlayerOpacity() < TransitionsConfig.INSTANCE.getPlayerSolidnessPoint();
    }

    @SuppressWarnings("unused") // used in asm
    public float getArmorOpacity() {
        return getPlayerOpacity();
    }

    @SuppressWarnings("unused") // used in asm
    public boolean isTransitionActive() {
        return this.transitionActive;
    }

    @SuppressWarnings("unused") // used in asm
    public boolean shouldItemBeRenderedInThirdPerson() {
        if (this.to instanceof FirstPersonPerspective && this.from instanceof FrontPerspective) {
            return this.getPlayerOpacity() > TransitionsConfig.INSTANCE.getThirdPersonItemHidePoint();
        } else return true;
    }

    @SuppressWarnings("unused") // used in asm
    public boolean shouldRenderFirstPersonHand() {
        if (this.to instanceof FirstPersonPerspective) {
            if (this.from instanceof BehindPlayerPerspective) return !transitionActive;
            else return true;
        }
        return false;
    }

    @SuppressWarnings("unused") // used in asm
    public boolean shouldRenderArrowLayer() {
        return this.getPlayerOpacity() > TransitionsConfig.INSTANCE.getArrowLayerHidePoint();
    }
}
