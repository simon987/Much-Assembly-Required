package net.simon987.server.assembly;

import net.simon987.server.assembly.instruction.*;
import net.simon987.server.logging.LogManager;

import java.util.HashMap;

/**
 * Default instruction set for the CPU
 */
public class DefaultInstructionSet implements InstructionSet {

    /**
     * Map of instructions, stored in opcode : Instruction format
     */
    private HashMap<Integer, Instruction> instructionMap = new HashMap<>(32);

    private Instruction defaultInstruction;

    /**
     * Create an empty instruction set
     */
    DefaultInstructionSet() {
        Instruction nop = new NopInstruction();
        defaultInstruction = nop;

        add(nop);
        add(new BrkInstruction());
        add(new MovInstruction());
        add(new AddInstruction());
        add(new SubInstruction());
        add(new AndInstruction());
        add(new OrInstruction());
        add(new ShlInstruction());
        add(new SalInstruction()); //Alias is added
        add(new ShrInstruction());
        add(new XorInstruction());
        add(new TestInstruction());
        add(new CmpInstruction());
        add(new NegInstruction());
        add(new NotInstruction());
        add(new RorInstruction());
        add(new RolInstruction());
        add(new RclInstruction());
        add(new RcrInstruction());
        add(new SarInstruction());
        add(new IncInstruction());
        add(new DecInstruction());
    }

    /**
     * Get an instruction from its opcode
     *
     * @param opcode opcode of the instruction
     * @return the instruction, default is not found
     */
    @Override
    public Instruction get(int opcode) {

        Instruction instruction = instructionMap.get(opcode);
        if (instruction != null) {
            return instruction;
        } else {
            // System.out.println("Invalid instruction " + opcode);
            //Todo: Notify user?  Set error flag?
            return defaultInstruction;
        }
    }

    /**
     * Add a new instruction to the instructionSet
     *
     * @param opcode      opcode of the instruction
     * @param instruction Instruction to add
     */
    public void addInstruction(int opcode, Instruction instruction) {
        instructionMap.put(opcode, instruction);
    }


    @Override
    public Instruction get(String mnemonic) {
        for (Instruction ins : instructionMap.values()) {
            if (ins.getMnemonic().equalsIgnoreCase(mnemonic)) {
                return ins;
            }
        }

        return null;
    }


    @Override
    public void add(Instruction instruction) {
        if (instructionMap.containsKey(instruction.getOpCode())) {
            LogManager.LOGGER.fine(instruction.getMnemonic() + " instruction is an alias for " +
                    instructionMap.get(instruction.getOpCode()).getMnemonic());
        } else {
            instructionMap.put(instruction.getOpCode(), instruction);

        }
    }
}
