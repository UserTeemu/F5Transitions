package io.github.tivj.f5transitions.asm.modifications.opacity;

import io.github.tivj.f5transitions.asm.tweaker.transformer.ITransformer;
import org.objectweb.asm.tree.*;
import scala.tools.asm.Opcodes;

import java.util.ListIterator;

import static io.github.tivj.f5transitions.asm.CommonInstructions.*;
import static io.github.tivj.f5transitions.asm.hooks.GeneralEntityRenderingHook.canApplyTransitionsToEntity;

public class LayerArrowTransformer implements ITransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.entity.layers.LayerArrow"};
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
                    if (node.getOpcode() == Opcodes.IFLE && node.getPrevious().getOpcode() == Opcodes.ILOAD) {
                        methodNode.instructions.insert(node, ifThereAreArrows(((JumpInsnNode) node).label));
                    }
                }
            }
        }
    }

    private InsnList ifThereAreArrows(LabelNode returnLabel) {
        InsnList list = new InsnList();
        list.add(canApplyTransitionsToEntity(1));
        list.add(new JumpInsnNode(Opcodes.IFEQ, returnLabel));

        list.add(getMinecraftInstance());
        list.add(getEntityRendererFromMCInstance());
        list.add(getTransitionHelper());
        list.add(shouldRenderArrowLayer());
        list.add(new JumpInsnNode(Opcodes.IFEQ, returnLabel));
        return list;
    }
}