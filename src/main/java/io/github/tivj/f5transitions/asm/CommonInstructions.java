package io.github.tivj.f5transitions.asm;

import io.github.tivj.f5transitions.asm.modifications.EntityRendererTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class CommonInstructions {
    public static AbstractInsnNode getPerspectiveFromID() {
        return new MethodInsnNode(Opcodes.INVOKESTATIC, "io/github/tivj/f5transitions/TransitionHelper", "getPerspectiveFromID", "(I)Lio/github/tivj/f5transitions/perspectives/Perspective;", false);
    }

    public static AbstractInsnNode updatePerspectiveTransitions() {
        return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "io/github/tivj/f5transitions/TransitionHelper", "updatePerspectiveTimer", "()V", false);
    }

    public static AbstractInsnNode getCameraDistance() {
        return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "io/github/tivj/f5transitions/TransitionHelper", "getCameraDistance", "(F)D", false);
    }

    public static AbstractInsnNode getYrotationBonus() {
        return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "io/github/tivj/f5transitions/TransitionHelper", "getYRotationBonus", "(F)F", false);
    }

    public static AbstractInsnNode shouldHeldItemBeRenderedInThirdPerson() {
        return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "io/github/tivj/f5transitions/TransitionHelper", "shouldItemBeRenderedInThirdPerson", "()Z", false);
    }

    public static AbstractInsnNode shouldRenderArrowLayer() {
        return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "io/github/tivj/f5transitions/TransitionHelper", "shouldRenderArrowLayer", "()Z", false);
    }

    public static AbstractInsnNode beforePerspectiveChanges() {
        return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "io/github/tivj/f5transitions/TransitionHelper", "beforePerspectiveChanged", "()V", false);
    }

    public static AbstractInsnNode changePerspective() {
        return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "io/github/tivj/f5transitions/TransitionHelper", "changePerspective", "(Lio/github/tivj/f5transitions/perspectives/Perspective;Z)V", false);
    }

    public static AbstractInsnNode getPlayerOpacity() {
        return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "io/github/tivj/f5transitions/TransitionHelper", "getPlayerOpacity", "()F", false);
    }

    public static AbstractInsnNode shouldDisableDepthMask() {
        return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "io/github/tivj/f5transitions/TransitionHelper", "shouldDisableDepthMask", "()Z", false);
    }

    public static AbstractInsnNode getArmorOpacity() {
        return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "io/github/tivj/f5transitions/TransitionHelper", "getArmorOpacity", "()F", false);
    }

    public static AbstractInsnNode isTransitionActive() {
        return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "io/github/tivj/f5transitions/TransitionHelper", "isTransitionActive", "()Z", false);
    }

    public static AbstractInsnNode shouldRenderFirstPersonHand() {
        return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "io/github/tivj/f5transitions/TransitionHelper", "shouldRenderFirstPersonHand", "()Z", false);
    }

    public static InsnList isEntityRenderEntity(int indexOfEntityVariable) {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, indexOfEntityVariable));
        list.add(getMinecraftInstance());
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/Minecraft", "func_175606_aa", "()Lnet/minecraft/entity/Entity;", false)); // getRenderViewEntity
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "equals", "(Ljava/lang/Object;)Z", false));
        return list;
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
