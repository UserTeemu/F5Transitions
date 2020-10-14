package io.github.tivj.f5transitions;

import io.github.tivj.f5transitions.config.TransitionsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;

import static io.github.tivj.f5transitions.utils.CalculationHelper.ease;

@Deprecated
public class TransitionHelperOld {
    public float perspectiveSwitchTimer = TransitionsConfig.maxPerpectiveTimer;
    public float distanceMultiplier = 0F;
    public float playerOpacity = 1F;

    public int lastPerspective = 0;
    public float previousYRotationBonus = 0F;
    public float yRotationBonus = 0F;

    private Minecraft mc;

    public TransitionHelperOld(Minecraft mc) {
        this.mc = mc;
    }

    public void updatePerspectiveTimer(EntityRenderer entityRenderer) {
        if (perspectiveSwitchTimer <= TransitionsConfig.maxPerpectiveTimer) {
            perspectiveSwitchTimer = Math.min(perspectiveSwitchTimer, TransitionsConfig.maxPerpectiveTimer) + TransitionsConfig.perspectiveTimerIncreaseValuePerTick;

            if (perspectiveSwitchTimer == TransitionsConfig.maxPerpectiveTimer + TransitionsConfig.perspectiveTimerIncreaseValuePerTick) {
                distanceMultiplier = this.mc.gameSettings.thirdPersonView == 0 ? 0F : 1F; // helps when the f5 is changed next time
                if (this.mc.gameSettings.thirdPersonView == 2) distanceMultiplier = -1;

                entityRenderer.thirdPersonDistanceTemp = entityRenderer.thirdPersonDistance * distanceMultiplier;

                previousYRotationBonus = yRotationBonus;

            } else {
                entityRenderer.thirdPersonDistanceTemp = entityRenderer.thirdPersonDistance * distanceMultiplier;
                distanceMultiplier = ease(perspectiveSwitchTimer / TransitionsConfig.maxPerpectiveTimer);
                if (lastPerspective == 1 && this.mc.gameSettings.thirdPersonView == 2) {
                    distanceMultiplier = -(distanceMultiplier * 2F - 1F);
                } else if (lastPerspective == 2 && this.mc.gameSettings.thirdPersonView == 0) {
                    distanceMultiplier -= 1F;
                }

                if (this.mc.gameSettings.thirdPersonView == 1) {
                    playerOpacity = ease(perspectiveSwitchTimer / TransitionsConfig.maxPerpectiveTimer % 1F);
                } else if (lastPerspective == 2 && this.mc.gameSettings.thirdPersonView == 0) {
                    playerOpacity = ease(1F - (perspectiveSwitchTimer / TransitionsConfig.maxPerpectiveTimer % 1F));
                } else playerOpacity = 1F;

                previousYRotationBonus = yRotationBonus;
                yRotationBonus = calculateRawYRotationBonus();
            }
            this.mc.renderGlobal.setDisplayListEntitiesDirty();
        }
    }

    public float getYRotationBonus(float partialTicks) {
        return previousYRotationBonus + (yRotationBonus - previousYRotationBonus) * partialTicks;
    }

    private float calculateRawYRotationBonus() {
        if (lastPerspective == 1 && this.mc.gameSettings.thirdPersonView == 2) {
            return 180F * (ease(perspectiveSwitchTimer / TransitionsConfig.maxPerpectiveTimer));
        } else if (lastPerspective == 2 && this.mc.gameSettings.thirdPersonView == 0) {
            return -180F * (ease(perspectiveSwitchTimer / TransitionsConfig.maxPerpectiveTimer) - 1F);
        }
        else return 0F;
    }

    public void beforePerspectiveChanged() {
        perspectiveSwitchTimer = 0F;
        lastPerspective = this.mc.gameSettings.thirdPersonView;
        yRotationBonus = previousYRotationBonus = this.mc.gameSettings.thirdPersonView == 2 ? 180F : 0F;
    }

    public boolean shouldRenderCustomHead() {
        if (this.mc.gameSettings.thirdPersonView == 0 && lastPerspective == 2) {
            return this.playerOpacity > TransitionsConfig.customHeadHide;
        } else return true;
    }

    public boolean isPlayerNotRenderedSolid() {
        return this.playerOpacity < TransitionsConfig.playerSolidnessPoint;
    }

    public boolean shouldItemBeRenderedInThirdPerson() {
        if (this.lastPerspective == 2 || this.mc.gameSettings.thirdPersonView == 0) {
            return this.playerOpacity > TransitionsConfig.thirdPersonItemHide;
        } else return true;
    }
}
