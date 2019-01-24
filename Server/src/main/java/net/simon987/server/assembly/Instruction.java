package net.simon987.server.assembly;

import net.simon987.server.assembly.exception.AssemblyException;
import net.simon987.server.assembly.exception.IllegalOperandException;

import java.io.ByteArrayOutputStream;

/**
 * Represents a instruction type (e.g. MOV) that performs an action
 * base on 0-2 Targets
 */
public abstract class Instruction {


    /**
     * Symbolic name of the instruction
     */
    private String mnemonic;

    /**
     * Opcode of the instruction (6-bit signed integer)
     */
    private int opCode;

    /**
     * Create a new Instruction
     *
     * @param mnemonic Mnemonic of the instruction
     * @param opCode   opCode of the instruction (6-bit signed integer)
     */
    public Instruction(String mnemonic, int opCode) {
        this.mnemonic = mnemonic;
        this.opCode = opCode;
    }

    /**
     * Execute an instruction with 2 operands
     *
     * @param dst      Destination operand
     * @param dstIndex Index of the destination operand
     * @param src      Source operand
     * @param srcIndex Index of the source operand
     * @param status   Status of the CPU before the execution
     * @return Status after the execution
     */
    public Status execute(Target dst, int dstIndex, Target src, int srcIndex, Status status) {

        return status;
    }

    /**
     * Execute an instruction with 2 operands, the second one being an immediate operand
     *
     * @param dst      Destination operand
     * @param dstIndex Index of the destination operand
     * @param src      Source operand
     * @param status   Status of the CPU before execution
     * @return Status after the execution
     */
    public Status execute(Target dst, int dstIndex, int src, Status status) {


        return status;
    }

    /**
     * Execute an instruction with 1 operand
     *
     * @param src      Source operand
     * @param srcIndex Index of the source operand
     * @param status   Status of the CPU before execution
     * @return Status after execution
     */
    public Status execute(Target src, int srcIndex, Status status) {

        return status;
    }

    /**
     * Execute an instruction with 1 operand that is an immediate value
     *
     * @param src    Source operand
     * @param status Status of the CPU before execution
     * @return Status after execution
     */
    public Status execute(int src, Status status) {

        return status;
    }

    /**
     * Execute an instruction that doesn't take any operand
     */
    public Status execute(Status status) {
        return status;
    }

    /**
     * Check if the operand combinaison is valid.
     *
     * @param o1 Destination operand
     * @param o2 Source operand
     * @return true if valid
     */
    public boolean operandsValid(Operand o1, Operand o2) {
        return o1.getType() != OperandType.IMMEDIATE16;
    }

    /**
     * Check if the operand is valid for this instruction
     *
     * @param o1 source operand
     * @return true if the specified operand can be used with this instruction
     */
    public boolean operandValid(Operand o1) {
        return true;
    }

    /**
     * Whether or not the instruction is valid without any
     * operands
     */
    public boolean noOperandsValid() {
        return false;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    /**
     * Encodes the instruction. Writes the result in the outputStream
     *
     * @param out encoded bytes will be written here
     */
    public void encode(ByteArrayOutputStream out, int currentLine) throws AssemblyException {

        if (!noOperandsValid()) {
            throw new IllegalOperandException("This instruction must have operand(s)!", currentLine);
        }

        MachineCode code = new MachineCode();
        code.writeOpcode(opCode);

        for (byte b : code.bytes()) {
            out.write(b);
        }
    }

    public void encode(ByteArrayOutputStream out, Operand o1, Operand o2, int currentLine)
            throws AssemblyException {
        MachineCode code = new MachineCode();
        code.writeOpcode(opCode);

        if (!operandsValid(o1, o2)) {
            throw new IllegalOperandException("Illegal operand combination : " + o1.getType()
                    + " and " + o2.getType(), currentLine);
        }

        //Source operand
        if (o2.getType() == OperandType.REGISTER16 || o2.getType() == OperandType.MEMORY_REG16) {
            //operand can be stored in its 5-bit space
            code.writeSourceOperand(o2.getValue());
        } else {
            //operand needs to be stored in another word
            code.writeSourceOperand(o2.getValue());
            code.appendWord((char) o2.getData());
        }

        //Destination operand
        if (o1.getType() == OperandType.REGISTER16 || o1.getType() == OperandType.MEMORY_REG16) {
            //operand can be stored in its 5-bit space
            code.writeDestinationOperand(o1.getValue());
        } else {
            //operand needs to be stored in another word
            code.writeDestinationOperand(o1.getValue());
            code.appendWord((char) o1.getData());
        }

        for (byte b : code.bytes()) {
            out.write(b);
        }
    }

    public void encode(ByteArrayOutputStream out, Operand o1, int currentLine)
            throws AssemblyException {
        MachineCode code = new MachineCode();
        code.writeOpcode(opCode);

        if (!operandValid(o1)) {
            throw new IllegalOperandException("Illegal operand combination: " + o1.getType() + " (none)", currentLine);
        }

        //Source operand
        if (o1.getType() == OperandType.REGISTER16 || o1.getType() == OperandType.MEMORY_REG16) {
            //operand can be stored in its 5-bit space
            code.writeSourceOperand(o1.getValue());
        } else {
            //operand needs to be stored in another word
            code.writeSourceOperand(o1.getValue());
            code.appendWord((char) o1.getData());
        }

        //Destination bits are left blank

        for (byte b : code.bytes()) {
            out.write(b);
        }
    }

    public int getOpCode() {
        return opCode;
    }
}
