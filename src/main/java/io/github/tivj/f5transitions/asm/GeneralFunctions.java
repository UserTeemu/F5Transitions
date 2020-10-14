package io.github.tivj.f5transitions.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class GeneralFunctions {
    public static AbstractInsnNode getPerspectiveSwitchTimer() {
        return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "io/github/tivj/f5transitions/TransitionHelper", "getProgress", "()F", false);
    }

    /**
     * Note: doesn't require an instance of TransitionHelper
     */
    public static AbstractInsnNode getMaxPerspectiveSwitchTimer() {
        return new FieldInsnNode(Opcodes.GETSTATIC, "io/github/tivj/f5transitions/config/TransitionsConfig", "maxPerpectiveTimer", "F");
    }

    public static AbstractInsnNode updatePerspectiveTransitions() {
        return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "io/github/tivj/f5transitions/TransitionHelper", "updatePerspectiveTimer", "()V", false);
    }

    public static AbstractInsnNode getLastPerspective() {
        return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "io/github/tivj/f5transitions/TransitionHelper", "getLastPerspective", "()I", false);
    }

    public static AbstractInsnNode getCameraDistanceMultiplier() {
        return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "io/github/tivj/f5transitions/TransitionHelper", "getDistanceMultiplier", "(F)F", false);
    }

    public static AbstractInsnNode getYrotationBonus() {
        return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "io/github/tivj/f5transitions/TransitionHelper", "getYRotationBonus", "(F)F", false);
    }

    public static AbstractInsnNode shouldCustomHeadBeRendered() {
        return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "io/github/tivj/f5transitions/TransitionHelper", "shouldRenderCustomHead", "()Z", false);
    }

    public static AbstractInsnNode shouldHeldItemBeRenderedInThirdPerson() {
        return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "io/github/tivj/f5transitions/TransitionHelper", "shouldItemBeRenderedInThirdPerson", "()Z", false);
    }

    public static AbstractInsnNode beforePerspectiveChanges() {
        return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "io/github/tivj/f5transitions/TransitionHelper", "beforePerspectiveChanged", "()V", false);
    }

    public static AbstractInsnNode getPlayerOpacity() {
        return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "io/github/tivj/f5transitions/TransitionHelper", "getPlayerOpacity", "()F", false);
    }

    public static AbstractInsnNode isPlayerNotRenderedSolid() {
        return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "io/github/tivj/f5transitions/TransitionHelper", "isPlayerNotRenderedSolid", "()Z", false);
    }

    public static AbstractInsnNode isTransitionActive() {
        return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "io/github/tivj/f5transitions/TransitionHelper", "isTransitionActive", "()Z", false);
    }
}
