package io.github.tivj.f5transitions.asm.modifications.compatibility;

import io.github.tivj.f5transitions.asm.CommonInstructions;
import io.github.tivj.f5transitions.asm.tweaker.transformer.ITransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class PerspectiveModTransformer implements ITransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"me.djtheredstoner.perspectivemod.PerspectiveMod"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals("enterPerspective")) {
                methodNode.instructions.insert(changePerspective(true));
            } else if (methodNode.name.equals("resetPerspective")) {
                methodNode.instructions.insert(changePerspective(false));
            }
        }
    }

    private InsnList changePerspective(boolean enter) {
        InsnList list = new InsnList();
        list.add(CommonInstructions.getMinecraftInstance());
        list.add(CommonInstructions.getEntityRendererFromMCInstance());
        list.add(CommonInstructions.getTransitionHelper());

        if (enter) {
            list.add(new InsnNode(Opcodes.ICONST_1));
        } else {
            list.add(new VarInsnNode(Opcodes.ALOAD, 0));
            list.add(new FieldInsnNode(Opcodes.GETFIELD, "me/djtheredstoner/perspectivemod/PerspectiveMod", "previousPerspective", "I"));
        }
        list.add(CommonInstructions.getPerspectiveFromID());

        list.add(new InsnNode(Opcodes.ICONST_1));
        list.add(CommonInstructions.changePerspective());
        return list;
    }
}
