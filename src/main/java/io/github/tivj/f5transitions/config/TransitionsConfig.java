package io.github.tivj.f5transitions.config;

import io.github.tivj.f5transitions.ease.EaseInOutQuad;
import io.github.tivj.f5transitions.ease.IEase;
import net.minecraft.block.*;

public class TransitionsConfig {
    public static final float customHeadHide = 0.6F; // this is the player's opacity when will start to be rendered solid.
    public static final float thirdPersonItemHide = 0.4F; // this is the player's held item should be hidden when going to 1st person view.
    public static float playerSolidnessPoint = 0.6F; // this is the player's opacity when will start to be rendered solid.
    public static float maxPerpectiveTimer = 16F;
    public static float perspectiveTimerIncreaseValuePerTick = 1F; // amount that the perspective timer increases per tick
    public static boolean rotateCameraToLeft = true; // chooses the direction camera rotates to when going to front perspective, also affects transition away from it
    public static boolean continuousRotation = true; // always rotate to the same direction

    public static IEase easingMethod = new EaseInOutQuad();

    public static boolean cameraCanGoThroughUncollidableBlocks = false;
    public static boolean cameraCanGoThroughCobwebs = false;
    public static boolean cameraCanGoThroughStrings = false;
    public static boolean cameraCanGoThroughPortals = false;
    public static boolean cameraCanGoThroughCrops = false;
    public static boolean cameraCanGoThroughVines = false;
    public static boolean cameraCanGoThroughStems = false;
    public static boolean cameraCanGoThroughSugarCanes = false;
    public static boolean cameraCanGoThroughBanners = false;
    public static boolean cameraCanGoThroughTorches = false;
    public static boolean cameraCanGoThroughRedstoneDust = false;
    public static boolean cameraCanGoThroughSigns = false;
    public static boolean cameraCanGoThroughBarrierBlocks = false;
    public static boolean cameraCanGoThroughPanes = false;
    public static boolean cameraCanGoThroughGlass = false;

    public static boolean cameraCanGoThroughBlock(Block block) {
        if (!cameraCanGoThroughUncollidableBlocks)                                  return false;
        if (cameraCanGoThroughCobwebs        && block instanceof BlockWeb)          return true;
        if (cameraCanGoThroughStrings        && block instanceof BlockTripWire)     return true;
        if (cameraCanGoThroughPortals        && block instanceof BlockPortal)       return true;
        if (cameraCanGoThroughCrops          && block instanceof BlockCrops)        return true;
        if (cameraCanGoThroughVines          && block instanceof BlockVine)         return true;
        if (cameraCanGoThroughStems          && block instanceof BlockStem)         return true;
        if (cameraCanGoThroughSugarCanes     && block instanceof BlockReed)         return true;
        if (cameraCanGoThroughBanners        && block instanceof BlockBanner)       return true;
        if (cameraCanGoThroughTorches        && block instanceof BlockTorch)        return true;
        if (cameraCanGoThroughRedstoneDust   && block instanceof BlockRedstoneWire) return true;
        if (cameraCanGoThroughSigns          && block instanceof BlockSign)         return true;
        if (cameraCanGoThroughBarrierBlocks  && block instanceof BlockBarrier)      return true;
        if (cameraCanGoThroughPanes          && block instanceof BlockPane)         return true;
        return cameraCanGoThroughGlass       && block instanceof BlockGlass;
    }
}
