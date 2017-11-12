package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.Instruction;

public class LeaInstruction extends Instruction {

    public static final int OPCODE = 30;

    public LeaInstruction() {
        super("lea", OPCODE);
    }


}
