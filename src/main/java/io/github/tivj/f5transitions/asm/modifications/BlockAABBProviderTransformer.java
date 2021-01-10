package io.github.tivj.f5transitions.asm.modifications;

import io.github.tivj.f5transitions.asm.tweaker.transformer.ITransformer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;
import java.util.Set;

public class BlockAABBProviderTransformer {
    private static final String addAABBstoSetMethodName = "addAllBoundingBoxesToSetForCameraRayTracing";
    private static final String addAABBstoSetMethodDesc = "(Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;Lnet/minecraft/block/state/IBlockState;Ljava/util/Set;)V";
    private static final String addAABBstoSetMethodSingature = "(Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;Lnet/minecraft/block/state/IBlockState;Ljava/util/Set<Lnet/minecraft/util/AxisAlignedBB;>;)V";

    public static class BlockTransformer implements ITransformer {
        @Override
        public String[] getClassName() {
            return new String[]{"net.minecraft.block.Block"};
        }

        @Override
        public void transform(ClassNode classNode, String name) {
            MethodNode methodNode = new MethodNode(Opcodes.ACC_PUBLIC, addAABBstoSetMethodName, addAABBstoSetMethodDesc, addAABBstoSetMethodSingature, null);
            methodNode.instructions.add(new LabelNode());
            methodNode.instructions.add(new InsnNode(Opcodes.RETURN));
            classNode.methods.add(methodNode);
        }
    }

    public static class BlockStairsTransformer implements ITransformer {
        @Override
        public String[] getClassName() {
            return new String[]{"net.minecraft.block.BlockStairs"};
        }

        @Override
        public void transform(ClassNode classNode, String name) {
            MethodNode methodNode = new MethodNode(Opcodes.ACC_PUBLIC, addAABBstoSetMethodName, addAABBstoSetMethodDesc, addAABBstoSetMethodSingature, null);
            methodNode.instructions.add(addAllBoundingBoxesToSetForCameraRayTracing());
            classNode.methods.add(methodNode);
        }

        private InsnList addAllBoundingBoxesToSetForCameraRayTracing() {
            InsnList list = new InsnList();

            LabelNode l2 = new LabelNode();
            LabelNode l3 = new LabelNode();
            LabelNode l5 = new LabelNode();
            LabelNode l6 = new LabelNode();
            LabelNode l9 = new LabelNode();
            LabelNode l10 = new LabelNode();
            LabelNode l13 = new LabelNode();

            list.add(new LabelNode());
            list.add(new VarInsnNode(Opcodes.ALOAD, 3));
            list.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraft/block/BlockStairs", "field_176309_a", "Lnet/minecraft/block/properties/PropertyDirection;")); // FACING
            list.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "net/minecraft/block/state/IBlockState", "func_177229_b", "(Lnet/minecraft/block/properties/IProperty;)Ljava/lang/Comparable;", true)); // getValue
            list.add(new TypeInsnNode(Opcodes.CHECKCAST, "net/minecraft/util/EnumFacing"));
            list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/util/EnumFacing", "func_176736_b", "()I", false)); // getHorizontalIndex
            list.add(new VarInsnNode(Opcodes.ISTORE, 5));

            list.add(new VarInsnNode(Opcodes.ALOAD, 3));
            list.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraft/block/BlockStairs", "field_176308_b", "Lnet/minecraft/block/properties/PropertyEnum;")); // HALF
            list.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "net/minecraft/block/state/IBlockState", "func_177229_b", "(Lnet/minecraft/block/properties/IProperty;)Ljava/lang/Comparable;", true)); // getValue
            list.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraft/block/BlockStairs$EnumHalf", "TOP", "Lnet/minecraft/block/BlockStairs$EnumHalf;"));
            list.add(new JumpInsnNode(Opcodes.IF_ACMPNE, l2));
            list.add(new InsnNode(Opcodes.ICONST_1));
            list.add(new JumpInsnNode(Opcodes.GOTO, l3));

            list.add(l2);
            list.add(new InsnNode(Opcodes.ICONST_0));

            list.add(l3);
            list.add(new VarInsnNode(Opcodes.ISTORE, 6));

            list.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraft/block/BlockStairs", "field_150150_a", "[[I"));
            list.add(new VarInsnNode(Opcodes.ILOAD, 5));
            list.add(new VarInsnNode(Opcodes.ILOAD, 6));
            list.add(new JumpInsnNode(Opcodes.IFEQ, l5));
            list.add(new InsnNode(Opcodes.ICONST_4));
            list.add(new JumpInsnNode(Opcodes.GOTO, l6));

            list.add(l5);
            list.add(new InsnNode(Opcodes.ICONST_0));

            list.add(l6);
            list.add(new InsnNode(Opcodes.IADD));
            list.add(new InsnNode(Opcodes.AALOAD));
            list.add(new VarInsnNode(Opcodes.ASTORE, 7));

            list.add(new VarInsnNode(Opcodes.ALOAD, 0));
            list.add(new InsnNode(Opcodes.ICONST_1));
            list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/block/BlockStairs", "field_150152_N", "Z")); // hasRaytraced

            list.add(new InsnNode(Opcodes.ICONST_0));
            list.add(new VarInsnNode(Opcodes.ISTORE, 8));

            list.add(l9);
            list.add(new VarInsnNode(Opcodes.ILOAD, 8));
            list.add(new IntInsnNode(Opcodes.BIPUSH, 8));
            list.add(new JumpInsnNode(Opcodes.IF_ICMPGE, l10));


            list.add(new VarInsnNode(Opcodes.ALOAD, 0));
            list.add(new VarInsnNode(Opcodes.ILOAD, 8));
            list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/block/BlockStairs", "field_150153_O", "I")); // rayTracePass

            list.add(new VarInsnNode(Opcodes.ALOAD, 7));
            list.add(new VarInsnNode(Opcodes.ILOAD, 8));
            list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/util/Arrays", "binarySearch", "([II)I", false));
            list.add(new JumpInsnNode(Opcodes.IFGE, l13));

            list.add(new VarInsnNode(Opcodes.ALOAD, 0));
            list.add(new VarInsnNode(Opcodes.ALOAD, 1));
            list.add(new VarInsnNode(Opcodes.ALOAD, 2));
            list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/block/BlockStairs", "func_180654_a", "(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/BlockPos;)V", false)); // setBlockBoundsBasedOnState

            list.add(new VarInsnNode(Opcodes.ALOAD, 4));
            list.add(new TypeInsnNode(Opcodes.NEW, "net/minecraft/util/AxisAlignedBB"));
            list.add(new InsnNode(Opcodes.DUP));
            list.add(new VarInsnNode(Opcodes.ALOAD, 0));
            list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/block/BlockStairs", "field_149759_B", "D")); // minX
            list.add(new VarInsnNode(Opcodes.ALOAD, 0));
            list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/block/BlockStairs", "field_149760_C", "D")); // minY
            list.add(new VarInsnNode(Opcodes.ALOAD, 0));
            list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/block/BlockStairs", "field_149754_D", "D")); // minZ
            list.add(new VarInsnNode(Opcodes.ALOAD, 0));
            list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/block/BlockStairs", "field_149755_E", "D")); // maxX
            list.add(new VarInsnNode(Opcodes.ALOAD, 0));
            list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/block/BlockStairs", "field_149756_F", "D")); // maxY
            list.add(new VarInsnNode(Opcodes.ALOAD, 0));
            list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/block/BlockStairs", "field_149757_G", "D")); // maxZ
            list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/minecraft/util/AxisAlignedBB", "<init>", "(DDDDDD)V", false));
            list.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "java/util/Set", "add", "(Ljava/lang/Object;)Z", true));
            list.add(new InsnNode(Opcodes.POP));

            list.add(l13);
            list.add(new IincInsnNode(8, 1));
            list.add(new JumpInsnNode(Opcodes.GOTO, l9));

            list.add(l10);
            list.add(new InsnNode(Opcodes.RETURN));

            return list;
        }
    }

    public static class PlaceholderMethodReplacerTransformer implements ITransformer {
        @Override
        public String[] getClassName() {
            return new String[]{"io.github.tivj.f5transitions.asm.BlockPlaceholder"};
        }

        @Override
        public void transform(ClassNode classNode, String name) {
            classNode.methods.get(1).instructions.insert(callRealMethod());
        }

        private InsnList callRealMethod() {
            InsnList list = new InsnList();
            list.add(new LabelNode());
            list.add(new VarInsnNode(Opcodes.ALOAD, 0));
            list.add(new VarInsnNode(Opcodes.ALOAD, 1));
            list.add(new VarInsnNode(Opcodes.ALOAD, 2));
            list.add(new VarInsnNode(Opcodes.ALOAD, 3));
            list.add(new VarInsnNode(Opcodes.ALOAD, 4));
            list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/block/Block", addAABBstoSetMethodName, addAABBstoSetMethodDesc, false));
            return list;
        }
    }
}