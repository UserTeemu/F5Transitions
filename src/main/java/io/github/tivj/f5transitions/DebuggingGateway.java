package io.github.tivj.f5transitions;

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
            lines.add("Camera distance: " + transition.getCameraDistance(0F));
            lines.add("Y rotation: " + transition.getYRotationBonus(0F));
            lines.add("from -> to perspective: " + (transition.from == null ? "null" : transition.from.getID()) + " -> " + transition.to.getID());
            lines.add("Direction: "+(direction == null ? "null" : direction.toString()));
        }
        lines.add("Perspective: " + Minecraft.getMinecraft().gameSettings.thirdPersonView);
        return lines;
    }
}