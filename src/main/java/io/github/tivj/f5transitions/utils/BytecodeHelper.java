package io.github.tivj.f5transitions.utils;

import io.github.tivj.f5transitions.TransitionsMod;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

/**
 * Intended for debugging use in UserTeemu's projects. Should not be used in production.
 * @author UserTeemu
 */
public class BytecodeHelper {
    public static void printEmAll(InsnList instructions) {
        TransitionsMod.LOGGER.info("BytecodeDump:"+"------- Method dump start --------");
        for (ListIterator<AbstractInsnNode> it = instructions.iterator(); it.hasNext();) {
            AbstractInsnNode instruction = it.next();
            if (instruction instanceof MethodInsnNode)          TransitionsMod.LOGGER.info("BytecodeDump: "+opcode(instruction.getOpcode())+" "+methodToMCP((MethodInsnNode) instruction));
            else if (instruction instanceof JumpInsnNode)       TransitionsMod.LOGGER.info("BytecodeDump: "+opcode(instruction.getOpcode())+" "+((JumpInsnNode) instruction).label.getLabel().toString());
            else if (instruction instanceof FieldInsnNode)      TransitionsMod.LOGGER.info("BytecodeDump: "+opcode(instruction.getOpcode())+" "+fieldToMCP((FieldInsnNode) instruction));
            else if (instruction instanceof VarInsnNode)        TransitionsMod.LOGGER.info("BytecodeDump: "+opcode(instruction.getOpcode())+" "+((VarInsnNode) instruction).var);
            else if (instruction instanceof TypeInsnNode)       TransitionsMod.LOGGER.info("BytecodeDump: "+opcode(instruction.getOpcode())+" "+FMLDeobfuscatingRemapper.INSTANCE.mapType(((TypeInsnNode) instruction).desc));
            else if (instruction instanceof LdcInsnNode)        TransitionsMod.LOGGER.info("BytecodeDump: LDC " + ((LdcInsnNode) instruction).cst);
            else if (instruction instanceof LabelNode)          TransitionsMod.LOGGER.info("BytecodeDump: LABELNODE "+((LabelNode) instruction).getLabel().toString());
            else if (instruction instanceof LineNumberNode)     {
                System.out.print("\n");
                TransitionsMod.LOGGER.info("BytecodeDump: Line: "+((LineNumberNode) instruction).line);
            } else if (instruction instanceof FrameNode) {
                TransitionsMod.LOGGER.info("BytecodeDump: Frame: "+frameToString((FrameNode) instruction));
            }

            else TransitionsMod.LOGGER.info("BytecodeDump: "+opcode(instruction.getOpcode()));
        }
        TransitionsMod.LOGGER.info("BytecodeDump:------- Method dump end --------\n");
    }

    private static String frameToString(FrameNode instruction) {
        StringBuilder out = new StringBuilder("Type: ");
        switch (instruction.type) {
            case Opcodes.F_NEW:
                out.append("NEW");
                break;
            case Opcodes.F_FULL:
                out.append("FULL");
                break;
            case Opcodes.F_CHOP:
                out.append("CHOP");
                break;
            case Opcodes.F_SAME:
                out.append("SAME");
                break;
            case Opcodes.F_APPEND:
                out.append("APPEND");
                break;
            case Opcodes.F_SAME1:
                out.append("SAME1");
                break;
            default: out.append("INVALID");
        }
        out.append(" Locals: [");
        for (Object local : instruction.local) {
            out.append(local.toString()+", ");
        }

        out.append("] Stacks: [");
        for (Object stack : instruction.stack) {
            out.append(stack.toString()+", ");
        }
        out.append("]");
        return out.toString();
    }

    private static String fieldToMCP(FieldInsnNode instruction) {
        String owner = instruction.owner;
        String desc = instruction.desc;

        FMLDeobfuscatingRemapper remapper = FMLDeobfuscatingRemapper.INSTANCE;
        String toReturn = remapper.mapFieldName(owner, instruction.name, desc);

        if (desc.startsWith("L")) desc = remapper.mapDesc(desc);
        owner = remapper.mapType(owner);

        return owner+" "+toReturn+" "+desc;
    }

    private static String methodToMCP(MethodInsnNode instruction) {
        String owner = instruction.owner;
        String desc = instruction.desc;

        FMLDeobfuscatingRemapper remapper = FMLDeobfuscatingRemapper.INSTANCE;
        String toReturn = remapper.mapFieldName(owner, instruction.name, desc);

        if (desc.startsWith("L")) desc = remapper.mapDesc(desc);
        owner = remapper.mapType(owner);

        return owner+" "+toReturn+" "+desc;
    }

    private static String opcode(int opcode) {
        switch (opcode) {
            case 50: return "AALOAD";
            case 83: return "AASTORE";
            case 1: return "ACONST_NULL";
            case 25: return "ALOAD";
            case 189: return "ANEWARRAY";
            case 176: return "ARETURN";
            case 190: return "ARRAYLENGTH";
            case 58: return "ASTORE";
            case 191: return "ATHROW";
            case 51: return "BALOAD";
            case 84: return "BASTORE";
            case 16: return "BIPUSH";
            case 52: return "CALOAD";
            case 85: return "CASTORE";
            case 192: return "CHECKCAST";
            case 144: return "D2F";
            case 142: return "D2I";
            case 143: return "D2L";
            case 99: return "DADD";
            case 49: return "DALOAD";
            case 82: return "DASTORE";
            case 152: return "DCMPG";
            case 151: return "DCMPL";
            case 14: return "DCONST_0";
            case 15: return "DCONST_1";
            case 111: return "DDIV";
            case 24: return "DLOAD";
            case 107: return "DMUL";
            case 119: return "DNEG";
            case 115: return "DREM";
            case 175: return "DRETURN";
            case 57: return "DSTORE";
            case 103: return "DSUB";
            case 89: return "DUP";
            case 90: return "DUP_X1";
            case 91: return "DUP_X2";
            case 92: return "DUP2";
            case 93: return "DUP2_X1";
            case 94: return "DUP2_X2";
            case 141: return "F2D";
            case 139: return "F2I";
            case 140: return "F2L";
            case 98: return "FADD";
            case 48: return "FALOAD";
            case 81: return "FASTORE";
            case 150: return "FCMPG";
            case 149: return "FCMPL";
            case 11: return "FCONST_0";
            case 12: return "FCONST_1";
            case 13: return "FCONST_2";
            case 110: return "FDIV";
            case 23: return "FLOAD";
            case 106: return "FMUL";
            case 118: return "FNEG";
            case 114: return "FREM";
            case 174: return "FRETURN";
            case 56: return "FSTORE";
            case 102: return "FSUB";
            case 180: return "GETFIELD";
            case 178: return "GETSTATIC";
            case 167: return "GOTO";
            case 145: return "I2B";
            case 146: return "I2C";
            case 135: return "I2D";
            case 134: return "I2F";
            case 133: return "I2L";
            case 147: return "I2S";
            case 96: return "IADD";
            case 46: return "IALOAD";
            case 126: return "IAND";
            case 79: return "IASTORE";
            case 3: return "ICONST_0";
            case 4: return "ICONST_1";
            case 5: return "ICONST_2";
            case 6: return "ICONST_3";
            case 7: return "ICONST_4";
            case 8: return "ICONST_5";
            case 2: return "ICONST_M1";
            case 108: return "IDIV";
            case 165: return "IF_ACMPEQ";
            case 166: return "IF_ACMPNE";
            case 159: return "IF_ICMPEQ";
            case 162: return "IF_ICMPGE";
            case 163: return "IF_ICMPGT";
            case 164: return "IF_ICMPLE";
            case 161: return "IF_ICMPLT";
            case 160: return "IF_ICMPNE";
            case 153: return "IFEQ";
            case 156: return "IFGE";
            case 157: return "IFGT";
            case 158: return "IFLE";
            case 155: return "IFLT";
            case 154: return "IFNE";
            case 199: return "IFNONNULL";
            case 198: return "IFNULL";
            case 132: return "IINC";
            case 21: return "ILOAD";
            case 104: return "IMUL";
            case 116: return "INEG";
            case 193: return "INSTANCEOF";
            case 186: return "INVOKEDYNAMIC";
            case 185: return "INVOKEINTERFACE";
            case 183: return "INVOKESPECIAL";
            case 184: return "INVOKESTATIC";
            case 182: return "INVOKEVIRTUAL";
            case 128: return "IOR";
            case 112: return "IREM";
            case 172: return "IRETURN";
            case 120: return "ISHL";
            case 122: return "ISHR";
            case 54: return "ISTORE";
            case 100: return "ISUB";
            case 124: return "IUSHR";
            case 130: return "IXOR";
            case 168: return "JSR";
            case 138: return "L2D";
            case 137: return "L2F";
            case 136: return "L2I";
            case 97: return "LADD";
            case 47: return "LALOAD";
            case 127: return "LAND";
            case 80: return "LASTORE";
            case 148: return "LCMP";
            case 9: return "LCONST_0";
            case 10: return "LCONST_1";
            case 18: return "LDC";
            case 109: return "LDIV";
            case 22: return "LLOAD";
            case 105: return "LMUL";
            case 117: return "LNEG";
            case 171: return "LOOKUPSWITCH";
            case 129: return "LOR";
            case 113: return "LREM";
            case 173: return "LRETURN";
            case 121: return "LSHL";
            case 123: return "LSHR";
            case 55: return "LSTORE";
            case 101: return "LSUB";
            case 125: return "LUSHR";
            case 131: return "LXOR";
            case 194: return "MONITORENTER";
            case 195: return "MONITOREXIT";
            case 197: return "MULTIANEWARRAY";
            case 187: return "NEW";
            case 188: return "NEWARRAY";
            case 0: return "NOP";
            case 87: return "POP";
            case 88: return "POP2";
            case 181: return "PUTFIELD";
            case 169: return "RET";
            case 177: return "RETURN";
            case 53: return "SALOAD";
            case 86: return "SASTORE";
            case 17: return "SIPUSH";
            case 95: return "SWAP";
            case 170: return "TABLESWITCH";
            case 179: return "PUTSTATIC";
            default: return "Opcode " + opcode;
        }
    }
}
