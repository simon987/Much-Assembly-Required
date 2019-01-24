package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.Instruction;
import net.simon987.server.assembly.Status;
import net.simon987.server.assembly.Target;
import net.simon987.server.assembly.Operand;
import net.simon987.server.assembly.OperandType;
import net.simon987.server.assembly.MachineCode;

import net.simon987.server.assembly.exception.AssemblyException;
import net.simon987.server.assembly.exception.IllegalOperandException;
import net.simon987.server.assembly.exception.InvalidMnemonicException;

import java.io.ByteArrayOutputStream;

import java.util.Map;
import java.util.HashMap;

/**
 * Implementation of the SETcc family of instructions
 * http://www.ousob.com/ng/iapx86/ng22d84.php
 */
public class SetccInstruction extends Instruction {

    public static final int OPCODE = 50;

    public static final int SETA = 0x01;
    public static final int SETAE = 0x02;
    public static final int SETBE = 0x03;
    public static final int SETB = 0x04;
    public static final int SETE = 0x05;
    public static final int SETNE = 0x06;
    public static final int SETG = 0x07;
    public static final int SETGE = 0x08;
    public static final int SETLE = 0x09;
    public static final int SETL = 0x0A;
    public static final int SETO = 0x0B;
    public static final int SETNO = 0x0C;
    public static final int SETS = 0x0D;
    public static final int SETNS = 0x0E;

    /**
     * Map of mnemonics, stored in mnemonic : family op code
     * This map includes aliases
     */
    private static final Map<String, Character> mnemonicFamilyOpCodeMap = new HashMap<>(26);

    static {
        mnemonicFamilyOpCodeMap.put("seta", (char) SETA);
        mnemonicFamilyOpCodeMap.put("setnbe", (char) SETA);
        mnemonicFamilyOpCodeMap.put("setae", (char) SETAE);
        mnemonicFamilyOpCodeMap.put("setnb", (char) SETAE);
        mnemonicFamilyOpCodeMap.put("setnc", (char) SETAE);
        mnemonicFamilyOpCodeMap.put("setbe", (char) SETBE);
        mnemonicFamilyOpCodeMap.put("setna", (char) SETBE);
        mnemonicFamilyOpCodeMap.put("setb", (char) SETB);
        mnemonicFamilyOpCodeMap.put("setc", (char) SETB);
        mnemonicFamilyOpCodeMap.put("setnae", (char) SETB);
        mnemonicFamilyOpCodeMap.put("sete", (char) SETE);
        mnemonicFamilyOpCodeMap.put("setz", (char) SETE);
        mnemonicFamilyOpCodeMap.put("setne", (char) SETNE);
        mnemonicFamilyOpCodeMap.put("setnz", (char) SETNE);
        mnemonicFamilyOpCodeMap.put("setg", (char) SETG);
        mnemonicFamilyOpCodeMap.put("setnle", (char) SETG);
        mnemonicFamilyOpCodeMap.put("setge", (char) SETGE);
        mnemonicFamilyOpCodeMap.put("setnl", (char) SETGE);
        mnemonicFamilyOpCodeMap.put("setle", (char) SETLE);
        mnemonicFamilyOpCodeMap.put("setng", (char) SETLE);
        mnemonicFamilyOpCodeMap.put("setl", (char) SETL);
        mnemonicFamilyOpCodeMap.put("setnge", (char) SETL);
        mnemonicFamilyOpCodeMap.put("seto", (char) SETO);
        mnemonicFamilyOpCodeMap.put("setno", (char) SETNO);
        mnemonicFamilyOpCodeMap.put("sets", (char) SETS);
        mnemonicFamilyOpCodeMap.put("setns", (char) SETNS);
    }

    public SetccInstruction() {
        super("setcc", OPCODE);
    }

    public SetccInstruction(String alias) {
        super(alias, OPCODE);
    }

    /**
     * The SET instructions set the 16-bit destination to 1 if the
     * specified condition is true, otherwise destination is set to 0.
     * <p>
     * FamilyOpcode   Instruction        SET to 1 if ... else to 0            Flags
     * 0x01           SETA, SETNBE       Above, Not Below or Equal            CF=0 AND ZF=0
     * 0x02           SETAE,SETNB,SETNC  Above or Equal, Not Below, No Carry  CF=0
     * 0x03           SETBE, SETNA       Below or Equal, Not Above            CF=1 OR ZF=1
     * 0x04           SETB, SETC,SETNAE  Below, Carry, Not Above or Equal     CF=1
     * 0x05           SETE, SETZ         Equal, Zero                          ZF=1
     * 0x06           SETNE, SETNZ       Not Equal, Not Zero                  ZF=0
     * 0x07           SETG, SETNLE       Greater, Not Less or Equal           SF=OF AND ZF=0
     * 0x08           SETGE, SETNL       Greater or Equal, Not Less           SF=OF
     * 0x09           SETLE, SETNG       Less or Equal, Not Greater           SF<>OF OR ZF=1
     * 0x0A           SETL, SETNGE       Less, Not Greater or Equal           SF<>OF
     * 0x0B           SETO               Overflow                             OF=1
     * 0x0C           SETNO              No Overflow                          OF=0
     * 0x0D           SETS               Sign (negative)                      SF=1
     * 0x0E           SETNS              No Sign (positive)                   SF=0
     */
    private static Status setcc(Target dst, int dstIndex, int familyOpCode, Status status) {
        switch (familyOpCode) {
            case SETA:
                return seta(dst, dstIndex, status);
            case SETAE:
                return setae(dst, dstIndex, status);
            case SETBE:
                return setbe(dst, dstIndex, status);
            case SETB:
                return setb(dst, dstIndex, status);
            case SETE:
                return sete(dst, dstIndex, status);
            case SETNE:
                return setne(dst, dstIndex, status);
            case SETG:
                return setg(dst, dstIndex, status);
            case SETGE:
                return setge(dst, dstIndex, status);
            case SETLE:
                return setle(dst, dstIndex, status);
            case SETL:
                return setl(dst, dstIndex, status);
            case SETO:
                return seto(dst, dstIndex, status);
            case SETNO:
                return setno(dst, dstIndex, status);
            case SETS:
                return sets(dst, dstIndex, status);
            case SETNS:
                return setns(dst, dstIndex, status);
            default:
                return status;
        }
    }

    /**
     * Target can be a memory location or register adressable by dst[dstIndex]
     * FamilyOpcode is the value encoded in the source operand as immidiate value
     * it will be used to determince what specfic SETcc operation should be execute
     */
    @Override
    public Status execute(Target dst, int dstIndex, int familyOpCode, Status status) {
        return setcc(dst, dstIndex, familyOpCode, status);
    }

    /**
     * This instruction can never be encoded with 2 operands since one word is reserved for its family op code encoding
     */
    @Override
    public boolean operandsValid(Operand o1, Operand o2) {
        return false;
    }

    @Override
    public boolean operandValid(Operand o1) {
        return o1.getType() != OperandType.IMMEDIATE16;
    }

    /**
     * Encodes the instruction. Writes the result in the outputStream.
     * Needs one operand of OperandType.REGISTER or OperandType.MEMORY_REG16
     *
     * @param out encoded bytes will be written here
     */
    @Override
    public void encode(ByteArrayOutputStream out, Operand o1, int currentLine) throws AssemblyException {
        String mnemonic = getMnemonic().toLowerCase();
        Character familyOpCode = mnemonicFamilyOpCodeMap.get(mnemonic);

        // This will catch the off case that someone uses the mnemonic 'setcc'
        // as far as the assembler knows this is a valid instruction, but we know it isn't
        if (familyOpCode == null) {
            throw new InvalidMnemonicException(getMnemonic(), currentLine);
        }

        if (!operandValid(o1)) {
            throw new IllegalOperandException("Illegal operand combination: " + o1.getType() + " (none)", currentLine);
        }

        MachineCode code = new MachineCode();
        code.writeOpcode(getOpCode());

        code.writeSourceOperand(Operand.IMMEDIATE_VALUE);
        code.appendWord(familyOpCode);

        if (o1.getType() == OperandType.REGISTER16 || o1.getType() == OperandType.MEMORY_REG16) {
            code.writeDestinationOperand(o1.getValue());
        } else {
            code.writeDestinationOperand(o1.getValue());
            code.appendWord((char) o1.getData());
        }

        for (byte b : code.bytes()) {
            out.write(b);
        }
    }

    /**
     * SETA, SETNBE   Above, Not Below or Equal   CF=0 AND ZF=0
     */
    private static Status seta(Target dst, int dstIndex, Status status) {
        boolean condition = !status.isCarryFlag() && !status.isZeroFlag();
        int value = condition ? 1 : 0;
        dst.set(dstIndex, value);
        return status;
    }

    /**
     * SETAE,SETNB,SETNC  Above or Equal, Not Below, No Carry  CF=0
     */
    private static Status setae(Target dst, int dstIndex, Status status) {
        boolean condition = !status.isCarryFlag();
        int value = condition ? 1 : 0;
        dst.set(dstIndex, value);
        return status;
    }

    /**
     * SETBE, SETNA       Below or Equal, Not Above            CF=1 OR ZF=1
     */
    private static Status setbe(Target dst, int dstIndex, Status status) {
        boolean condition = status.isCarryFlag() || status.isZeroFlag();
        int value = condition ? 1 : 0;
        dst.set(dstIndex, value);
        return status;
    }

    /**
     * SETB, SETC,SETNAE  Below, Carry, Not Above or Equal     CF=1
     */
    private static Status setb(Target dst, int dstIndex, Status status) {
        boolean condition = status.isCarryFlag();
        int value = condition ? 1 : 0;
        dst.set(dstIndex, value);
        return status;
    }

    /**
     * SETE, SETZ         Equal, Zero                          ZF=1
     */
    private static Status sete(Target dst, int dstIndex, Status status) {
        boolean condition = status.isZeroFlag();
        int value = condition ? 1 : 0;
        dst.set(dstIndex, value);
        return status;
    }

    /**
     * SETNE, SETNZ       Not Equal, Not Zero                  ZF=0
     */
    private static Status setne(Target dst, int dstIndex, Status status) {
        boolean condition = !status.isZeroFlag();
        int value = condition ? 1 : 0;
        dst.set(dstIndex, value);
        return status;
    }

    /**
     * SETG, SETNLE       Greater, Not Less or Equal           SF=OF AND ZF=0
     */
    private static Status setg(Target dst, int dstIndex, Status status) {
        boolean condition = (status.isSignFlag() == status.isOverflowFlag()) && !status.isZeroFlag();
        int value = condition ? 1 : 0;
        dst.set(dstIndex, value);
        return status;
    }

    /**
     * SETGE, SETNL       Greater or Equal, Not Less           SF=OF
     */
    private static Status setge(Target dst, int dstIndex, Status status) {
        boolean condition = status.isSignFlag() == status.isOverflowFlag();
        int value = condition ? 1 : 0;
        dst.set(dstIndex, value);
        return status;
    }

    /**
     * SETLE, SETNG       Less or Equal, Not Greater           SF<>OF OR ZF=1
     */
    private static Status setle(Target dst, int dstIndex, Status status) {
        boolean condition = (status.isSignFlag() != status.isOverflowFlag()) || status.isZeroFlag();
        int value = condition ? 1 : 0;
        dst.set(dstIndex, value);
        return status;
    }

    /**
     * SETL, SETNGE       Less, Not Greater or Equal           SF<>OF
     */
    private static Status setl(Target dst, int dstIndex, Status status) {
        boolean condition = status.isSignFlag() != status.isOverflowFlag();
        int value = condition ? 1 : 0;
        dst.set(dstIndex, value);
        return status;
    }

    /**
     * SETO               Overflow                             OF=1
     */
    private static Status seto(Target dst, int dstIndex, Status status) {
        boolean condition = status.isOverflowFlag();
        int value = condition ? 1 : 0;
        dst.set(dstIndex, value);
        return status;
    }

    /**
     * SETNO              No Overflow                          OF=0
     */
    private static Status setno(Target dst, int dstIndex, Status status) {
        boolean condition = !status.isOverflowFlag();
        int value = condition ? 1 : 0;
        dst.set(dstIndex, value);
        return status;
    }

    /**
     * SETS               Sign (negative)                      SF=1
     */
    private static Status sets(Target dst, int dstIndex, Status status) {
        boolean condition = status.isSignFlag();
        int value = condition ? 1 : 0;
        dst.set(dstIndex, value);
        return status;
    }

    /**
     * SETNS              No Sign (positive)                   SF=0
     */
    private static Status setns(Target dst, int dstIndex, Status status) {
        boolean condition = !status.isSignFlag();
        int value = condition ? 1 : 0;
        dst.set(dstIndex, value);
        return status;
    }
}
