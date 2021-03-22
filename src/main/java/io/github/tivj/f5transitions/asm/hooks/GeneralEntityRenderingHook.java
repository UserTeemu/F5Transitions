package io.github.tivj.f5transitions.asm.hooks;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class GeneralEntityRenderingHook {
    public static boolean isRightRenderContextForTransitions = false;
    public static boolean canApplyTransitionsToEntity(Entity entity) {
        return entity != null && entity.equals(Minecraft.getMinecraft().getRenderViewEntity()) && isRightRenderContextForTransitions;
    }

    public static InsnList canApplyTransitionsToEntity(int indexOfEntityVariable) {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, indexOfEntityVariable));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "io/github/tivj/f5transitions/asm/hooks/GeneralEntityRenderingHook", "canApplyTransitionsToEntity", "(Lnet/minecraft/entity/Entity;)Z", false));
        return list;
    }

    public static InsnList setContext(boolean state) {
        InsnList list = new InsnList();
        list.add(new InsnNode(state ? Opcodes.ICONST_1 : Opcodes.ICONST_0));
        list.add(new FieldInsnNode(Opcodes.PUTSTATIC, "io/github/tivj/f5transitions/asm/hooks/GeneralEntityRenderingHook", "isRightRenderContextForTransitions", "Z"));
        return list;
    }
}
