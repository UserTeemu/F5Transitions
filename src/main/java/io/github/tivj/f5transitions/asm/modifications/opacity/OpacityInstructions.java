package io.github.tivj.f5transitions.asm.modifications.opacity;

import io.github.tivj.f5transitions.asm.CommonInstructions;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class OpacityInstructions {
    public static InsnList beforeRender(LocalVariableNode isEntityRenderEntity, boolean pushMatrix, boolean setColor) {
        InsnList list = new InsnList();
        list.add(isEntityRenderEntity.start);
        list.add(CommonInstructions.isEntityRenderEntity(1));
        list.add(new InsnNode(Opcodes.DUP));
        list.add(new VarInsnNode(Opcodes.ISTORE, isEntityRenderEntity.index));

        LabelNode end = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, end));

        if (pushMatrix) {
            list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", "pushMatrix", "()V", false));
        }

        if (setColor) {
            list.add(new InsnNode(Opcodes.FCONST_1));
            list.add(new InsnNode(Opcodes.FCONST_1));
            list.add(new InsnNode(Opcodes.FCONST_1));
            list.add(CommonInstructions.getMinecraftInstance());
            list.add(CommonInstructions.getEntityRendererFromMCInstance());
            list.add(CommonInstructions.getTransitionHelper());
            list.add(CommonInstructions.getArmorOpacity());
            list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", "color", "(FFFF)V", false));
        }

        LabelNode afterDepthMaskDisabled = new LabelNode();
        list.add(CommonInstructions.getMinecraftInstance());
        list.add(CommonInstructions.getEntityRendererFromMCInstance());
        list.add(CommonInstructions.getTransitionHelper());
        list.add(CommonInstructions.shouldDisableDepthMask());
        list.add(new JumpInsnNode(Opcodes.IFEQ, afterDepthMaskDisabled));
        list.add(new InsnNode(Opcodes.ICONST_0));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", "depthMask", "(Z)V", false));
        list.add(afterDepthMaskDisabled);

        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", "enableBlend", "()V", false));

        list.add(new IntInsnNode(Opcodes.SIPUSH, 516));
        list.add(new LdcInsnNode(0.003921569F));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", "alphaFunc", "(IF)V", false));

        list.add(end);
        return list;
    }

    public static InsnList afterRender(LocalVariableNode isEntityRenderEntity, LabelNode end, boolean popMatrix) {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ILOAD, isEntityRenderEntity.index));
        list.add(new JumpInsnNode(Opcodes.IFEQ, end));

        list.add(new IntInsnNode(Opcodes.SIPUSH, 516));
        list.add(new LdcInsnNode(0.1F));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", "alphaFunc", "(IF)V", false));

        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", "disableBlend", "()V", false));

        list.add(new InsnNode(Opcodes.ICONST_1));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", "depthMask", "(Z)V", false));

        if (popMatrix) {
            list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", "popMatrix", "()V", false));
        }

        list.add(isEntityRenderEntity.end);
        return list;
    }

    public static InsnList getAlpha(LocalVariableNode isEntityRenderEntity, LabelNode end) {
        InsnList list = new InsnList();
        LabelNode realAlpha = new LabelNode();

        list.add(new VarInsnNode(Opcodes.ILOAD, isEntityRenderEntity.index));
        list.add(new JumpInsnNode(Opcodes.IFEQ, realAlpha));

        list.add(CommonInstructions.getMinecraftInstance());
        list.add(CommonInstructions.getEntityRendererFromMCInstance());
        list.add(CommonInstructions.getTransitionHelper());
        list.add(CommonInstructions.getArmorOpacity());
        list.add(new JumpInsnNode(Opcodes.GOTO, end));

        list.add(realAlpha);
        return list;
    }
}
