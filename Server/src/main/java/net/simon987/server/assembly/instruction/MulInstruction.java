package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.*;


public class MulInstruction extends Instruction {

    public static final int OPCODE = 23;

    private CPU cpu;

    public MulInstruction(CPU cpu) {
        super("mul", OPCODE);

        this.cpu = cpu;
    }

    @Override
    public Status execute(Target src, int srcIndex, Status status) {

        int result = cpu.getRegisterSet().getRegister("A").getValue() * (char) src.get(srcIndex);

        int hWord = Util.getHigherWord(result);
        if (hWord != 0) {
            status.setOverflowFlag(true);
            status.setCarryFlag(true);
            cpu.getRegisterSet().getRegister("Y").setValue(hWord);//Don't overwrite Y register if it's blank
        } else {
            status.setOverflowFlag(false);
            status.setCarryFlag(false);
        }

        cpu.getRegisterSet().set(1, Util.getLowerWord(result));

        return status;
    }

    @Override
    public Status execute(int src, Status status) {


        int result = cpu.getRegisterSet().getRegister("A").getValue() * (char) src;

        int hWord = Util.getHigherWord(result);
        if (hWord != 0) {
            status.setOverflowFlag(true);
            status.setCarryFlag(true);
            cpu.getRegisterSet().getRegister("Y").setValue(hWord);//Don't overwrite Y register if it's blank
        } else {
            status.setOverflowFlag(false);
            status.setCarryFlag(false);
        }

        cpu.getRegisterSet().getRegister("A").setValue(Util.getLowerWord(result));

        return status;
    }


}
