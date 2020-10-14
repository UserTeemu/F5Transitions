package io.github.tivj.f5transitions.config;

import io.github.tivj.f5transitions.ease.EaseInOutQuad;
import io.github.tivj.f5transitions.ease.IEase;

public class TransitionsConfig {
    public static final float customHeadHide = 0.6F; // this is the player's opacity when will start to be rendered solid.
    public static final float thirdPersonItemHide = 0.4F; // this is the player's held item should be hidden when going to 1st person view.
    public static float playerSolidnessPoint = 0.6F; // this is the player's opacity when will start to be rendered solid.
    public static float maxPerpectiveTimer = 16F;
    public static float perspectiveTimerIncreaseValuePerTick = 1F; // amount that the perspective timer increases per tick
    public static boolean rotateCameraToLeft = true; // chooses the direction camera rotates to when going to front perspective, also affects transition away from it
    public static boolean continuousRotation = true; // always rotate to the same direction

    public static IEase easingMethod = new EaseInOutQuad();
}
