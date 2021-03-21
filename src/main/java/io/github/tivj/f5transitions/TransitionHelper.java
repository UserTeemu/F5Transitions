package io.github.tivj.f5transitions;

import io.github.tivj.f5transitions.compatibility.PerspectiveModTransitionHelper;
import io.github.tivj.f5transitions.config.TransitionsConfig;
import io.github.tivj.f5transitions.perspectives.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.util.MathHelper;

import java.util.ArrayList;
import java.util.List;

import static io.github.tivj.f5transitions.config.EaseProperty.*;

public class TransitionHelper {
    public boolean transitionActive = false;
    public float progress = 0F;
    public float progressOfMax = 0F;

    public Perspective from;
    public Perspective to;
    private final List<Runnable> transitionFinishCallbacks = new ArrayList<>();

    public final Minecraft mc;
    private final EntityRenderer entityRenderer;
    public final PerspectiveModTransitionHelper perspectiveModTransitionHelper;

    public TransitionHelper(EntityRenderer entityRenderer, Minecraft mc) {
        this.entityRenderer = entityRenderer;
        this.mc = mc;
        this.to = getPerspectiveFromID(mc.gameSettings.thirdPersonView);
        DebuggingGateway.transition = this;
        this.perspectiveModTransitionHelper = new PerspectiveModTransitionHelper(this);
    }

    private static final Perspective[] perspectives = new Perspective[]{new FirstPersonPerspective(), new BehindPlayerPerspective(), new FrontPerspective(), new DJPerspectiveModPerspective()};
    public static Perspective getPerspectiveFromID(int id) {
        return perspectives[id];
    }

    @SuppressWarnings("unused") // used in asm
    public void changePerspective(Perspective to, boolean transitionToPerspective) {
        if (this.to.equals(to)) return;

        if (this.transitionActive) {
            this.callFinishCallback();
        }

        if (transitionToPerspective) {
            this.from = this.to;
            this.progress = 0F;
            this.progressOfMax = 0F;
        } else {
            this.from = null;
            this.progress = TransitionsConfig.INSTANCE.getMaxPerpectiveTimer();
            this.progressOfMax = 1F;
        }

        this.transitionActive = transitionToPerspective;
        this.to = to;

        this.perspectiveModTransitionHelper.onPerspectiveChanged();
    }

    @SuppressWarnings("unused") // used in asm
    public void setTransitionFinishCallback(Runnable callback) {
        transitionFinishCallbacks.add(callback);
    }

    public float getProgressOfMaxWithPartialTicks(float partialTicks) {
        return MathHelper.clamp_float((progress + partialTicks) / TransitionsConfig.INSTANCE.getMaxPerpectiveTimer(), 0F, 1F);
    }

    @SuppressWarnings("unused") // used in asm
    public void updatePerspectiveTimer() {
        if (transitionActive) {
            progress++;
            if (progress > TransitionsConfig.INSTANCE.getMaxPerpectiveTimer()) {
                transitionActive = false;
                progress = TransitionsConfig.INSTANCE.getMaxPerpectiveTimer();
                progressOfMax = 1F;
                callFinishCallback();
            } else {
                progressOfMax = progress / TransitionsConfig.INSTANCE.getMaxPerpectiveTimer();
            }
            this.mc.renderGlobal.setDisplayListEntitiesDirty();
        }
    }

    private void callFinishCallback() {
        for (Runnable callback : transitionFinishCallbacks) callback.run();
        transitionFinishCallbacks.clear();
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
            float toYRotation = this.to.getCameraYRotation() * (TransitionsConfig.INSTANCE.getRotateCameraClockwise() ? -1F : 1F);
            float fromYRotation = this.from.getCameraYRotation() * (TransitionsConfig.INSTANCE.getRotateCameraClockwise() ? -1F : 1F);
            if (TransitionsConfig.INSTANCE.getSameCameraRotationDirection()) {
                if (TransitionsConfig.INSTANCE.getRotateCameraClockwise()) {
                    toYRotation += 360F;
                    while (toYRotation - fromYRotation >= 360F) {
                        toYRotation -= 360F;
                    }
                } else {
                    toYRotation -= 360F;
                    while (toYRotation - fromYRotation <= -360F) {
                        toYRotation += 360F;
                    }
                }
            }

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
        if (this.to instanceof FirstPersonPerspective && (this.from instanceof FrontPerspective || this.from instanceof FirstPersonPerspective)) {
            return this.getPlayerOpacity() > TransitionsConfig.INSTANCE.getThirdPersonItemHidePoint();
        } else return true;
    }

    @SuppressWarnings("unused") // used in asm
    public boolean shouldRenderFirstPersonHand() {
        if (this.to instanceof FirstPersonPerspective) {
            if (this.from instanceof BehindPlayerPerspective || this.from instanceof DJPerspectiveModPerspective) return !transitionActive;
            else return true;
        }
        return false;
    }

    @SuppressWarnings("unused") // used in asm
    public boolean shouldRenderArrowLayer() {
        return this.getPlayerOpacity() > TransitionsConfig.INSTANCE.getArrowLayerHidePoint();
    }
}
