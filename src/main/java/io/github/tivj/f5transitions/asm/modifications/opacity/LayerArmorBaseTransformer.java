package io.github.tivj.f5transitions.asm.modifications.opacity;

import io.github.tivj.f5transitions.asm.tweaker.transformer.ITransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class LayerArmorBaseTransformer implements ITransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.entity.layers.LayerArmorBase"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode methodNode : classNode.methods) {
            String methodName = mapMethodName(classNode, methodNode);
            if (methodName.equals("renderLayer") || methodName.equals("func_177141_a")) {
                LocalVariableNode isEntityRenderEntity = createLocalVariable("isEntityRenderEntity", "Z", new LabelNode(), new LabelNode(), methodNode.localVariables);
                methodNode.localVariables.add(isEntityRenderEntity);

                LabelNode endOfRendering = new LabelNode();
                for (int i = 0; i < methodNode.instructions.size(); i++) {
                    AbstractInsnNode node = methodNode.instructions.get(i);
                    if (node.getOpcode() == Opcodes.GETFIELD && node.getPrevious().getOpcode() == Opcodes.ALOAD && ((VarInsnNode)node.getPrevious()).var == 0) {
                        String fieldName = mapFieldNameFromNode(node);
                        if (fieldName.equals("alpha") || fieldName.equals("field_177187_e")) {
                            LabelNode end = new LabelNode();
                            InsnList getAlpha = OpacityInstructions.getAlpha(isEntityRenderEntity, end);
                            i += getAlpha.size();
                            methodNode.instructions.insertBefore(node.getPrevious(), getAlpha);
                            methodNode.instructions.insert(node, end);
                        }
                    } else if (node.getOpcode() == Opcodes.INVOKEVIRTUAL && node.getPrevious().getPrevious().getOpcode() == Opcodes.ALOAD && ((VarInsnNode)node.getPrevious().getPrevious()).var == 11) {
                        String invokeName = mapMethodNameFromNode(node);
                        if (invokeName.equals("getColor") || invokeName.equals("func_177182_a")) {
                            methodNode.instructions.insertBefore(node.getPrevious().getPrevious(), OpacityInstructions.beforeRender(isEntityRenderEntity, true, false));
                            i = methodNode.instructions.indexOf(node);
                        }
                    } else if (node.getOpcode() == Opcodes.IFNE && node.getPrevious().getOpcode() == Opcodes.GETFIELD && node.getNext().getNext().getNext().getOpcode() == Opcodes.IFEQ) {
                        String fieldName = mapFieldNameFromNode(node.getPrevious());
                        if (fieldName.equals("skipRenderGlint") || fieldName.equals("field_177193_i")) {
                            methodNode.instructions.insert(node.getNext().getNext().getNext(), new JumpInsnNode(Opcodes.IFEQ, endOfRendering));
                            methodNode.instructions.remove(node.getNext().getNext().getNext());

                            methodNode.instructions.insert(node, new JumpInsnNode(Opcodes.IFNE, endOfRendering));
                            methodNode.instructions.remove(node);
                        }
                    } else if (node.getOpcode() == Opcodes.INVOKESPECIAL && node.getNext() instanceof LabelNode) {
                        String invokeName = mapMethodNameFromNode(node);
                        if (invokeName.equals("renderGlint") || invokeName.equals("func_177183_a")) {
                            methodNode.instructions.insert(node, OpacityInstructions.afterRender(isEntityRenderEntity, (LabelNode) node.getNext(), true, false));
                            methodNode.instructions.insert(node, endOfRendering);
                            return;
                        }
                    }
                }
            }
        }
    }
}