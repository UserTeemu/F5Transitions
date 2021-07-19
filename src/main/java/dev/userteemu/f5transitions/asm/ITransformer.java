package dev.userteemu.f5transitions.asm;

import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.tree.*;

import java.util.HashSet;
import java.util.List;

public interface ITransformer {
    String[] getClassName();
    void transform(ClassNode classNode, String name);

    default String mapMethodName(ClassNode classNode, MethodNode methodNode) {
        return FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(classNode.name, methodNode.name, methodNode.desc);
    }

    default String mapMethodDesc(MethodNode methodNode) {
        return FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(methodNode.desc);
    }

    default String mapMethodNameFromNode(AbstractInsnNode node) {
        MethodInsnNode methodInsnNode = (MethodInsnNode) node;
        return FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(methodInsnNode.owner, methodInsnNode.name, methodInsnNode.desc);
    }
    default String mapFieldNameFromNode(AbstractInsnNode node) {
        FieldInsnNode methodInsnNode = (FieldInsnNode) node;
        return FMLDeobfuscatingRemapper.INSTANCE.mapFieldName(methodInsnNode.owner, methodInsnNode.name, methodInsnNode.desc);
    }

    default boolean isSameFieldReference(AbstractInsnNode node1, AbstractInsnNode node2) {
        FieldInsnNode fieldNode1 = (FieldInsnNode) node1;
        FieldInsnNode fieldNode2 = (FieldInsnNode) node2;

        return fieldNode1.owner.equals(fieldNode2.owner) &&
               fieldNode1.name.equals(fieldNode2.name) &&
               fieldNode1.desc.equals(fieldNode2.desc);
    }

    default LocalVariableNode createLocalVariable(final String name, final String desc, final LabelNode start, final LabelNode end, List<LocalVariableNode> localVariables) {
        HashSet<Integer> usedIndexes = new HashSet<>();
        for (LocalVariableNode localVariable : localVariables) {
            usedIndexes.add(localVariable.index);
            if (localVariable.desc.equals("D") || localVariable.desc.equals("L")) usedIndexes.add(localVariable.index+1);
        }

        boolean needsTwoIndexes = desc.equals("D") || desc.equals("L");
        int index = 0;
        while (true) {
            if (usedIndexes.contains(index)) {
                index++;
            } else if (needsTwoIndexes && usedIndexes.contains(index+1)) {
                index += 2;
            } else break;
        }

        return new LocalVariableNode(name, desc, null, start, end, index);
    }
}
