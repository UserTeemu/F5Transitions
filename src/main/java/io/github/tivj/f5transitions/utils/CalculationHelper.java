package io.github.tivj.f5transitions.utils;

import io.github.tivj.f5transitions.config.TransitionsConfig;
import net.minecraft.util.MathHelper;

public class CalculationHelper {
    public static float ease(float input) {
        return TransitionsConfig.easingMethod.ease(input);
    }

    public static float easeClamped(float input) {
        return ease(MathHelper.clamp_float(input, 0F, 1F));
    }

    public static float smoothen(float previousValue, float unsmoothenedValue, float partialTicks) {
        return previousValue + (unsmoothenedValue - previousValue) * partialTicks;
    }
}
