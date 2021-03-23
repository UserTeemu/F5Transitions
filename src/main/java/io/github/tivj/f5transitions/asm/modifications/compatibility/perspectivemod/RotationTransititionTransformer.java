package io.github.tivj.f5transitions.asm.modifications.compatibility.perspectivemod;

import io.github.tivj.f5transitions.asm.CommonInstructions;
import io.github.tivj.f5transitions.asm.ITransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

public class RotationTransititionTransformer implements ITransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"me.djtheredstoner.perspectivemod.asm.hooks.ActiveRenderInfoHook", "me.djtheredstoner.perspectivemod.asm.hooks.EntityRendererHook", "me.djtheredstoner.perspectivemod.asm.hooks.RenderManagerHook"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode methodNode : classNode.methods) {
            ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
            while (iterator.hasNext()) {
                AbstractInsnNode node = iterator.next();
                if (node.getOpcode() == Opcodes.GETFIELD && ((FieldInsnNode)node).owner.equals("me/djtheredstoner/perspectivemod/PerspectiveMod") && ((FieldInsnNode)node).desc.equals("F") && node.getPrevious().getOpcode() == Opcodes.GETSTATIC && node.getNext().getNext().getNext().getNext().getOpcode() == Opcodes.ALOAD && node.getNext().getNext().getNext().getNext().getNext().getOpcode() == Opcodes.GETFIELD) {
                    String fieldName = ((FieldInsnNode)node).name;
                    FieldInsnNode mcField = (FieldInsnNode) node.getNext().getNext().getNext().getNext().getNext();
                    String mcFieldName = mapFieldNameFromNode(mcField);

                    boolean rotationYaw = mcFieldName.equals("rotationYaw") || mcFieldName.equals("field_70177_z");
                    boolean prevRotationYaw = mcFieldName.equals("prevRotationYaw") || mcFieldName.equals("field_70126_B");

                    boolean rotationPitch = mcFieldName.equals("rotationPitch") || mcFieldName.equals("field_70125_A");
                    boolean prevRotationPitch = mcFieldName.equals("prevRotationPitch") || mcFieldName.equals("field_70127_C");

                    boolean isYaw = fieldName.equals("cameraYaw") && (rotationYaw || prevRotationYaw);
                    boolean isPitch = fieldName.equals("cameraPitch") && (rotationPitch || prevRotationPitch);
                    if (isYaw || isPitch) {
                        methodNode.instructions.insertBefore(node.getPrevious(), getTransitionHelperInstance());
                        methodNode.instructions.insert(node, getCorrectedValue(mcField, isYaw, prevRotationYaw || prevRotationPitch));
                    }
                }
            }
        }
    }

    private InsnList getCorrectedValue(FieldInsnNode mcField, boolean isYaw, boolean isPreviousValue) {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, ((VarInsnNode)mcField.getPrevious()).var));
        list.add(new InsnNode(isYaw ? Opcodes.ICONST_1 : Opcodes.ICONST_0));
        list.add(new InsnNode(isPreviousValue ? Opcodes.ICONST_1 : Opcodes.ICONST_0));
        list.add(CommonInstructions.getMultipliedFacingValueForPerspectiveMod());
        return list;
    }

    private InsnList getTransitionHelperInstance() {
        InsnList list = new InsnList();
        list.add(CommonInstructions.getMinecraftInstance());
        list.add(CommonInstructions.getEntityRendererFromMCInstance());
        list.add(CommonInstructions.getTransitionHelper());
        list.add(CommonInstructions.getPerspectiveModTransitionHelper());
        return list;
    }
}
