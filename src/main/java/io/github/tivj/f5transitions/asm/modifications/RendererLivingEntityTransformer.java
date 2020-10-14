package io.github.tivj.f5transitions.asm.modifications;

import io.github.tivj.f5transitions.asm.tweaker.transformer.ITransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

import static io.github.tivj.f5transitions.asm.GeneralFunctions.getPlayerOpacity;
import static io.github.tivj.f5transitions.asm.GeneralFunctions.isPlayerNotRenderedSolid;

public class RendererLivingEntityTransformer implements ITransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.entity.RendererLivingEntity"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode methodNode : classNode.methods) {
            String methodName = mapMethodName(classNode, methodNode);
            if (methodName.equals("renderModel") || methodName.equals("func_77036_a")) {
                int phase = 0;
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (phase == 0 && node.getOpcode() == Opcodes.IFEQ && node.getPrevious().getOpcode() == Opcodes.ILOAD && ((VarInsnNode) node.getPrevious()).var == 9 && node.getNext().getNext().getNext().getOpcode() == Opcodes.INVOKESTATIC && node.getNext() instanceof LabelNode) { // also trigger the if statement (that makes the player opaque) if an entity is the player
                        String invokeName = mapMethodNameFromNode(node.getNext().getNext().getNext());
                        if (invokeName.equals("pushMatrix") || invokeName.equals("func_179094_E")) {
                            LabelNode beginningLabel = (LabelNode) node.getNext();
                            methodNode.instructions.insertBefore(node, new JumpInsnNode(Opcodes.IFNE, beginningLabel));
                            methodNode.instructions.insertBefore(node, isEntityPlayer());
                            phase++;
                        }
                    } else if (phase == 1 && node.getOpcode() == Opcodes.LDC && ((LdcInsnNode)node).cst.equals(0.15F)) { // change GlStateManager.color's alpha
                            LabelNode afterNormalValue = new LabelNode();
                            methodNode.instructions.insert(node, afterNormalValue);
                            methodNode.instructions.insertBefore(node, getReplacementAlpha(afterNormalValue, false));
                            phase++;

                    } else if (phase == 2 && node.getOpcode() == Opcodes.INVOKESTATIC && node.getPrevious().getOpcode() == Opcodes.ICONST_0 && node.getNext() instanceof LabelNode) {// disable depth mask if needed
                        String invokeName = mapMethodNameFromNode(node);
                        if (invokeName.equals("depthMask") || invokeName.equals("func_179132_a")) {
                            methodNode.instructions.insertBefore(node.getPrevious(), shouldDisableDepthMask((LabelNode) node.getNext()));
                            phase++;
                        }
                    } else if (phase == 3 && node.getOpcode() == Opcodes.LDC && ((LdcInsnNode)node).cst.equals(0.003921569F)) { // change GlStateManager.alphaFunc's ref, similar to phase 1
                        LabelNode afterNormalValue = new LabelNode();
                        methodNode.instructions.insert(node, afterNormalValue);
                        methodNode.instructions.insertBefore(node, getReplacementAlpha(afterNormalValue, true));
                        phase++;
                    } else if (phase == 4 && node.getOpcode() == Opcodes.IFEQ && node.getPrevious().getOpcode() == Opcodes.ILOAD && ((VarInsnNode) node.getPrevious()).var == 9 && node.getNext() instanceof LabelNode) {
                        LabelNode beginningLabel = (LabelNode) node.getNext();
                        methodNode.instructions.insertBefore(node, new JumpInsnNode(Opcodes.IFNE, beginningLabel));
                        methodNode.instructions.insertBefore(node, isEntityPlayer());
                        return;
                    }
                }
            }
        }
    }

    private InsnList shouldDisableDepthMask(LabelNode afterDisabling) {
        InsnList list = new InsnList();
        LabelNode beforeDisabling = new LabelNode();

        list.add(isEntityPlayer());
        list.add(new JumpInsnNode(Opcodes.IFEQ, beforeDisabling));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/Minecraft", "func_71410_x", "()Lnet/minecraft/client/Minecraft;", false)); // getMinecraft
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/Minecraft", "field_71460_t", "Lnet/minecraft/client/renderer/EntityRenderer;")); // entityRenderer
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/EntityRenderer", EntityRendererTransformer.transitionHelper.name, EntityRendererTransformer.transitionHelper.desc));
        list.add(isPlayerNotRenderedSolid());
        list.add(new JumpInsnNode(Opcodes.IFEQ, afterDisabling));
        list.add(beforeDisabling);
        return list;
    }

    private InsnList getReplacementAlpha(LabelNode afterNormalValue, boolean divideByTen) {
        InsnList list = new InsnList();
        LabelNode endOfReplacement = new LabelNode();

        list.add(isEntityPlayer());
        list.add(new JumpInsnNode(Opcodes.IFEQ, endOfReplacement));

        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/Minecraft", "func_71410_x", "()Lnet/minecraft/client/Minecraft;", false)); // getMinecraft
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/Minecraft", "field_71460_t", "Lnet/minecraft/client/renderer/EntityRenderer;")); // entityRenderer
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/EntityRenderer", EntityRendererTransformer.transitionHelper.name, EntityRendererTransformer.transitionHelper.desc));
        list.add(getPlayerOpacity());

        if (divideByTen) {
            list.add(new LdcInsnNode(10F));
            list.add(new InsnNode(Opcodes.FDIV));
        }

        list.add(new JumpInsnNode(Opcodes.GOTO, afterNormalValue));
        list.add(endOfReplacement);
        return list;
    }

    private InsnList isEntityPlayer() {
        InsnList list = new InsnList();
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/Minecraft", "func_71410_x", "()Lnet/minecraft/client/Minecraft;", false)); // getMinecraft
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/Minecraft", "field_71439_g", "Lnet/minecraft/client/entity/EntityPlayerSP;")); // thePlayer
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "equals", "(Ljava/lang/Object;)Z", false));
        return list;
    }
}