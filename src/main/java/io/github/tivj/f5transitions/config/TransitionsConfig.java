package io.github.tivj.f5transitions.config;

import club.sk1er.elementa.constraints.animation.Animations;
import club.sk1er.vigilance.Vigilant;
import club.sk1er.vigilance.data.Property;
import club.sk1er.vigilance.data.PropertyType;
import net.minecraft.block.*;

import java.io.File;

public class TransitionsConfig extends Vigilant {
    public TransitionsConfig() {
        super(new File("./config/f5transitions.toml"));
        initialize();
        AnimationEasingConfiguration.setupAnimationEasingProperties(this);
    }

    @Property(
            type = PropertyType.DECIMAL_SLIDER,
            name = "Animation length",
            description = "in client ticks",
            maxF = 1000, decimalPlaces = 3,
            category = "Animation", subcategory = "Animation"
    )
    public static float maxPerpectiveTimer = 16F;

    @Property(
            type = PropertyType.SWITCH,
            name = "Rotate camera clockwise",
            category = "Animation", subcategory = "Animation"
    )
    public static boolean rotateCameraClockwise = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Camera always rotates to the same direction",
            category = "Animation", subcategory = "Animation"
    )
    public static boolean sameCameraRotationDirection = true;

    @Deprecated // because it should not be used, use AnimationEasingConfiguration$EaseUse instead
    @SuppressWarnings("unused") // used as the property in Vigilance
    public static int rotationEasingMethodIndex = Animations.IN_OUT_QUAD.ordinal();

    @Deprecated // because it should not be used, use AnimationEasingConfiguration$EaseUse instead
    @SuppressWarnings("unused") // used as the property in Vigilance
    public static int distanceEasingMethodIndex = Animations.IN_OUT_QUAD.ordinal();

    @Deprecated // because it should not be used, use AnimationEasingConfiguration$EaseUse instead
    @SuppressWarnings("unused") // used as the property in Vigilance
    public static int opacityEasingMethodIndex = Animations.IN_OUT_QUAD.ordinal();

    @Property(
            type = PropertyType.PERCENT_SLIDER,
            name = "Player solidness point",
            description = "The lowest player opacity value when the player will be rendered solid",
            category = "Animation", subcategory = "Animation details - Probably not wise to change unless you need to"
    )
    public static float playerSolidnessPoint = 0.6F;

    @Property(
            type = PropertyType.PERCENT_SLIDER,
            name = "Item invisibility point",
            description = "When going to 1st person view, the highest player opacity value when the item player is holding can be rendered",
            category = "Animation", subcategory = "Animation details - Probably not wise to change unless you need to"
    )
    public static float thirdPersonItemHide = 0.4F;

    @Property(
            type = PropertyType.PERCENT_SLIDER,
            name = "Arrows on player invisibility point",
            description = "When going to 1st person view, the lowest player opacity value when arrows on the player can be seen",
            category = "Animation", subcategory = "Animation details - Probably not wise to change unless you need to"
    )
    public static float arrowLayerHide = 0.18F;

    @Property(
            type = PropertyType.SWITCH,
            name = "Change blocks that camera can collide with",
            category = "Collisions", subcategory = "Master toggle"
    )
    public static boolean changeCollidableBlocks = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Cobwebs",
            category = "Collisions", subcategory = "Camera can go through..."
    )
    public static boolean cameraCanGoThroughCobwebs = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Strings",
            category = "Collisions", subcategory = "Camera can go through..."
    )
    public static boolean cameraCanGoThroughStrings = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Portals",
            category = "Collisions", subcategory = "Camera can go through..."
    )
    public static boolean cameraCanGoThroughPortals = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Crops",
            category = "Collisions", subcategory = "Camera can go through..."
    )
    public static boolean cameraCanGoThroughCrops = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Vines",
            category = "Collisions", subcategory = "Camera can go through..."
    )
    public static boolean cameraCanGoThroughVines = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Stems",
            category = "Collisions", subcategory = "Camera can go through..."
    )
    public static boolean cameraCanGoThroughStems = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Sugar canes",
            category = "Collisions", subcategory = "Camera can go through..."
    )
    public static boolean cameraCanGoThroughSugarCanes = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Banners",
            category = "Collisions", subcategory = "Camera can go through..."
    )
    public static boolean cameraCanGoThroughBanners = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Torches",
            category = "Collisions", subcategory = "Camera can go through..."
    )
    public static boolean cameraCanGoThroughTorches = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Redstone dust",
            category = "Collisions", subcategory = "Camera can go through..."
    )
    public static boolean cameraCanGoThroughRedstoneDust = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Signs",
            category = "Collisions", subcategory = "Camera can go through..."
    )
    public static boolean cameraCanGoThroughSigns = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Barrier blocks",
            category = "Collisions", subcategory = "Camera can go through..."
    )
    public static boolean cameraCanGoThroughBarrierBlocks = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Panes",
            category = "Collisions", subcategory = "Camera can go through..."
    )
    public static boolean cameraCanGoThroughPanes = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Glass",
            category = "Collisions", subcategory = "Camera can go through..."
    )
    public static boolean cameraCanGoThroughGlass = false;

    public static boolean cameraCanGoThroughBlock(Block block) {
        if (!changeCollidableBlocks)                                  return false;
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
