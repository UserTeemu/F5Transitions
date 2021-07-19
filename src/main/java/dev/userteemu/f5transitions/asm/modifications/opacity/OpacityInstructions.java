package dev.userteemu.f5transitions.asm.modifications.opacity;

import dev.userteemu.f5transitions.asm.CommonInstructions;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import static dev.userteemu.f5transitions.asm.hooks.GeneralEntityRenderingHook.canApplyTransitionsToEntity;

public class OpacityInstructions {
    public static InsnList beforeRender(LocalVariableNode isEntityRenderEntity, boolean pushMatrix, boolean setColor) {
        InsnList list = new InsnList();
        list.add(isEntityRenderEntity.start);
        list.add(canApplyTransitionsToEntity(1));
        list.add(new InsnNode(Opcodes.DUP));
        list.add(new VarInsnNode(Opcodes.ISTORE, isEntityRenderEntity.index));

        LabelNode end = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, end));

        if (pushMatrix) {
            list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", "func_179094_E", "()V", false)); // pushMatrix
        }

        if (setColor) {
            list.add(new InsnNode(Opcodes.FCONST_1));
            list.add(new InsnNode(Opcodes.FCONST_1));
            list.add(new InsnNode(Opcodes.FCONST_1));
            list.add(CommonInstructions.getMinecraftInstance());
            list.add(CommonInstructions.getEntityRendererFromMCInstance());
            list.add(CommonInstructions.getTransitionHelper());
            list.add(CommonInstructions.getArmorOpacity());
            list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", "func_179131_c", "(FFFF)V", false)); // color
        }

        LabelNode afterDepthMaskDisabled = new LabelNode();
        list.add(CommonInstructions.getMinecraftInstance());
        list.add(CommonInstructions.getEntityRendererFromMCInstance());
        list.add(CommonInstructions.getTransitionHelper());
        list.add(CommonInstructions.shouldDisableDepthMask());
        list.add(new JumpInsnNode(Opcodes.IFEQ, afterDepthMaskDisabled));
        list.add(new InsnNode(Opcodes.ICONST_0));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", "func_179132_a", "(Z)V", false)); // depthMask
        list.add(afterDepthMaskDisabled);

        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", "func_179147_l", "()V", false)); // enableBlend

        list.add(new IntInsnNode(Opcodes.SIPUSH, 516));
        list.add(new LdcInsnNode(0.003921569F));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", "func_179092_a", "(IF)V", false)); // alphaFunc

        list.add(end);
        return list;
    }

    public static InsnList afterRender(LocalVariableNode isEntityRenderEntity, LabelNode end, boolean popMatrix) {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ILOAD, isEntityRenderEntity.index));
        list.add(new JumpInsnNode(Opcodes.IFEQ, end));

        list.add(new IntInsnNode(Opcodes.SIPUSH, 516));
        list.add(new LdcInsnNode(0.1F));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", "func_179092_a", "(IF)V", false)); // alphaFunc

        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", "func_179084_k", "()V", false)); // disableBlend

        list.add(new InsnNode(Opcodes.ICONST_1));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", "func_179132_a", "(Z)V", false)); // depthMask

        if (popMatrix) {
            list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", "func_179121_F", "()V", false)); // popMatrix
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
