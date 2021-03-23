package io.github.tivj.f5transitions.asm.modifications;

import io.github.tivj.f5transitions.asm.ITransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

public class GuiOverlayDebugTransformerForDebug implements ITransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.gui.GuiOverlayDebug"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode methodNode : classNode.methods) {
            String methodName = mapMethodName(classNode, methodNode);
            if (methodName.equals("call")) {
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == Opcodes.ARETURN && node.getPrevious().getOpcode() == Opcodes.ALOAD) {
                        VarInsnNode var = (VarInsnNode) node.getPrevious();
                        if (var.var == 5) {
                            methodNode.instructions.insertBefore(var, debugrender());
                            break;
                        }
                    }
                }
            }
        }
    }

    private InsnList debugrender() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 5));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "io/github/tivj/f5transitions/DebuggingGateway","debugString","()Ljava/util/Collection;", false));
        list.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "java/util/List","addAll","(Ljava/util/Collection;)Z", true));
        return list;
    }
}