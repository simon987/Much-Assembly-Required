package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.Instruction;
import net.simon987.server.assembly.Status;
import net.simon987.server.assembly.Target;
import net.simon987.server.assembly.Util;
import net.simon987.server.assembly.Operand;;

import net.simon987.server.assembly.exception.IllegalOperandException;

import java.io.ByteArrayOutputStream;

/**
 * Implementation of the SETcc family of instructions
 * http://www.ousob.com/ng/iapx86/ng22d84.php
 * 
 * Current problems to get this working is:
 * - The assembler uses the instruction set to get an instruction by mnemonic, have to check how other alias instructions will be implemented
 * 
 * Stuff to consider
 * - move the implementation of each instruction to its own class that extends this class
 */
public class SetccInstruction extends Instruction {

    public static final int OPCODE = 50;

    public SetccInstruction() {
        // NOTE: The assembler treat this string as a valid mnemonic
        super("setcc", OPCODE);
    }

    public SetccInstruction(String mnemonic) {
        super(mnemonic, OPCODE);
    }

    /**
     * Generic switch on the (family) opcode
     * The SET instructions set the 8-bit destination to 1 if the
     * specified condition is true, otherwise destination is set to 0.
     * 
     *   Instruction        SET to 1 if ... else to 0            Flags
     *   SETA, SETNBE       Above, Not Below or Equal            CF=0 AND ZF=0
     *   SETAE,SETNB,SETNC  Above or Equal, Not Below, No Carry  CF=0
     *   SETBE, SETNA       Below or Equal, Not Above            CF=1 OR ZF=1
     *   SETB, SETC,SETNAE  Below, Carry, Not Above or Equal     CF=1
     *   SETE, SETZ         Equal, Zero                          ZF=1
     *   SETNE, SETNZ       Not Equal, Not Zero                  ZF=0
     *
     *   SETG, SETNLE       Greater, Not Less or Equal           SF=OF AND ZF=0
     *   SETGE, SETNL       Greater or Equal, Not Less           SF=OF
     *   SETLE, SETNG       Less or Equal, Not Greater           SF<>OF OR ZF=1
     *   SETL, SETNGE       Less, Not Greater or Equal           SF<>OF
     *   SETO               Overflow                             OF=1
     *   SETNO              No Overflow                          OF=0
     *   SETS               Sign (negative)                      SF=1
     *   SETNS              No Sign (positive)                   SF=0
     */
    private static Status setcc(Target dst, int dstIndex, int familyOpCode, Status status) {
        return status;
    }

    /**
     * Target can be a memory location or register adressable by dst[dstIndex]
     * FamilyOpcode is the value encoded in the source operand as immideate value
     * it will be used to determince what specfic SETcc operation should be execute
     */
    @Override
    public Status execute(Target dst, int dstIndex, int familyOpCode, Status status) {
        return setcc(dst, dstIndex, familyOpCode, status);
    }

    @Override
    public void encode(ByteArrayOutputStream out, Operand o1, int currentLine) {

        return;
    }
}
