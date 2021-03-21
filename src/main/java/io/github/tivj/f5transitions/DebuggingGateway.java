package io.github.tivj.f5transitions;

import io.github.tivj.f5transitions.config.TransitionsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DebuggingGateway {
    public static TransitionHelper transition;

    public static EnumFacing direction = null;
    public static Collection<String> debugString() {
        List<String> lines = new ArrayList<>();
        if (transition != null) {
            lines.add("Transition ongoing: " + transition.transitionActive);
            lines.add("Progress: " + transition.progress + " / " + TransitionsConfig.INSTANCE.getMaxPerpectiveTimer());
            lines.add("Camera distance: " + transition.getCameraDistance(0F));
            lines.add("Player opacity: " + transition.getPlayerOpacity());
            lines.add("Y rotation: " + transition.getYRotationBonus(0F));
            lines.add("from -> to perspective: " + (transition.from == null ? "null" : transition.from.getID()) + " -> " + transition.to.getID());
            lines.add("from -> to perspective Y: " + (transition.from == null ? "null" : transition.from.getCameraYRotation()) + " -> " + transition.to.getCameraYRotation());
            lines.add("Direction: "+(direction == null ? "null" : direction.toString()));
        }
        lines.add("Perspective: " + Minecraft.getMinecraft().gameSettings.thirdPersonView + (transition == null ? "" : " (" + transition.to.getID() + ")"));
        return lines;
    }
}