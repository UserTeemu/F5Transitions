package io.github.tivj.f5transitions.asm.modifications;

import io.github.tivj.f5transitions.asm.GeneralFunctions;
import io.github.tivj.f5transitions.asm.tweaker.transformer.ITransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

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
                    if (node.getOpcode() == Opcodes.IFNULL && node.getPrevious().getOpcode() == Opcodes.ALOAD) {
                        if (((VarInsnNode)node.getPrevious()).var == 9) {
                            methodNode.instructions.insert(node, shouldHeldItemBeRenderedInThirdPerson(((JumpInsnNode)node).label));
                            return;
                        }
                    }
                }
            }
        }
    }

    private InsnList shouldHeldItemBeRenderedInThirdPerson(LabelNode ifNot) {
        InsnList list = new InsnList();
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/Minecraft", "func_71410_x", "()Lnet/minecraft/client/Minecraft;", false)); // getMinecraft
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/Minecraft", "field_71460_t", "Lnet/minecraft/client/renderer/EntityRenderer;")); // entityRenderer
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/EntityRenderer", EntityRendererTransformer.transitionHelper.name, EntityRendererTransformer.transitionHelper.desc));
        list.add(GeneralFunctions.shouldHeldItemBeRenderedInThirdPerson());
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifNot));
        return list;
    }
}