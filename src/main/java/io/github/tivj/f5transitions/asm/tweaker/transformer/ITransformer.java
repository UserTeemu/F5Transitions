package io.github.tivj.f5transitions.asm.tweaker.transformer;

import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.List;

public interface ITransformer {
    String[] getClassName();
    void transform(ClassNode classNode, String name);

    default List<String> debuggableClass() {
        return new ArrayList<>();
    }

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
}
