package io.github.tivj.f5transitions.asm.modifications;

import io.github.tivj.f5transitions.asm.GeneralFunctions;
import io.github.tivj.f5transitions.asm.tweaker.transformer.ITransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

/**
 * I want to apologize for really bad looking code, IDK how to make it efficient and pretty at the same time. I chose efficiency.
 * For more human readable information of what this does, see https://gist.github.com/UserTeemu/ad8da79ad8ba3fbdfcf2c1b3ad6a01fc
 * Note: the gist uses codebase of the prototype of the mod
 */
public class EntityRendererTransformer implements ITransformer {
    public static FieldNode transitionHelper = new FieldNode(Opcodes.ACC_PUBLIC, "perspectiveTransitionHelper", "Lio/github/tivj/f5transitions/TransitionHelper;", null, null);

    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.EntityRenderer"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        classNode.fields.add(transitionHelper);
        for (MethodNode methodNode : classNode.methods) {
            String methodName = mapMethodName(classNode, methodNode);

            if (methodNode.name.equals("<init>")) {
                methodNode.instructions.insert(methodNode.instructions.getFirst().getNext().getNext().getNext().getNext(), initTransitionHelper());
            } else if (methodName.equals("updateRenderer") || methodName.equals("func_78464_a")) {
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == Opcodes.PUTFIELD && node.getPrevious().getOpcode() == Opcodes.GETFIELD) {
                        String putField = mapFieldNameFromNode(node);
                        String getField = mapFieldNameFromNode(node.getPrevious());
                        if ((putField.equals("thirdPersonDistanceTemp") || putField.equals("field_78491_C")) && (getField.equals("thirdPersonDistance") || getField.equals("field_78490_B"))) {
                            methodNode.instructions.insert(node, updatePerspectiveTransitions());

                            methodNode.instructions.remove(node.getPrevious().getPrevious().getPrevious());
                            methodNode.instructions.remove(node.getPrevious().getPrevious());
                            methodNode.instructions.remove(node.getPrevious());
                            methodNode.instructions.remove(node);
                            break;
                        }
                    }
                }
            } else if (methodName.equals("orientCamera") || methodName.equals("func_78467_g")) {
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == Opcodes.GETFIELD) {
                        String fieldName = mapFieldNameFromNode(node);
                        if (fieldName.equals("thirdPersonView") || fieldName.equals("field_74320_O")) {
                            if (node.getNext().getOpcode() == Opcodes.IFLE && node.getNext().getNext() instanceof LabelNode) {
                                methodNode.instructions.insert(node.getNext(), orIfTransitionNotFinished((LabelNode) node.getNext().getNext(), ((JumpInsnNode) node.getNext()).label));
                                methodNode.instructions.remove(node.getNext());
                                break;
                            }
                        }
                    }
                }

                iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == Opcodes.GETFIELD) {
                        String fieldName = mapFieldNameFromNode(node);
                        if (fieldName.equals("thirdPersonDistanceTemp") || fieldName.equals("field_78491_C")) {
                            if (node.getNext().getOpcode() == Opcodes.FSUB && node.getPrevious().getOpcode() == Opcodes.ALOAD && node.getNext().getNext().getNext().getNext().getOpcode() == Opcodes.FADD) {
                                AbstractInsnNode removableNode = node.getNext().getNext().getNext();
                                for (int i = 0; i < 10; i++) {
                                    methodNode.instructions.remove(removableNode.getNext());
                                    removableNode = removableNode.getPrevious();
                                }

                                methodNode.instructions.insert(removableNode.getNext(), getCameraDistance());
                                break;
                            }
                        }
                    }
                }

                iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == Opcodes.GETFIELD) {
                        String fieldName = mapFieldNameFromNode(node);
                        if (fieldName.equals("thirdPersonView") || fieldName.equals("field_74320_O")) {
                            if (node.getNext().getOpcode() == Opcodes.ICONST_2 && node.getNext().getNext().getOpcode() == Opcodes.IF_ICMPNE && node.getNext().getNext().getNext() instanceof LabelNode) {
                                AbstractInsnNode laterNode = node.getNext().getNext().getNext().getNext().getNext().getNext();

                                if ( // this is complicated, but I want to make sure that I am removing the correct things, removes `f2 += 180.0F` and it's if statement
                                        laterNode.getOpcode() == Opcodes.LDC &&
                                        ((LdcInsnNode) laterNode).cst.equals(180F) &&
                                        laterNode.getNext().getNext().getOpcode() == Opcodes.FSTORE &&
                                        ((VarInsnNode) laterNode.getNext().getNext()).var == 13
                                ) {
                                    laterNode = laterNode.getNext();
                                    for (int i = 12; i > 0; i--) {
                                        if (!(laterNode.getNext() instanceof LabelNode || laterNode.getNext() instanceof LineNumberNode)) {
                                            methodNode.instructions.remove(laterNode.getNext());
                                        }
                                        laterNode = laterNode.getPrevious();
                                    }
                                }
                            }
                        }
                    }
                }

                iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (
                        node.getOpcode() == Opcodes.DLOAD && ((VarInsnNode)node).var == 25 &&
                            node.getNext().getOpcode() == Opcodes.DSTORE && ((VarInsnNode)node.getNext()).var == 10 &&
                            node.getPrevious().getPrevious().getPrevious().getOpcode() == Opcodes.IFGE
                    ) {
                        LabelNode ifNegative = new LabelNode();
                        LabelNode elseLabel = ((JumpInsnNode)node.getPrevious().getPrevious().getPrevious()).label;
                        ((JumpInsnNode)node.getPrevious().getPrevious().getPrevious()).label = ifNegative;
                        methodNode.instructions.insert(node.getNext(), orIfInThirdPersonThenStoreNegative(ifNegative, elseLabel));
                        break;
                    }
                }

                iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == Opcodes.GETFIELD) {
                        String fieldName = mapFieldNameFromNode(node);
                        if (fieldName.equals("thirdPersonView") || fieldName.equals("field_74320_O")) {
                            if (node.getNext().getOpcode() == Opcodes.ICONST_2 && node.getNext().getNext().getOpcode() == Opcodes.IF_ICMPNE && node.getNext().getNext().getNext() instanceof LabelNode) {
                                AbstractInsnNode laterNode = node.getNext().getNext().getNext().getNext().getNext().getNext();

                                if ( // removes `GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F)` and it's if statement
                                        laterNode.getPrevious().getOpcode() == Opcodes.LDC &&
                                        ((LdcInsnNode) laterNode.getPrevious()).cst.equals(180F) &&
                                        laterNode.getNext().getOpcode() == Opcodes.FCONST_1 &&
                                        laterNode.getNext().getNext().getNext().getOpcode() == Opcodes.INVOKESTATIC
                                ) {
                                    String invokeName = mapMethodNameFromNode(laterNode.getNext().getNext().getNext());
                                    if (invokeName.equals("rotate") || invokeName.equals("func_179114_b")) {
                                        laterNode = laterNode.getPrevious().getPrevious();
                                        methodNode.instructions.insert(laterNode.getNext(), getYrotationBonus());

                                        for (int i = 9; i > 0; i--) { // remove unwanted nodes
                                            if (!(laterNode.getNext() instanceof LabelNode || laterNode.getNext() instanceof LineNumberNode)) {
                                                methodNode.instructions.remove(laterNode.getNext());
                                            }
                                            laterNode = laterNode.getPrevious();
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private InsnList orIfInThirdPersonThenStoreNegative(LabelNode ifNegative, LabelNode elseLabel) {
        InsnList list = new InsnList();
        list.add(new JumpInsnNode(Opcodes.GOTO, elseLabel));

        list.add(ifNegative);
        list.add(new VarInsnNode(Opcodes.DLOAD, 10));
        list.add(new InsnNode(Opcodes.DCONST_0));
        list.add(new InsnNode(Opcodes.DCMPG));
        list.add(new JumpInsnNode(Opcodes.IFGE, elseLabel));

        list.add(new VarInsnNode(Opcodes.DLOAD, 25));
        list.add(new InsnNode(Opcodes.DNEG));
        list.add(new VarInsnNode(Opcodes.DLOAD, 10));
        list.add(new InsnNode(Opcodes.DCMPL));
        list.add(new JumpInsnNode(Opcodes.IFLE, elseLabel));

        list.add(new VarInsnNode(Opcodes.DLOAD, 25));
        list.add(new InsnNode(Opcodes.DNEG));
        list.add(new VarInsnNode(Opcodes.DSTORE, 10));
        return list;
    }

    private InsnList initTransitionHelper() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new TypeInsnNode(Opcodes.NEW, "io/github/tivj/f5transitions/TransitionHelper"));
        list.add(new InsnNode(Opcodes.DUP));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "io/github/tivj/f5transitions/TransitionHelper", "<init>", "(Lnet/minecraft/client/renderer/EntityRenderer;Lnet/minecraft/client/Minecraft;)V", false));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/client/renderer/EntityRenderer", transitionHelper.name, transitionHelper.desc));
        return list;
    }

    private InsnList updatePerspectiveTransitions() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/EntityRenderer", transitionHelper.name, transitionHelper.desc));
        list.add(GeneralFunctions.updatePerspectiveTransitions());
        return list;
    }

    private InsnList getYrotationBonus() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/EntityRenderer", transitionHelper.name, transitionHelper.desc));
        list.add(new VarInsnNode(Opcodes.FLOAD, 1));
        list.add(GeneralFunctions.getYrotationBonus());
        return list;
    }

    private InsnList getCameraDistance() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/EntityRenderer", transitionHelper.name, transitionHelper.desc));
        list.add(new VarInsnNode(Opcodes.FLOAD, 1));
        list.add(GeneralFunctions.getCameraDistance());
        return list;
    }

    private InsnList orIfTransitionNotFinished(LabelNode ifTrue, LabelNode elseLabel) {
        InsnList list = new InsnList();
        list.add(new JumpInsnNode(Opcodes.IFGT, ifTrue));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/EntityRenderer", transitionHelper.name, transitionHelper.desc));
        list.add(GeneralFunctions.isTransitionActive());
        list.add(new JumpInsnNode(Opcodes.IFEQ, elseLabel));
        return list;
    }
}