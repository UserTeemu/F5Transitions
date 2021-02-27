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

import static io.github.tivj.f5transitions.TransitionPhase.FROM;
import static io.github.tivj.f5transitions.config.EaseProperty.*;

public class TransitionHelper {
    private static final HashSet<Perspective> perspectives = new HashSet<>();

    private static void initPerspectives() {
        perspectives.add(new FirstPersonPerspective());
        perspectives.add(new BehindPlayerPerspective());
        perspectives.add(new FrontPerspective());
    }

    public Perspective from;
    public Perspective to;
    public float progress = 0F;
    public float progressOfMax = 0F;
    public boolean transitionActive = false;

    private final Minecraft mc;
    private final EntityRenderer entityRenderer;

    public TransitionHelper(EntityRenderer entityRenderer, Minecraft mc) {
        this.entityRenderer = entityRenderer;
        this.mc = mc;
        if (perspectives.size() == 0) initPerspectives();
        this.to = getPerspectiveFromID(mc.gameSettings.thirdPersonView);

        DebuggingGateway.transition = this;
    }

    public static Perspective getPerspectiveFromID(int id) {
        if (id > 2 || id < 0) id = 0;
        for (Perspective perspective : perspectives) {
            if (perspective.getID() == id) return perspective;
        }
        throw new IllegalStateException("Perspectives have not been initialized correctly! ID "+id+" was not found!");
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
        } else {
            this.from = null;
            this.progress = TransitionsConfig.INSTANCE.getMaxPerpectiveTimer();
            this.progressOfMax = 1F;
        }

        this.transitionActive = transitionToPerspective;
        this.to = to;
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
            return fromCameraDistance + (DISTANCE.getValue(MathHelper.clamp_float((progress + partialTicks) / TransitionsConfig.INSTANCE.getMaxPerpectiveTimer(), 0F, 1F)) * (toCameraDistance - fromCameraDistance));
        }
    }

    @SuppressWarnings("unused") // used in asm
    public float getYRotationBonus(float partialTicks) {
        if (from == null || !transitionActive) return to.getCameraYRotation(TransitionPhase.NO_TRANSITION);
        else return from.getCameraYRotation(FROM) + (ROTATION.getValue(MathHelper.clamp_float((progress + partialTicks) / TransitionsConfig.INSTANCE.getMaxPerpectiveTimer(), 0F, 1F)) * (to.getCameraYRotation(TransitionPhase.TO) - from.getCameraYRotation(FROM)));
    }

    @SuppressWarnings("unused") // used in asm
    public float getPlayerOpacity() {
        if (from == null || !transitionActive) return 1F;
        else return this.to.getPlayerOpacity(progressOfMax);
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
    public boolean shouldRenderArrowLayer() {
        return this.getPlayerOpacity() > TransitionsConfig.INSTANCE.getArrowLayerHidePoint();
    }
}
