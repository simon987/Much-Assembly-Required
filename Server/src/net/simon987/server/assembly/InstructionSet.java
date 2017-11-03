package net.simon987.server.assembly;

/**
 * A set of instructions for a CPU.
 * <p>
 * Defines what a CPU can do
 */
public interface InstructionSet {

    /**
     * Get an instruction by its opcode
     *
     * @param opcode opcode of the instruction
     * @return the instruction, null is not found
     */
    Instruction get(int opcode);

    /**
     * Get an instruction by its mnemonic
     *
     * @param mnemonic mnemonic of the instruction, not case sensitive
     * @return the instruction, if not found, the default instruction is returned
     */
    Instruction get(String mnemonic);

    /**
     * Add an instruction to the set
     *
     * @param instruction instruction to add
     */
    void add(Instruction instruction);

}
