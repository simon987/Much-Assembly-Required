package net.simon987.server.assembly;

import net.simon987.server.assembly.instruction.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Default instruction set for the CPU
 */
public class DefaultInstructionSet implements InstructionSet {

    /**
     * Map of instructions, stored in opcode : Instruction format
     */
    private Map<Integer, Instruction> instructionMap = new HashMap<>(32);

    /**
     * Map of aliasses, stored in mnemonic : Instruction format
     */
    private Map<String, Instruction> aliasesMap = new HashMap<>(16);

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

        // 'abstract' instruction
        add(new SetccInstruction());

        // aliases
        add(new SalInstruction());

        // Setcc family
        add(new SetaeInstruction());
        add(new SetaInstruction());
        add(new SetbeInstruction());
        add(new SetbInstruction());
        add(new SetcInstruction());
        add(new SeteInstruction());
        add(new SetgeInstruction());
        add(new SetgInstruction());
        add(new SetleInstruction());
        add(new SetlInstruction());
        add(new SetnaeInstruction());
        add(new SetnaInstruction());
        add(new SetnbeInstruction());
        add(new SetnbInstruction());
        add(new SetncInstruction());
        add(new SetneInstruction());
        add(new SetngeInstruction());
        add(new SetngInstruction());
        add(new SetnleInstruction());
        add(new SetnlInstruction());
        add(new SetnoInstruction());
        add(new SetnsInstruction());
        add(new SetnzInstruction());
        add(new SetoInstruction());
        add(new SetsInstruction());
        add(new SetzInstruction());
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
     * Get an instruction from its mnemonic
     */
    @Override
    public Instruction get(String mnemonic) {
        for (Instruction ins : instructionMap.values()) {
            if (ins.getMnemonic().equalsIgnoreCase(mnemonic)) {
                return ins;
            }
        }

        Instruction aliasedInstruction = aliasesMap.get(mnemonic.toLowerCase());
        if (aliasedInstruction != null) {
            return aliasedInstruction;
        }

        return null;
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
    public void add(Instruction instruction) {
        Instruction aliasedInstruction = instructionMap.get(instruction.getOpCode());
        if (aliasedInstruction != null) {
            aliasesMap.put(instruction.getMnemonic(), instruction);
        } else {
            instructionMap.put(instruction.getOpCode(), instruction);
        }
    }
}
