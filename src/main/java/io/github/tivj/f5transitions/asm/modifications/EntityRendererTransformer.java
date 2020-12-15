package io.github.tivj.f5transitions.asm.modifications;

import io.github.tivj.f5transitions.asm.GeneralFunctions;
import io.github.tivj.f5transitions.asm.tweaker.transformer.ITransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * I want to apologize for really bad looking code, IDK how to make it efficient and pretty at the same time. I chose efficiency.
 * For more human readable information of what this does, see ASM explainations/EntityRendererTransformer.md
 */
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
                LocalVariableNode blockHitSideVariable = createLocalVariable("blockHitSide", "Lnet/minecraft/util/EnumFacing;", new LabelNode(), new LabelNode(), methodNode.localVariables);
                methodNode.localVariables.add(blockHitSideVariable);
                methodNode.instructions.insert(new VarInsnNode(Opcodes.ASTORE, blockHitSideVariable.index));
                methodNode.instructions.insert(new InsnNode(Opcodes.ACONST_NULL));
                methodNode.instructions.insert(blockHitSideVariable.start);

                LocalVariableNode closestHitDistanceVariable = createLocalVariable("closestHitDistance", "D", new LabelNode(), new LabelNode(), methodNode.localVariables);
                methodNode.localVariables.add(closestHitDistanceVariable);

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
                                    AbstractInsnNode lookupNode = laterNode.getNext();
                                    methodNode.instructions.insert(laterNode, initializeClosestHitDistance(closestHitDistanceVariable));
                                    int occurrences = 0;
                                    while (occurrences < 3) {
                                        if (lookupNode.getOpcode() == Opcodes.DLOAD && ((VarInsnNode) lookupNode).var == 10 && lookupNode.getNext().getOpcode() == Opcodes.DMUL && lookupNode.getNext().getNext().getOpcode() == Opcodes.DSTORE) {
                                            occurrences++;
                                            ((VarInsnNode) lookupNode).var = closestHitDistanceVariable.index;
                                        }
                                        lookupNode = lookupNode.getNext();
                                    }
                                    break;
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
                        LabelNode endLabel = ((JumpInsnNode)node.getPrevious().getPrevious().getPrevious()).label;
                        for (int i = 8; i > 0; i--) {
                            methodNode.instructions.remove(node.getNext());
                            node = node.getPrevious();
                        }

                        methodNode.instructions.insert(node.getNext(), orIfInThirdPersonThenStoreNegative(endLabel, blockHitSideVariable, closestHitDistanceVariable));
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

                iterator = methodNode.instructions.iterator();
                int negativeD3Occurrences = 0;
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (
                            node.getOpcode() == Opcodes.DLOAD && ((VarInsnNode)node).var == 10 &&
                            node.getPrevious().getOpcode() == Opcodes.FCONST_0 &&
                            node.getNext().getOpcode() == Opcodes.DNEG &&
                            node.getNext().getNext().getOpcode() == Opcodes.D2F &&
                            node.getNext().getNext().getNext().getOpcode() == Opcodes.INVOKESTATIC
                    ) {
                        negativeD3Occurrences++;
                        if (negativeD3Occurrences > 1) {
                            methodNode.instructions.insertBefore(node.getNext().getNext().getNext().getNext(), translateZByConstantIfSideHit(blockHitSideVariable));
                            break;
                        }
                    }
                }

                methodNode.instructions.insertBefore(methodNode.instructions.getLast().getPrevious(), translateZIfSideExists(blockHitSideVariable));
            }
        }
    }

    private InsnList translateZByConstantIfSideHit(LocalVariableNode blockHitSideVariable) {
        InsnList list = new InsnList();
        LabelNode end = new LabelNode();

        list.add(new VarInsnNode(Opcodes.ALOAD, blockHitSideVariable.index));
        list.add(new JumpInsnNode(Opcodes.IFNULL, end));
        list.add(new InsnNode(Opcodes.FCONST_0));
        list.add(new InsnNode(Opcodes.FCONST_0));
        list.add(new LdcInsnNode(0.03F));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", "func_179109_b", "(FFF)V", false)); // translate

        list.add(end);
        return list;
    }

    private InsnList translateZIfSideExists(LocalVariableNode blockHitSideVariable) {
        InsnList list = new InsnList();
        LabelNode end = new LabelNode();

        list.add(new VarInsnNode(Opcodes.ALOAD, blockHitSideVariable.index));
        list.add(new JumpInsnNode(Opcodes.IFNULL, end));

        list.add(new VarInsnNode(Opcodes.ALOAD, blockHitSideVariable.index));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/util/EnumFacing", "func_176730_m", "()Lnet/minecraft/util/Vec3i;", false)); // getDirectionVec
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/util/Vec3i", "func_177958_n", "()I", false)); // getX
        list.add(new InsnNode(Opcodes.INEG));
        list.add(new InsnNode(Opcodes.I2F));
        list.add(new LdcInsnNode(0.06F));
        list.add(new InsnNode(Opcodes.FMUL));

        list.add(new VarInsnNode(Opcodes.ALOAD, blockHitSideVariable.index));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/util/EnumFacing", "func_176730_m", "()Lnet/minecraft/util/Vec3i;", false)); // getDirectionVec
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/util/Vec3i", "func_177956_o", "()I", false)); // getY
        list.add(new InsnNode(Opcodes.INEG));
        list.add(new InsnNode(Opcodes.I2F));
        list.add(new LdcInsnNode(0.06F));
        list.add(new InsnNode(Opcodes.FMUL));

        list.add(new VarInsnNode(Opcodes.ALOAD, blockHitSideVariable.index));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/util/EnumFacing", "func_176730_m", "()Lnet/minecraft/util/Vec3i;", false)); // getDirectionVec
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/util/Vec3i", "func_177952_p", "()I", false)); // getZ
        list.add(new InsnNode(Opcodes.INEG));
        list.add(new InsnNode(Opcodes.I2F));
        list.add(new LdcInsnNode(0.06F));
        list.add(new InsnNode(Opcodes.FMUL));

        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", "func_179109_b", "(FFF)V", false)); // translate
        list.add(blockHitSideVariable.end);
        list.add(end);
        return list;
    }

    /** same as:
        if (hitDistance < 4D) {
            if (hitDistance < distance) {
                distance = hitDistance;
            } else if (distance < 0D && -hitDistance > distance) {
                distance = -hitDistance;
            }
        }

        if (hitDistance < closestHitDistance) {
            closestHitDistance = hitDistance;
            side = movingobjectposition.sideHit;
        } else if (closestHitDistance < 0D && -hitDistance > closestHitDistance) {
            closestHitDistance = -hitDistance;
            side = movingobjectposition.sideHit;
        }
     */
    private InsnList orIfInThirdPersonThenStoreNegative(LabelNode end, LocalVariableNode blockHitSideVariable, LocalVariableNode closestHitDistanceVariable) {
        InsnList list = new InsnList();

        LabelNode checkIfNegativeDistance = new LabelNode();
        LabelNode checkIfClosestHitDistanceShouldBeChanged = new LabelNode();
        LabelNode checkIfNegativeClosestDistance = new LabelNode();

        list.add(new VarInsnNode(Opcodes.DLOAD, 25));
        list.add(new LdcInsnNode(4D));
        list.add(new InsnNode(Opcodes.DCMPG));
        list.add(new JumpInsnNode(Opcodes.IFGE, checkIfClosestHitDistanceShouldBeChanged));

        list.add(new VarInsnNode(Opcodes.DLOAD, 25));
        list.add(new VarInsnNode(Opcodes.DLOAD, 10));
        list.add(new InsnNode(Opcodes.DCMPG));
        list.add(new JumpInsnNode(Opcodes.IFGE, checkIfNegativeDistance));

        list.add(new VarInsnNode(Opcodes.DLOAD, 25));
        list.add(new VarInsnNode(Opcodes.DSTORE, 10));
        list.add(new JumpInsnNode(Opcodes.GOTO, checkIfClosestHitDistanceShouldBeChanged));

        list.add(checkIfNegativeDistance);
        list.add(new VarInsnNode(Opcodes.DLOAD, 10));
        list.add(new InsnNode(Opcodes.DCONST_0));
        list.add(new InsnNode(Opcodes.DCMPG));
        list.add(new JumpInsnNode(Opcodes.IFGE, checkIfClosestHitDistanceShouldBeChanged));
        list.add(new VarInsnNode(Opcodes.DLOAD, 25));
        list.add(new InsnNode(Opcodes.DNEG));
        list.add(new VarInsnNode(Opcodes.DLOAD, 10));
        list.add(new InsnNode(Opcodes.DCMPL));
        list.add(new JumpInsnNode(Opcodes.IFLE, checkIfClosestHitDistanceShouldBeChanged));

        list.add(new VarInsnNode(Opcodes.DLOAD, 25));
        list.add(new InsnNode(Opcodes.DNEG));
        list.add(new VarInsnNode(Opcodes.DSTORE, 10));

        list.add(checkIfClosestHitDistanceShouldBeChanged);
        list.add(new VarInsnNode(Opcodes.DLOAD, 25));
        list.add(new VarInsnNode(Opcodes.DLOAD, closestHitDistanceVariable.index));
        list.add(new InsnNode(Opcodes.DCMPG));
        list.add(new JumpInsnNode(Opcodes.IFGE, checkIfNegativeClosestDistance));

        list.add(new VarInsnNode(Opcodes.DLOAD, 25));
        list.add(new VarInsnNode(Opcodes.DSTORE, closestHitDistanceVariable.index));

        list.add(new VarInsnNode(Opcodes.ALOAD, 24));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/util/MovingObjectPosition", "field_178784_b", "Lnet/minecraft/util/EnumFacing;")); // sideHit
        list.add(new VarInsnNode(Opcodes.ASTORE, blockHitSideVariable.index));
        list.add(new JumpInsnNode(Opcodes.GOTO, end));

        list.add(checkIfNegativeClosestDistance);
        list.add(new VarInsnNode(Opcodes.DLOAD, closestHitDistanceVariable.index));
        list.add(new InsnNode(Opcodes.DCONST_0));
        list.add(new InsnNode(Opcodes.DCMPG));
        list.add(new JumpInsnNode(Opcodes.IFGE, end));
        list.add(new VarInsnNode(Opcodes.DLOAD, 25));
        list.add(new InsnNode(Opcodes.DNEG));
        list.add(new VarInsnNode(Opcodes.DLOAD, closestHitDistanceVariable.index));
        list.add(new InsnNode(Opcodes.DCMPL));
        list.add(new JumpInsnNode(Opcodes.IFLE, end));

        list.add(new VarInsnNode(Opcodes.DLOAD, 25));
        list.add(new InsnNode(Opcodes.DNEG));
        list.add(new VarInsnNode(Opcodes.DSTORE, closestHitDistanceVariable.index));

        list.add(new VarInsnNode(Opcodes.ALOAD, 24));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/util/MovingObjectPosition", "field_178784_b", "Lnet/minecraft/util/EnumFacing;")); // sideHit
        list.add(new VarInsnNode(Opcodes.ASTORE, blockHitSideVariable.index));

        list.add(closestHitDistanceVariable.end);
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

    private InsnList rotateYrotationBonus() {
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

    private InsnList initializeClosestHitDistance(LocalVariableNode closestHitDistanceVariable) {
        InsnList list = new InsnList();
        list.add(closestHitDistanceVariable.start);
        list.add(new VarInsnNode(Opcodes.DLOAD, 10));

            list.add(new VarInsnNode(Opcodes.DLOAD, 10));
            list.add(new InsnNode(Opcodes.DCONST_0));
            list.add(new InsnNode(Opcodes.DCMPL));
            LabelNode ifLess = new LabelNode();
            list.add(new JumpInsnNode(Opcodes.IFLE, ifLess));

            list.add(new InsnNode(Opcodes.DCONST_1));
            LabelNode continue_ = new LabelNode();
            list.add(new JumpInsnNode(Opcodes.GOTO, continue_));

            list.add(ifLess);
            list.add(new LdcInsnNode(-1D));

        list.add(continue_);
        list.add(new InsnNode(Opcodes.DADD));
        list.add(new VarInsnNode(Opcodes.DSTORE, closestHitDistanceVariable.index));
        return list;
    }
}