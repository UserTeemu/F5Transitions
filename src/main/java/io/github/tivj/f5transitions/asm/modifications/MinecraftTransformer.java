package io.github.tivj.f5transitions.asm.modifications;

import io.github.tivj.f5transitions.asm.GeneralFunctions;
import io.github.tivj.f5transitions.asm.tweaker.transformer.ITransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

public class MinecraftTransformer implements ITransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.Minecraft"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode methodNode : classNode.methods) {
            String methodName = mapMethodName(classNode, methodNode);
            if (methodName.equals("runGameLoop") || methodName.equals("func_71411_J")) {
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == Opcodes.PUTFIELD && node.getPrevious().getPrevious().getOpcode() == Opcodes.GETFIELD) {
                        String ownerName = mapFieldNameFromNode(node.getPrevious().getPrevious());
                        String fieldName = mapFieldNameFromNode(node);
                        if ((ownerName.equals("gameSettings") || ownerName.equals("field_71474_y")) && (fieldName.equals("thirdPersonView") || fieldName.equals("field_74320_O"))) {
                            methodNode.instructions.insertBefore(node.getPrevious().getPrevious().getPrevious(), beforePerspectiveChanged());
                            break;
                        }
                    }
                }
            } else if (methodName.equals("runTick") || methodName.equals("func_71407_l")) {
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == Opcodes.PUTFIELD && node.getPrevious().getPrevious().getPrevious().getOpcode() == Opcodes.GETFIELD) {
                        String fieldName = mapFieldNameFromNode(node);
                        if ((fieldName.equals("thirdPersonView") || fieldName.equals("field_74320_O")) && isSameFieldReference(node, node.getPrevious().getPrevious().getPrevious())) {
                            AbstractInsnNode ownerNode = node.getPrevious().getPrevious().getPrevious().getPrevious().getPrevious(); // just so I don't need to call this again
                            if (ownerNode.getOpcode() == Opcodes.GETFIELD) {
                                String ownerName = mapFieldNameFromNode(node.getPrevious().getPrevious().getPrevious().getPrevious().getPrevious());
                                if (ownerName.equals("gameSettings") || ownerName.equals("field_71474_y")) {
                                    methodNode.instructions.insertBefore(node.getPrevious().getPrevious().getPrevious().getPrevious().getPrevious(), beforePerspectiveChanged());
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private InsnList beforePerspectiveChanged() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/Minecraft", "field_71460_t", "Lnet/minecraft/client/renderer/EntityRenderer;")); // entityRenderer
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/EntityRenderer", EntityRendererTransformer.transitionHelper.name, EntityRendererTransformer.transitionHelper.desc));
        list.add(GeneralFunctions.beforePerspectiveChanges());
        return list;
    }
}