package io.github.tivj.f5transitions.asm.modifications.opacity;

import io.github.tivj.f5transitions.asm.CommonInstructions;
import io.github.tivj.f5transitions.asm.tweaker.transformer.ITransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

public class LayerHeldItemAndCustomHeadTransformer implements ITransformer {
    @Override
    public String[] getClassName() { // transformers for these 2 classes are in the same transformer, because the operations are quite similar
        return new String[]{"net.minecraft.client.renderer.entity.layers.LayerHeldItem", "net.minecraft.client.renderer.entity.layers.LayerCustomHead"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode methodNode : classNode.methods) {
            String methodName = mapMethodName(classNode, methodNode);
            if (methodName.equals("doRenderLayer") || methodName.equals("func_177141_a")) {
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                if (name.equals("net.minecraft.client.renderer.entity.layers.LayerHeldItem")) {
                    while (iterator.hasNext()) {
                        AbstractInsnNode node = iterator.next();
                        if (node.getOpcode() == Opcodes.ALOAD && ((VarInsnNode) node).var == 9) {
                            if (node.getNext().getOpcode() == Opcodes.IFNULL) { // if in debug
                                methodNode.instructions.insert(node.getNext(), addBytecode(CommonInstructions.shouldHeldItemBeRenderedInThirdPerson(), (LabelNode) node.getNext().getNext(), ((JumpInsnNode) node.getNext()).label));
                                return;
                            } else if (node.getNext().getOpcode() == Opcodes.IFNONNULL) { // if in production
                                LabelNode oldLabel = ((JumpInsnNode) node.getNext()).label;
                                methodNode.instructions.insert(node.getNext(), new JumpInsnNode(Opcodes.IFNULL, (LabelNode) node.getNext().getNext()));
                                methodNode.instructions.remove(node.getNext());
                                methodNode.instructions.insert(node.getNext(), addBytecode(CommonInstructions.shouldHeldItemBeRenderedInThirdPerson(), oldLabel, null));
                                return;
                            }
                        }
                    }
                } else {
                    while (iterator.hasNext()) {
                        AbstractInsnNode node = iterator.next();
                        if (node.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                            String invokeName = mapMethodNameFromNode(node);
                            if (invokeName.equals("getItem") || invokeName.equals("func_77973_b")) {
                                if (node.getNext().getOpcode() == Opcodes.IFNULL) { // if in debug
                                    methodNode.instructions.insert(node.getNext(), addBytecode(CommonInstructions.shouldCustomHeadBeRendered(), (LabelNode) node.getNext().getNext(), ((JumpInsnNode) node.getNext()).label));
                                    return;
                                } else if (node.getNext().getOpcode() == Opcodes.IFNONNULL) { // if in production
                                    LabelNode oldLabel = ((JumpInsnNode)node.getNext()).label;
                                    methodNode.instructions.insert(node.getNext(), new JumpInsnNode(Opcodes.IFNULL, (LabelNode) node.getNext().getNext()));
                                    methodNode.instructions.remove(node.getNext());
                                    methodNode.instructions.insert(node.getNext(), addBytecode(CommonInstructions.shouldCustomHeadBeRendered(), oldLabel, null));
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private InsnList addBytecode(AbstractInsnNode function, LabelNode canContinueLabel, LabelNode returnLabel) {
        InsnList list = new InsnList();
        list.add(CommonInstructions.isEntityRenderEntity(1));
        list.add(new JumpInsnNode(Opcodes.IFEQ, canContinueLabel));

        list.add(CommonInstructions.getMinecraftInstance());
        list.add(CommonInstructions.getEntityRendererFromMCInstance());
        list.add(CommonInstructions.getTransitionHelper());
        list.add(function);
        list.add(returnLabel == null ? new JumpInsnNode(Opcodes.IFNE, canContinueLabel) : new JumpInsnNode(Opcodes.IFEQ, returnLabel));
        return list;
    }
}