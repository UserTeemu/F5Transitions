package dev.userteemu.f5transitions.asm.modifications.opacity;

import dev.userteemu.f5transitions.asm.ITransformer;
import org.objectweb.asm.tree.*;
import scala.tools.asm.Opcodes;

import java.util.ListIterator;

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

                boolean inProd = false;
                LabelNode newEndLabel = new LabelNode(); // used only by non-production transforming
                LabelNode originalEndLabel = null;
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (originalEndLabel == null && !inProd) {
                        if (node.getOpcode() == Opcodes.INVOKEVIRTUAL && (node.getNext().getOpcode() == Opcodes.IFNE || node.getNext().getOpcode() == Opcodes.IFEQ) && node.getNext().getNext() instanceof LabelNode && node.getNext().getNext().getNext() instanceof LineNumberNode) {
                            String invokeName = mapMethodNameFromNode(node);
                            if (invokeName.equals("isInvisible") || invokeName.equals("func_82150_aj")) {
                                for (int i = 0; i < 4; i++) iterator.next();
                                if (node.getNext().getOpcode() == Opcodes.IFNE) {
                                    originalEndLabel = ((JumpInsnNode) node.getNext()).label;
                                    inProd = false;
                                } else {
                                    node = node.getNext().getNext().getNext();
                                    inProd = true;
                                }

                                methodNode.instructions.insert(node.getNext().getNext().getNext(), OpacityInstructions.beforeRender(isEntityRenderEntity, true, true));
                            }
                        }
                    } else if (inProd) {
                        if (originalEndLabel == null) {
                            if (node.getPrevious().getOpcode() == Opcodes.ICONST_2 && node.getOpcode() == Opcodes.IF_ICMPGE) {
                                originalEndLabel = ((JumpInsnNode) node).label;
                            }
                        } else if (originalEndLabel.equals(node) && node.getNext() instanceof LineNumberNode) {
                            methodNode.instructions.insert(node.getNext(), OpacityInstructions.afterRender(isEntityRenderEntity, originalEndLabel,true));
                        }
                    } else {
                        if (node instanceof JumpInsnNode && originalEndLabel.equals(((JumpInsnNode) node).label)) {
                            ((JumpInsnNode) node).label = newEndLabel;
                        } else if (originalEndLabel.equals(node)) {
                            methodNode.instructions.insertBefore(node, newEndLabel);
                            methodNode.instructions.insertBefore(node, OpacityInstructions.afterRender(isEntityRenderEntity, originalEndLabel, true));
                            break;
                        }
                    }
                }
            }
        }
    }
}