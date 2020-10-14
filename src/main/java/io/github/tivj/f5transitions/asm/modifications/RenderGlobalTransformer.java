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
            if (methodName.equals("renderEntities") || methodName.equals("todo")) {
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == Opcodes.GETFIELD && node.getNext().getOpcode() == Opcodes.IFNE) {
                        String fieldName = mapFieldNameFromNode(node);
                        if (fieldName.equals("thirdPersonView") || fieldName.equals("todo")) {
                            AbstractInsnNode abstractInsnNode = node.getNext().getNext().getNext().getNext().getNext().getNext().getNext();
                            if (abstractInsnNode.getOpcode() == Opcodes.GETFIELD && abstractInsnNode.getNext().getOpcode() == Opcodes.DCONST_0 && abstractInsnNode.getNext().getNext().getOpcode() == Opcodes.DCMPG) {
                                String secondFieldName = mapFieldNameFromNode(abstractInsnNode);
                                if (secondFieldName.equals("posY") || secondFieldName.equals("todo")) {
                                    // actual things start from here
                                    methodNode.instructions.insert(node.getNext(), orIfTransitionNotFinished(((JumpInsnNode)node.getNext()).label));
                                    // actual things ended already... sad
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
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/RenderGlobal", "mc", "Lnet/minecraft/client/Minecraft;"));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/Minecraft", "entityRenderer", "Lnet/minecraft/client/renderer/EntityRenderer;"));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/EntityRenderer", EntityRendererTransformer.transitionHelper.name, EntityRendererTransformer.transitionHelper.desc));
        list.add(isTransitionActive());
        list.add(new JumpInsnNode(Opcodes.IFNE, nextIfStatement));
        return list;
    }
}