package io.github.tivj.f5transitions;

import io.github.tivj.f5transitions.config.TransitionsConfig;
import io.github.tivj.f5transitions.perspectives.BehindPlayerPerspective;
import io.github.tivj.f5transitions.perspectives.FirstPersonPerspective;
import io.github.tivj.f5transitions.perspectives.FrontPerspective;
import io.github.tivj.f5transitions.perspectives.Perspective;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;

import java.util.HashSet;

import static io.github.tivj.f5transitions.TransitionPhase.FROM;
import static io.github.tivj.f5transitions.utils.CalculationHelper.easeClamped;

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

    private static Perspective getPerspectiveFromID(int id) {
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
            this.progress = TransitionsConfig.maxPerpectiveTimer;
            this.progressOfMax = 1F;
        }

        this.transitionActive = transitionToPerspective;
        this.to = to;
    }

    @SuppressWarnings("unused") // used in asm
    public void updatePerspectiveTimer() {
        if (transitionActive) {
            progress += TransitionsConfig.perspectiveTimerIncreaseValuePerTick;
            if (progress >= TransitionsConfig.maxPerpectiveTimer + TransitionsConfig.perspectiveTimerIncreaseValuePerTick) {
                transitionActive = false;
                progress = TransitionsConfig.maxPerpectiveTimer;
                progressOfMax = 1F;
            } else {
                progressOfMax = progress / TransitionsConfig.maxPerpectiveTimer;
            }
            this.mc.renderGlobal.setDisplayListEntitiesDirty();
        }
    }

    @SuppressWarnings("unused") // used in asm
    public float getDistanceMultiplier(float partialTicks) {
        float toDistanceMultiplier = to.getDistanceMultiplier(this.entityRenderer.thirdPersonDistance);
        if (from == null || !transitionActive) return toDistanceMultiplier;
        else {
            float fromDistanceMultiplier = from.getDistanceMultiplier(this.entityRenderer.thirdPersonDistance);
            return fromDistanceMultiplier + (easeClamped((progress + (partialTicks * TransitionsConfig.perspectiveTimerIncreaseValuePerTick)) / TransitionsConfig.maxPerpectiveTimer) * (toDistanceMultiplier - fromDistanceMultiplier));
        }
    }

    @SuppressWarnings("unused") // used in asm
    public float getYRotationBonus(float partialTicks) {
        if (from == null || !transitionActive) return to.getCameraYRotation(TransitionPhase.NO_TRANSITION);
        else return from.getCameraYRotation(FROM) + (easeClamped((progress + (partialTicks * TransitionsConfig.perspectiveTimerIncreaseValuePerTick)) / TransitionsConfig.maxPerpectiveTimer) * (to.getCameraYRotation(TransitionPhase.TO) - from.getCameraYRotation(FROM)));
    }

    @SuppressWarnings("unused") // used in asm
    public float getPlayerOpacity() {
        if (from == null || !transitionActive) return 1F;
        else return this.to.getPlayerOpacity(progressOfMax);
    }

    @SuppressWarnings("unused") // used in asm
    public boolean isTransitionActive() {
        return this.transitionActive;
    }

    @SuppressWarnings("unused") // used in asm
    public boolean shouldRenderCustomHead() {
        if (this.to instanceof FirstPersonPerspective && this.from instanceof FrontPerspective) {
            return this.getPlayerOpacity() > TransitionsConfig.customHeadHide;
        } else return true;
    }

    @SuppressWarnings("unused") // used in asm
    public boolean isPlayerNotRenderedSolid() {
        return this.getPlayerOpacity() < TransitionsConfig.playerSolidnessPoint;
    }

    @SuppressWarnings("unused") // used in asm
    public boolean shouldItemBeRenderedInThirdPerson() {
        if (this.to instanceof FirstPersonPerspective && this.from instanceof FrontPerspective) {
            return this.getPlayerOpacity() > TransitionsConfig.thirdPersonItemHide;
        } else return true;
    }
}
