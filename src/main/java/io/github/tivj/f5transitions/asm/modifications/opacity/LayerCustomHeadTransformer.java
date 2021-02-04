package io.github.tivj.f5transitions.asm.modifications.opacity;

import io.github.tivj.f5transitions.asm.tweaker.transformer.ITransformer;
import org.objectweb.asm.tree.*;
import scala.tools.asm.Opcodes;

import java.util.ListIterator;

import static io.github.tivj.f5transitions.asm.modifications.opacity.LayerArmorBaseTransformer.*;

public class LayerCustomHeadTransformer implements ITransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.entity.layers.LayerCustomHead"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode methodNode : classNode.methods) {
            String methodName = mapMethodName(classNode, methodNode);
            if (methodName.equals("doRenderLayer") || methodName.equals("func_177141_a")) {
                LocalVariableNode isEntityRenderEntity = createLocalVariable("isEntityRenderEntity", "Z", new LabelNode(), new LabelNode(), methodNode.localVariables);
                methodNode.localVariables.add(isEntityRenderEntity);

                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == Opcodes.FCONST_1 && node.getNext().getOpcode() == Opcodes.INVOKESTATIC) {
                        String invokeName = mapMethodNameFromNode(node.getNext());
                        if (invokeName.equals("color") || invokeName.equals("func_179124_c")) {
                            LabelNode end = new LabelNode();
                            methodNode.instructions.insertBefore(node, getAlpha(isEntityRenderEntity, end));
                            methodNode.instructions.insert(node, end);
                        }
                    }
                }

                iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == Opcodes.INVOKESTATIC) {
                        String invokeName = mapMethodNameFromNode(node);
                        if (invokeName.equals("pushMatrix") || invokeName.equals("func_179094_E")) {
                            methodNode.instructions.insert(node, beforeRender(isEntityRenderEntity, false));
                            break;
                        }
                    }
                }

                iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == Opcodes.INVOKESTATIC) {
                        String invokeName = mapMethodNameFromNode(node);
                        if (invokeName.equals("popMatrix") || invokeName.equals("func_179121_F")) {
                            LabelNode end = new LabelNode();
                            methodNode.instructions.insertBefore(node, afterRender(isEntityRenderEntity, end, false));
                            methodNode.instructions.insertBefore(node, end);
                            break;
                        }
                    }
                }
            }
        }
    }
}