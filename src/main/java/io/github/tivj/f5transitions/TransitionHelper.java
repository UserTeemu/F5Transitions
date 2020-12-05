package io.github.tivj.f5transitions;

import io.github.tivj.f5transitions.config.TransitionsConfig;
import io.github.tivj.f5transitions.perspectives.BehindPlayerPerspective;
import io.github.tivj.f5transitions.perspectives.FirstPersonPerspective;
import io.github.tivj.f5transitions.perspectives.FrontPerspective;
import io.github.tivj.f5transitions.perspectives.Perspective;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

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
    public double getCameraDistance(float partialTicks) {
        float toCameraDistance = to.getCameraDistance(this.entityRenderer.thirdPersonDistance);
        if (from == null || !transitionActive) return toCameraDistance;
        else {
            float fromCameraDistance = from.getCameraDistance(this.entityRenderer.thirdPersonDistance);
            return fromCameraDistance + (easeClamped((progress + (partialTicks * TransitionsConfig.perspectiveTimerIncreaseValuePerTick)) / TransitionsConfig.maxPerpectiveTimer) * (toCameraDistance - fromCameraDistance));
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

    @SuppressWarnings("unused") // used in asm
    public double ensureGoodDistance(double currentDistance, Vec3 cameraPos) {
        double smallestDistance = 4D;
        for (int iterationX = -1; iterationX <= 1; iterationX++) {
            for (int iterationY = -1; iterationY <= 1; iterationY++) {
                for (int iterationZ = -1; iterationZ <= 1; iterationZ++) {
                    BlockPos pos = new BlockPos(cameraPos.addVector(iterationX, iterationY + 0.05D, iterationZ));
                    IBlockState state = mc.theWorld.getBlockState(pos);
                    Block block = state.getBlock();
                    if (!block.equals(Blocks.air)) {
                        AxisAlignedBB boundingBox = block.getCollisionBoundingBox(mc.theWorld, pos, state);
                        if (boundingBox != null) {
                            // distance calculation stolen from https://stackoverflow.com/a/18157551
                            double dx = Math.max(Math.max(boundingBox.minX - cameraPos.xCoord, cameraPos.xCoord - boundingBox.maxX), 0);
                            double dy = Math.max(Math.max(boundingBox.minY - cameraPos.yCoord, cameraPos.yCoord - boundingBox.maxY), 0);
                            double dz = Math.max(Math.max(boundingBox.minZ - cameraPos.zCoord, cameraPos.zCoord - boundingBox.maxZ), 0);
                            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
                            if (distance < smallestDistance) smallestDistance = distance;
                        }
                    }
                }
            }
        }
        if (smallestDistance <= 0.05D) {
            currentDistance -= smallestDistance - 0.05D;
        }
        return currentDistance;
    }
}
