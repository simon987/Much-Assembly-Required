package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.CPU;
import net.simon987.server.assembly.Instruction;
import net.simon987.server.assembly.Status;
import net.simon987.server.assembly.Target;

/**
 * Divide instruction.
 * <p>
 * DIV C
 * A = Y:A / C
 * Y = Y:A % C
 * </p>
 */
public class DivInstruction extends Instruction {

    public static final int OPCODE = 24;

    private CPU cpu;

    public DivInstruction(CPU cpu) {
        super("div", OPCODE);
        this.cpu = cpu;
    }

    @Override
    public Status execute(Target src, int srcIndex, Status status) {

        //Source = Y:A
        int source = (((cpu.getRegisterSet().getRegister("Y").getValue() & 0xFFFF) << 16)) |
                (cpu.getRegisterSet().getRegister("A").getValue() & 0xFFFF);

        if (src.get(srcIndex) == 0) {
            //Division by 0
            status.setBreakFlag(true);
            status.setErrorFlag(true);
        } else {
            cpu.getRegisterSet().getRegister("A").setValue((char) (source / (char) src.get(srcIndex)));
            cpu.getRegisterSet().getRegister("Y").setValue((char) (source % (char) src.get(srcIndex)));
        }

        return status;
    }

    @Override
    public Status execute(int src, Status status) {


        //Source = Y:A
        int source = (((cpu.getRegisterSet().getRegister("Y").getValue() & 0xFFFF) << 16)) |
                (cpu.getRegisterSet().getRegister("A").getValue() & 0xFFFF);

        if (src == 0) {
            //Division by 0
            status.setBreakFlag(true);
            status.setErrorFlag(true);
        } else {
            cpu.getRegisterSet().getRegister("A").setValue((char) (source / (char) src));
            cpu.getRegisterSet().getRegister("Y").setValue((char) (source % (char) src));
        }


        return status;
    }
}
