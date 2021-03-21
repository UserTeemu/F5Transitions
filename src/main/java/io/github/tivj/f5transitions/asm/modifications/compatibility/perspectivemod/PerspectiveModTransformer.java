package io.github.tivj.f5transitions.asm.modifications.compatibility.perspectivemod;

import io.github.tivj.f5transitions.asm.CommonInstructions;
import io.github.tivj.f5transitions.asm.tweaker.transformer.ITransformer;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

public class PerspectiveModTransformer implements ITransformer {
    public static FieldNode exitTransitionActive = new FieldNode(Opcodes.ACC_PUBLIC, "exitTransitionActive", "Z", null, null);
    public static MethodNode onTransitionFinishedLambda = new MethodNode(Opcodes.ACC_SYNTHETIC | Opcodes.ACC_PRIVATE, "lambda$setTransitionFinishCallback$0", "()V", null, null);

    @Override
    public String[] getClassName() {
        return new String[]{"me.djtheredstoner.perspectivemod.PerspectiveMod"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        classNode.fields.add(exitTransitionActive);
        classNode.methods.add(onTransitionFinishedLambda);
        onTransitionFinishedLambda.instructions = transitionFinishCallback();

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals("enterPerspective")) {
                methodNode.instructions.insert(beforeEnter());
            } else if (methodNode.name.equals("resetPerspective")) {
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == Opcodes.PUTFIELD && node.getPrevious().getOpcode() == Opcodes.ICONST_0 && node.getPrevious().getPrevious().getOpcode() == Opcodes.ALOAD) {
                        if (((FieldInsnNode) node).name.equals("perspectiveToggled") && ((VarInsnNode) node.getPrevious().getPrevious()).var == 0) {
                            methodNode.instructions.remove(node.getPrevious().getPrevious());
                            methodNode.instructions.remove(node.getPrevious());
                            methodNode.instructions.remove(node);
                            break;
                        }
                    }
                }

                methodNode.instructions.insert(startExitTransition());
            } else if (methodNode.name.equals("onPressed")) {
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (
                            node.getOpcode() == Opcodes.PUTFIELD &&
                            node.getPrevious().getOpcode() == Opcodes.GETFIELD
                    ) {
                        String previousFieldName = mapFieldNameFromNode(node.getPrevious());
                        if (((FieldInsnNode) node).name.equals("cameraYaw") && (previousFieldName.equals("rotationYaw") || previousFieldName.equals("field_70177_z"))) {
                            AbstractInsnNode otherNode = node.getPrevious().getPrevious().getPrevious().getPrevious().getPrevious().getPrevious().getPrevious().getPrevious();
                            if (otherNode.getOpcode() == Opcodes.IFEQ && otherNode.getPrevious().getOpcode() == Opcodes.ILOAD) {
                                otherNode = otherNode.getNext().getNext();
                                for (int i = 0; i < 16; i++) {
                                    methodNode.instructions.remove(otherNode.getNext());
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private InsnList beforeEnter() {
        InsnList list = new InsnList();
        LabelNode elseLabel = new LabelNode();

        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "me/djtheredstoner/perspectivemod/PerspectiveMod", exitTransitionActive.name, exitTransitionActive.desc));
        list.add(new JumpInsnNode(Opcodes.IFEQ, elseLabel));

        list.add(new InsnNode(Opcodes.RETURN));

        list.add(elseLabel);

        list.add(storeFacing());
        list.add(changePerspective(true));
        return list;
    }

    private InsnList changePerspective(boolean enter) {
        InsnList list = new InsnList();

        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "me/djtheredstoner/perspectivemod/PerspectiveMod", "mc", "Lnet/minecraft/client/Minecraft;"));
        list.add(CommonInstructions.getEntityRendererFromMCInstance());
        list.add(CommonInstructions.getTransitionHelper());

        if (enter) {
            list.add(new InsnNode(Opcodes.ICONST_3)); // DJPerspectiveModPerspective
        } else {
            list.add(new VarInsnNode(Opcodes.ALOAD, 0));
            list.add(new FieldInsnNode(Opcodes.GETFIELD, "me/djtheredstoner/perspectivemod/PerspectiveMod", "previousPerspective", "I"));
        }
        list.add(CommonInstructions.getPerspectiveFromID());
        list.add(new InsnNode(Opcodes.ICONST_1));
        list.add(CommonInstructions.changePerspective());
        return list;
    }

    private InsnList startExitTransition() {
        InsnList list = new InsnList();
        LabelNode elseLabel = new LabelNode();

        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "me/djtheredstoner/perspectivemod/PerspectiveMod", exitTransitionActive.name, exitTransitionActive.desc));
        list.add(new JumpInsnNode(Opcodes.IFNE, elseLabel));

        list.add(changePerspective(false));

        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "me/djtheredstoner/perspectivemod/PerspectiveMod", "mc", "Lnet/minecraft/client/Minecraft;"));
        list.add(CommonInstructions.getEntityRendererFromMCInstance());
        list.add(CommonInstructions.getTransitionHelper());

        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(
                new InvokeDynamicInsnNode(
                        "run",
                        "(Lme/djtheredstoner/perspectivemod/PerspectiveMod;)Ljava/lang/Runnable;",
                        new Handle(
                                Opcodes.H_INVOKESTATIC,
                                "java/lang/invoke/LambdaMetafactory",
                                "metafactory",
                                "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;"
                        ),
                        Type.getType("()V"),
                        new Handle(Opcodes.H_INVOKESPECIAL, "me/djtheredstoner/perspectivemod/PerspectiveMod", onTransitionFinishedLambda.name, onTransitionFinishedLambda.desc),
                        Type.getType("()V")
                )
        );
        list.add(CommonInstructions.setTransitionFinishCallback());

        list.add(elseLabel);
        return list;
    }

    private InsnList transitionFinishCallback() {
        InsnList list = new InsnList();
        list.add(new LabelNode());

        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new InsnNode(Opcodes.ICONST_0));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "me/djtheredstoner/perspectivemod/PerspectiveMod", "perspectiveToggled", "Z"));

        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new InsnNode(Opcodes.ICONST_0));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "me/djtheredstoner/perspectivemod/PerspectiveMod", exitTransitionActive.name, exitTransitionActive.desc));

        list.add(storeFacing());

        list.add(new InsnNode(Opcodes.RETURN));
        list.add(new LabelNode());
        return list;
    }

    private InsnList storeFacing() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "me/djtheredstoner/perspectivemod/PerspectiveMod", "mc", "Lnet/minecraft/client/Minecraft;"));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/Minecraft", "field_71439_g", "Lnet/minecraft/client/entity/EntityPlayerSP;")); // thePlayer
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/entity/EntityPlayerSP", "field_70177_z", "F")); // rotationYaw
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "me/djtheredstoner/perspectivemod/PerspectiveMod", "cameraYaw", "F"));

        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "me/djtheredstoner/perspectivemod/PerspectiveMod", "mc", "Lnet/minecraft/client/Minecraft;"));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/Minecraft", "field_71439_g", "Lnet/minecraft/client/entity/EntityPlayerSP;")); // thePlayer
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/entity/EntityPlayerSP", "field_70125_A", "F")); // rotationPitch
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "me/djtheredstoner/perspectivemod/PerspectiveMod", "cameraPitch", "F"));
        return list;
    }
}
