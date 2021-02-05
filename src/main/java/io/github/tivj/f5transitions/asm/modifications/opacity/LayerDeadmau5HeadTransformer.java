package io.github.tivj.f5transitions.asm.modifications.opacity;

import io.github.tivj.f5transitions.asm.tweaker.transformer.ITransformer;
import org.objectweb.asm.tree.*;
import scala.tools.asm.Opcodes;

import java.util.ListIterator;

import static io.github.tivj.f5transitions.asm.modifications.opacity.OpacityInstructions.*;

public class LayerDeadmau5HeadTransformer implements ITransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.entity.layers.LayerDeadmau5Head"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode methodNode : classNode.methods) {
            String methodName = mapMethodName(classNode, methodNode);
            if (methodName.equals("doRenderLayer") || methodName.equals("func_177141_a")) {
                LocalVariableNode isEntityRenderEntity = createLocalVariable("isEntityRenderEntity", "Z", new LabelNode(), new LabelNode(), methodNode.localVariables);
                methodNode.localVariables.add(isEntityRenderEntity);

                LabelNode originalEndLabel = null;
                LabelNode newEndLabel = new LabelNode();
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (originalEndLabel == null) {
                        if (node.getOpcode() == Opcodes.INVOKEVIRTUAL && node.getNext().getOpcode() == Opcodes.IFNE && node.getNext().getNext() instanceof LabelNode && node.getNext().getNext().getNext() instanceof LineNumberNode) {
                            String invokeName = mapMethodNameFromNode(node);
                            if (invokeName.equals("isInvisible") || invokeName.equals("func_82150_aj")) {
                                for (int i = 0; i < 4; i++) iterator.next();
                                originalEndLabel = ((JumpInsnNode) node.getNext()).label;
                                methodNode.instructions.insert(node.getNext().getNext().getNext(), beforeRender(isEntityRenderEntity, true, true));
                            }
                        }
                    } else if (node instanceof JumpInsnNode && originalEndLabel.equals(((JumpInsnNode) node).label)) {
                        ((JumpInsnNode) node).label = newEndLabel;
                    } else if (originalEndLabel.equals(node)) {
                        methodNode.instructions.insertBefore(node, newEndLabel);
                        methodNode.instructions.insertBefore(node, afterRender(isEntityRenderEntity, originalEndLabel,true, true));
                        break;
                    }
                }
            }
        }
    }
}