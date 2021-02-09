package io.github.tivj.f5transitions.asm.modifications;

import io.github.tivj.f5transitions.asm.CommonInstructions;
import io.github.tivj.f5transitions.asm.tweaker.transformer.ITransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class EntityRendererTransformer implements ITransformer {
    public static FieldNode transitionHelper = new FieldNode(Opcodes.ACC_PUBLIC, "perspectiveTransitionHelper", "Lio/github/tivj/f5transitions/TransitionHelper;", null, null);

    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.EntityRenderer"};
    }

    @Override
    public List<String> debuggableClass() {
        List<String> list = new ArrayList<>();
        list.add("orientCamera");
        return list;
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
                                AbstractInsnNode removableNode = node.getNext().getNext().getNext().getNext();
                                for (int i = 0; i < 11; i++) {
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
                                AbstractInsnNode laterNode = node.getNext().getNext().getNext().getNext().getNext().getNext().getNext();
                                if (
                                        laterNode.getPrevious().getOpcode() == Opcodes.LDC &&
                                        ((LdcInsnNode) laterNode.getPrevious()).cst.equals(180F) &&
                                        laterNode.getNext().getOpcode() == Opcodes.FSTORE &&
                                        ((VarInsnNode) laterNode.getNext()).var == 13
                                ) {
                                    // removes `f2 += 180.0F` and it's if statement
                                    for (int i = 12; i > 0; i--) {
                                        if (!(laterNode.getNext() instanceof LabelNode || laterNode.getNext() instanceof LineNumberNode)) {
                                            methodNode.instructions.remove(laterNode.getNext());
                                        }
                                        laterNode = laterNode.getPrevious();
                                    }

                                    // changes multiplier of end vector
                                    AbstractInsnNode lookupNode = laterNode.getNext();
                                    int occurrences = 0;
                                    while (occurrences < 3) {
                                        if (lookupNode.getOpcode() == Opcodes.DLOAD && ((VarInsnNode) lookupNode).var == 10 && lookupNode.getNext().getOpcode() == Opcodes.DMUL && lookupNode.getNext().getNext().getOpcode() == Opcodes.DSTORE) {
                                            occurrences++;
                                            methodNode.instructions.insert(lookupNode, new InsnNode(Opcodes.DADD));
                                            methodNode.instructions.insert(lookupNode, new LdcInsnNode(0.3D));
                                        }
                                        lookupNode = lookupNode.getNext();
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }

                int[] rayTraceModifierIndexes = new int[]{21, 21, 22, 22, 23, 23};
                for (int i = 0; i < methodNode.instructions.size(); i++) {
                    AbstractInsnNode node = methodNode.instructions.get(i);
                    if (node.getOpcode() == Opcodes.FSTORE) {
                        for (int index = 0; index < rayTraceModifierIndexes.length; index++) {
                            if (((VarInsnNode) node).var == rayTraceModifierIndexes[index]) {
                                if (node.getPrevious().getOpcode() == Opcodes.I2F) {
                                    methodNode.instructions.insertBefore(node, new LdcInsnNode(0.1F));
                                    methodNode.instructions.insertBefore(node, new InsnNode(Opcodes.FMUL));
                                    i += 2;
                                } else if (node.getPrevious().getOpcode() == Opcodes.FMUL) {
                                    methodNode.instructions.remove(node.getPrevious().getPrevious().getPrevious());
                                    methodNode.instructions.remove(node.getPrevious().getPrevious());
                                    methodNode.instructions.remove(node.getPrevious());
                                    methodNode.instructions.remove(node);
                                    i -= 4;
                                } else {
                                    continue;
                                }
                                rayTraceModifierIndexes[index] = -1;
                                break;
                            }
                        }

                        boolean shouldBreak = true;
                        for (int index : rayTraceModifierIndexes) {
                            if (index != -1) {
                                shouldBreak = false;
                                break;
                            }
                        }
                        if (shouldBreak) break;
                    }
                }

                iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (
                        node.getOpcode() == Opcodes.INVOKEVIRTUAL &&
                        node.getNext().getOpcode() == Opcodes.ASTORE &&
                        ((VarInsnNode)node.getNext()).var == 24
                    ) {
                        String invokeName = mapMethodNameFromNode(node);
                        if (invokeName.equals("rayTraceBlocks") || invokeName.equals("func_72933_a")) {
                            methodNode.instructions.set(node, new MethodInsnNode(Opcodes.INVOKESTATIC, "io/github/tivj/f5transitions/utils/RaytracingUtil", "rayTraceBlocks", "(Lnet/minecraft/world/World;Lnet/minecraft/util/Vec3;Lnet/minecraft/util/Vec3;)Lnet/minecraft/util/MovingObjectPosition;", false));
                            break;
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
                                        methodNode.instructions.insert(laterNode.getNext(), rotateYrotationBonus());

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
        list.add(CommonInstructions.getTransitionHelper());
        list.add(CommonInstructions.updatePerspectiveTransitions());
        return list;
    }

    private InsnList rotateYrotationBonus() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(CommonInstructions.getTransitionHelper());
        list.add(new VarInsnNode(Opcodes.FLOAD, 1));
        list.add(CommonInstructions.getYrotationBonus());
        return list;
    }

    private InsnList getCameraDistance() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(CommonInstructions.getTransitionHelper());
        list.add(new VarInsnNode(Opcodes.FLOAD, 1));
        list.add(CommonInstructions.getCameraDistance());
        return list;
    }

    private InsnList orIfTransitionNotFinished(LabelNode ifTrue, LabelNode elseLabel) {
        InsnList list = new InsnList();
        list.add(new JumpInsnNode(Opcodes.IFGT, ifTrue));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(CommonInstructions.getTransitionHelper());
        list.add(CommonInstructions.isTransitionActive());
        list.add(new JumpInsnNode(Opcodes.IFEQ, elseLabel));
        return list;
    }
}