package io.github.tivj.f5transitions.asm.modifications.opacity;

import io.github.tivj.f5transitions.asm.CommonInstructions;
import io.github.tivj.f5transitions.asm.ITransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

import static io.github.tivj.f5transitions.asm.CommonInstructions.*;
import static io.github.tivj.f5transitions.asm.hooks.GeneralEntityRenderingHook.canApplyTransitionsToEntity;

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
                LocalVariableNode isEntityRenderEntity = createLocalVariable("isEntityRenderEntity", "Z", new LabelNode(), new LabelNode(), methodNode.localVariables);
                methodNode.localVariables.add(isEntityRenderEntity);
                methodNode.instructions.add(isEntityRenderEntity.end);

                int phase = 0;
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (phase == 0 && node.getOpcode() == Opcodes.IFEQ && node.getPrevious().getOpcode() == Opcodes.ILOAD && ((VarInsnNode) node.getPrevious()).var == 9 && node.getNext().getNext().getNext().getOpcode() == Opcodes.INVOKESTATIC && node.getNext() instanceof LabelNode) { // also trigger the if statement (that makes the player opaque) if an entity is the player
                        String invokeName = mapMethodNameFromNode(node.getNext().getNext().getNext());
                        if (invokeName.equals("pushMatrix") || invokeName.equals("func_179094_E")) {
                            methodNode.instructions.insertBefore(node.getPrevious(), isEntityRenderEntity.start);
                            methodNode.instructions.insertBefore(node.getPrevious(), canApplyTransitionsToEntity(1));
                            methodNode.instructions.insertBefore(node.getPrevious(), new VarInsnNode(Opcodes.ISTORE, isEntityRenderEntity.index));

                            LabelNode beginningLabel = (LabelNode) node.getNext();
                            methodNode.instructions.insertBefore(node, new JumpInsnNode(Opcodes.IFNE, beginningLabel));
                            methodNode.instructions.insertBefore(node, new VarInsnNode(Opcodes.ILOAD, isEntityRenderEntity.index));
                            phase++;
                        }
                    } else if (phase == 1 && node.getOpcode() == Opcodes.LDC && ((LdcInsnNode)node).cst.equals(0.15F)) { // change GlStateManager.color's alpha
                        LabelNode afterNormalValue = new LabelNode();
                        methodNode.instructions.insert(node, afterNormalValue);
                        methodNode.instructions.insertBefore(node, getReplacementAlpha(afterNormalValue, isEntityRenderEntity));
                        phase++;
                    } else if (phase == 2 && node.getOpcode() == Opcodes.INVOKESTATIC && node.getPrevious().getOpcode() == Opcodes.ICONST_0 && node.getNext() instanceof LabelNode) {// disable depth mask if needed
                        String invokeName = mapMethodNameFromNode(node);
                        if (invokeName.equals("depthMask") || invokeName.equals("func_179132_a")) {
                            methodNode.instructions.insertBefore(node.getPrevious(), shouldDisableDepthMask((LabelNode) node.getNext(), isEntityRenderEntity));
                            phase++;
                        }
                    } else if (phase == 3 && node.getOpcode() == Opcodes.IFEQ && node.getPrevious().getOpcode() == Opcodes.ILOAD && ((VarInsnNode) node.getPrevious()).var == 9 && node.getNext() instanceof LabelNode) {
                        LabelNode beginningLabel = (LabelNode) node.getNext();
                        methodNode.instructions.insertBefore(node, new JumpInsnNode(Opcodes.IFNE, beginningLabel));
                        methodNode.instructions.insertBefore(node, new VarInsnNode(Opcodes.ILOAD, isEntityRenderEntity.index));
                        return;
                    }
                }
            }
        }
    }

    private InsnList shouldDisableDepthMask(LabelNode afterDisabling, LocalVariableNode isEntityRenderEntity) {
        InsnList list = new InsnList();
        LabelNode beforeDisabling = new LabelNode();

        list.add(new VarInsnNode(Opcodes.ILOAD, isEntityRenderEntity.index));
        list.add(new JumpInsnNode(Opcodes.IFEQ, beforeDisabling));
        list.add(getMinecraftInstance());
        list.add(getEntityRendererFromMCInstance());
        list.add(getTransitionHelper());
        list.add(CommonInstructions.shouldDisableDepthMask());
        list.add(new JumpInsnNode(Opcodes.IFEQ, afterDisabling));
        list.add(beforeDisabling);
        return list;
    }

    private InsnList getReplacementAlpha(LabelNode afterNormalValue, LocalVariableNode isEntityRenderEntity) {
        InsnList list = new InsnList();
        LabelNode endOfReplacement = new LabelNode();

        list.add(new VarInsnNode(Opcodes.ILOAD, isEntityRenderEntity.index));
        list.add(new JumpInsnNode(Opcodes.IFEQ, endOfReplacement));

        list.add(getMinecraftInstance());
        list.add(getEntityRendererFromMCInstance());
        list.add(getTransitionHelper());
        list.add(getPlayerOpacity());

        list.add(new JumpInsnNode(Opcodes.GOTO, afterNormalValue));
        list.add(endOfReplacement);
        return list;
    }
}