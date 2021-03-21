package io.github.tivj.f5transitions.asm.modifications;

import io.github.tivj.f5transitions.asm.CommonInstructions;
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
                            methodNode.instructions.insert(node, changePerspective());
                            break;
                        }
                    }
                }
            } else if (methodName.equals("runTick") || methodName.equals("func_71407_l")) {
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                        String invokeName = mapMethodNameFromNode(node);
                        if (invokeName.equals("setDisplayListEntitiesDirty") || invokeName.equals("func_174979_m")) {
                            AbstractInsnNode earlierNode = node.getPrevious().getPrevious().getPrevious().getPrevious().getPrevious().getPrevious();
                            if (earlierNode.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                                String earlierInvokeName = mapMethodNameFromNode(earlierNode);
                                if (earlierInvokeName.equals("loadEntityShader") || earlierInvokeName.equals("func_175066_a")) {
                                    methodNode.instructions.insertBefore(node.getPrevious().getPrevious().getPrevious(), changePerspective());
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private InsnList changePerspective() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(CommonInstructions.getEntityRendererFromMCInstance());
        list.add(CommonInstructions.getTransitionHelper());

        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/Minecraft", "field_71474_y", "Lnet/minecraft/client/settings/GameSettings;")); // gameSettings
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/settings/GameSettings", "field_74320_O", "I")); // thirdPersonView
        list.add(CommonInstructions.getPerspectiveFromID());

        list.add(new InsnNode(Opcodes.ICONST_1));
        list.add(CommonInstructions.changePerspective());
        return list;
    }
}