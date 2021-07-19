package dev.userteemu.f5transitions.asm.modifications.opacity;

import dev.userteemu.f5transitions.asm.CommonInstructions;
import dev.userteemu.f5transitions.asm.ITransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

import static dev.userteemu.f5transitions.asm.hooks.GeneralEntityRenderingHook.canApplyTransitionsToEntity;

public class LayerHeldItemTransformer implements ITransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.entity.layers.LayerHeldItem"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode methodNode : classNode.methods) {
            String methodName = mapMethodName(classNode, methodNode);
            if (methodName.equals("doRenderLayer") || methodName.equals("func_177141_a")) {
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == Opcodes.ALOAD && ((VarInsnNode) node).var == 9) {
                        if (node.getNext().getOpcode() == Opcodes.IFNULL) { // if in debug
                            methodNode.instructions.insert(node.getNext(), addBytecode((LabelNode) node.getNext().getNext(), ((JumpInsnNode) node.getNext()).label));
                            return;
                        } else if (node.getNext().getOpcode() == Opcodes.IFNONNULL) { // if in production
                            LabelNode oldLabel = ((JumpInsnNode) node.getNext()).label;
                            methodNode.instructions.insert(node.getNext(), new JumpInsnNode(Opcodes.IFNULL, (LabelNode) node.getNext().getNext()));
                            methodNode.instructions.remove(node.getNext());
                            methodNode.instructions.insert(node.getNext(), addBytecode(oldLabel, null));
                            return;
                        }
                    }
                }
            }
        }
    }

    private InsnList addBytecode(LabelNode canContinueLabel, LabelNode returnLabel) {
        InsnList list = new InsnList();
        list.add(canApplyTransitionsToEntity(1));
        list.add(new JumpInsnNode(Opcodes.IFEQ, canContinueLabel));

        list.add(CommonInstructions.getMinecraftInstance());
        list.add(CommonInstructions.getEntityRendererFromMCInstance());
        list.add(CommonInstructions.getTransitionHelper());
        list.add(CommonInstructions.shouldHeldItemBeRenderedInThirdPerson());
        list.add(returnLabel == null ? new JumpInsnNode(Opcodes.IFNE, canContinueLabel) : new JumpInsnNode(Opcodes.IFEQ, returnLabel));
        return list;
    }
}