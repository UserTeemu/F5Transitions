package io.github.tivj.f5transitions.asm.modifications;

import io.github.tivj.f5transitions.asm.GeneralFunctions;
import io.github.tivj.f5transitions.asm.tweaker.transformer.ITransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

public class ItemRendererTransformer implements ITransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.ItemRenderer"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode methodNode : classNode.methods) {
            String methodName = mapMethodName(classNode, methodNode);
            if (methodName.equals("renderItemInFirstPerson") || methodName.equals("func_78440_a")) {
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == Opcodes.IFNULL && node.getPrevious().getOpcode() == Opcodes.GETFIELD && node.getPrevious().getPrevious().getPrevious().getPrevious().getPrevious().getOpcode() == Opcodes.INVOKESTATIC) {
                        String fieldName = mapFieldNameFromNode(node.getPrevious());
                        String invokeName = mapMethodNameFromNode(node.getPrevious().getPrevious().getPrevious().getPrevious().getPrevious());
                        if (
                                (fieldName.equals("itemToRender") || invokeName.equals("field_78453_b")) &&
                                (invokeName.equals("pushMatrix") || invokeName.equals("func_179094_E"))
                        ) {
                            methodNode.instructions.insertBefore(node.getPrevious().getPrevious().getPrevious().getPrevious(), getYrotationBonus());
                            return;
                        }
                    }
                }
            }
        }
    }

    private InsnList getYrotationBonus() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/ItemRenderer", "field_78455_a", "Lnet/minecraft/client/Minecraft;")); // mc
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/Minecraft", "field_71460_t", "Lnet/minecraft/client/renderer/EntityRenderer;")); // entityRenderer
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/EntityRenderer", EntityRendererTransformer.transitionHelper.name, EntityRendererTransformer.transitionHelper.desc));
        list.add(new VarInsnNode(Opcodes.FLOAD, 1));
        list.add(GeneralFunctions.getYrotationBonus());
        list.add(new InsnNode(Opcodes.FNEG));
        list.add(new InsnNode(Opcodes.FCONST_0));
        list.add(new InsnNode(Opcodes.FCONST_1));
        list.add(new InsnNode(Opcodes.FCONST_0));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", "func_179114_b", "(FFFF)V", false)); // rotate
        return list;
    }
}