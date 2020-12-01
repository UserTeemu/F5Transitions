package io.github.tivj.f5transitions.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class GeneralFunctions {
    public static AbstractInsnNode updatePerspectiveTransitions() {
        return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "io/github/tivj/f5transitions/TransitionHelper", "updatePerspectiveTimer", "()V", false);
    }

    public static AbstractInsnNode getCameraDistance() {
        return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "io/github/tivj/f5transitions/TransitionHelper", "getCameraDistance", "(F)F", false);
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
