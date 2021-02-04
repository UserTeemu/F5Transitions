package io.github.tivj.f5transitions.asm.modifications.opacity;

import io.github.tivj.f5transitions.asm.CommonInstructions;
import io.github.tivj.f5transitions.asm.tweaker.transformer.ITransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class LayerArmorBaseTransformer implements ITransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.entity.layers.LayerArmorBase"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode methodNode : classNode.methods) {
            String methodName = mapMethodName(classNode, methodNode);
            if (methodName.equals("renderLayer") || methodName.equals("func_177141_a")) {
                LocalVariableNode isEntityRenderEntity = createLocalVariable("isEntityRenderEntity", "Z", new LabelNode(), new LabelNode(), methodNode.localVariables);
                methodNode.localVariables.add(isEntityRenderEntity);

                LabelNode endOfRendering = new LabelNode();
                for (int i = 0; i < methodNode.instructions.size(); i++) {
                    AbstractInsnNode node = methodNode.instructions.get(i);
                    if (node.getOpcode() == Opcodes.GETFIELD && node.getPrevious().getOpcode() == Opcodes.ALOAD && ((VarInsnNode)node.getPrevious()).var == 0) {
                        String fieldName = mapFieldNameFromNode(node);
                        if (fieldName.equals("alpha") || fieldName.equals("field_177187_e")) {
                            LabelNode end = new LabelNode();
                            InsnList getAlpha = getAlpha(isEntityRenderEntity, end);
                            i += getAlpha.size();
                            methodNode.instructions.insertBefore(node.getPrevious(), getAlpha);
                            methodNode.instructions.insert(node, end);
                        }
                    } else if (node.getOpcode() == Opcodes.INVOKEVIRTUAL && node.getPrevious().getPrevious().getOpcode() == Opcodes.ALOAD && ((VarInsnNode)node.getPrevious().getPrevious()).var == 11) {
                        String invokeName = mapMethodNameFromNode(node);
                        if (invokeName.equals("getColor") || invokeName.equals("func_177182_a")) {
                            methodNode.instructions.insertBefore(node.getPrevious().getPrevious(), beforeRender(isEntityRenderEntity, true));
                            i = methodNode.instructions.indexOf(node);
                        }
                    } else if (node.getOpcode() == Opcodes.IFNE && node.getPrevious().getOpcode() == Opcodes.GETFIELD && node.getNext().getNext().getNext().getOpcode() == Opcodes.IFEQ) {
                        String fieldName = mapFieldNameFromNode(node.getPrevious());
                        if (fieldName.equals("skipRenderGlint") || fieldName.equals("field_177193_i")) {
                            methodNode.instructions.insert(node.getNext().getNext().getNext(), new JumpInsnNode(Opcodes.IFEQ, endOfRendering));
                            methodNode.instructions.remove(node.getNext().getNext().getNext());

                            methodNode.instructions.insert(node, new JumpInsnNode(Opcodes.IFNE, endOfRendering));
                            methodNode.instructions.remove(node);
                        }
                    } else if (node.getOpcode() == Opcodes.INVOKESPECIAL && node.getNext() instanceof LabelNode) {
                        String invokeName = mapMethodNameFromNode(node);
                        if (invokeName.equals("renderGlint") || invokeName.equals("func_177183_a")) {
                            methodNode.instructions.insert(node, afterRender(isEntityRenderEntity, (LabelNode) node.getNext(), true));
                            methodNode.instructions.insert(node, endOfRendering);
                            return;
                        }
                    }
                }
            }
        }
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

    public static InsnList beforeRender(LocalVariableNode isEntityRenderEntity, boolean pushMatrix) {
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
}