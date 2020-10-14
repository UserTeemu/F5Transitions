package io.github.tivj.f5transitions.asm.modifications;

import io.github.tivj.f5transitions.asm.tweaker.transformer.ITransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

import static io.github.tivj.f5transitions.asm.GeneralFunctions.*;

public class RenderGlobalTransformer implements ITransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.RenderGlobal"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode methodNode : classNode.methods) {
            String methodName = mapMethodName(classNode, methodNode);
            if (methodName.equals("renderEntities") || methodName.equals("func_180446_a")) {
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == Opcodes.GETFIELD && node.getNext().getOpcode() == Opcodes.IFNE) {
                        String fieldName = mapFieldNameFromNode(node);
                        if (fieldName.equals("thirdPersonView") || fieldName.equals("field_74320_O")) {
                            AbstractInsnNode abstractInsnNode = node.getNext().getNext().getNext().getNext().getNext().getNext().getNext();
                            if (abstractInsnNode.getOpcode() == Opcodes.GETFIELD && abstractInsnNode.getNext().getOpcode() == Opcodes.DCONST_0 && abstractInsnNode.getNext().getNext().getOpcode() == Opcodes.DCMPG) {
                                String secondFieldName = mapFieldNameFromNode(abstractInsnNode);
                                if (secondFieldName.equals("posY") || secondFieldName.equals("field_70163_u")) {
                                    methodNode.instructions.insert(node.getNext(), orIfTransitionNotFinished(((JumpInsnNode)node.getNext()).label));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private InsnList orIfTransitionNotFinished(LabelNode nextIfStatement) {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/RenderGlobal", "field_72777_q", "Lnet/minecraft/client/Minecraft;")); // mc
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/Minecraft", "field_71460_t", "Lnet/minecraft/client/renderer/EntityRenderer;")); // entityRenderer
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/EntityRenderer", EntityRendererTransformer.transitionHelper.name, EntityRendererTransformer.transitionHelper.desc));
        list.add(isTransitionActive());
        list.add(new JumpInsnNode(Opcodes.IFNE, nextIfStatement));
        return list;
    }
}