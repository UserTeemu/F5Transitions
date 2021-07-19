package dev.userteemu.f5transitions.asm;

import dev.userteemu.f5transitions.asm.modifications.EntityRendererTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class CommonInstructions {
    public static AbstractInsnNode getPerspectiveModTransitionHelper() {
        return new FieldInsnNode(Opcodes.GETFIELD, "dev/userteemu/f5transitions/TransitionHelper", "perspectiveModTransitionHelper", "Ldev/userteemu/f5transitions/compatibility/PerspectiveModTransitionHelper;");
    }

    public static AbstractInsnNode getPerspectiveFromID() {
        return new MethodInsnNode(Opcodes.INVOKESTATIC, "dev/userteemu/f5transitions/TransitionHelper", "getPerspectiveFromID", "(I)Ldev/userteemu/f5transitions/perspectives/Perspective;", false);
    }

    public static AbstractInsnNode updatePerspectiveTransitions() {
        return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "dev/userteemu/f5transitions/TransitionHelper", "updatePerspectiveTimer", "()V", false);
    }

    public static AbstractInsnNode getCameraDistance() {
        return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "dev/userteemu/f5transitions/TransitionHelper", "getCameraDistance", "(F)D", false);
    }

    public static AbstractInsnNode getYrotationBonus() {
        return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "dev/userteemu/f5transitions/TransitionHelper", "getYRotationBonus", "(F)F", false);
    }

    public static AbstractInsnNode shouldHeldItemBeRenderedInThirdPerson() {
        return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "dev/userteemu/f5transitions/TransitionHelper", "shouldItemBeRenderedInThirdPerson", "()Z", false);
    }

    public static AbstractInsnNode shouldRenderArrowLayer() {
        return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "dev/userteemu/f5transitions/TransitionHelper", "shouldRenderArrowLayer", "()Z", false);
    }

    public static AbstractInsnNode changePerspective() {
        return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "dev/userteemu/f5transitions/TransitionHelper", "changePerspective", "(Ldev/userteemu/f5transitions/perspectives/Perspective;Z)V", false);
    }

    public static AbstractInsnNode setTransitionFinishCallback() {
        return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "dev/userteemu/f5transitions/TransitionHelper", "setTransitionFinishCallback", "(Ljava/lang/Runnable;)V", false);
    }

    public static AbstractInsnNode getPlayerOpacity() {
        return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "dev/userteemu/f5transitions/TransitionHelper", "getPlayerOpacity", "()F", false);
    }

    public static AbstractInsnNode shouldDisableDepthMask() {
        return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "dev/userteemu/f5transitions/TransitionHelper", "shouldDisableDepthMask", "()Z", false);
    }

    public static AbstractInsnNode getArmorOpacity() {
        return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "dev/userteemu/f5transitions/TransitionHelper", "getArmorOpacity", "()F", false);
    }

    public static AbstractInsnNode isTransitionActive() {
        return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "dev/userteemu/f5transitions/TransitionHelper", "isTransitionActive", "()Z", false);
    }

    public static AbstractInsnNode shouldRenderFirstPersonHand() {
        return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "dev/userteemu/f5transitions/TransitionHelper", "shouldRenderFirstPersonHand", "()Z", false);
    }

    public static AbstractInsnNode getMultipliedFacingValueForPerspectiveMod() {
        return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "dev/userteemu/f5transitions/compatibility/PerspectiveModTransitionHelper", "getMultipliedFacingValueForPerspectiveMod", "(FLnet/minecraft/entity/Entity;ZZ)F", false);
    }

    public static AbstractInsnNode getMinecraftInstance() {
        return new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/Minecraft", "func_71410_x", "()Lnet/minecraft/client/Minecraft;", false); // getMinecraft
    }

    public static AbstractInsnNode getEntityRendererFromMCInstance() {
        return new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/Minecraft", "field_71460_t", "Lnet/minecraft/client/renderer/EntityRenderer;"); // entityRenderer
    }

    public static AbstractInsnNode getTransitionHelper() {
        return new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/EntityRenderer", EntityRendererTransformer.transitionHelper.name, EntityRendererTransformer.transitionHelper.desc);
    }
}
