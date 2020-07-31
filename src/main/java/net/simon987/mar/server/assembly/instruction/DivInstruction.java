package net.simon987.mar.server.assembly.instruction;

import net.simon987.mar.server.assembly.CPU;
import net.simon987.mar.server.assembly.Instruction;
import net.simon987.mar.server.assembly.Status;
import net.simon987.mar.server.assembly.Target;

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

    private final CPU cpu;

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
            cpu.interrupt(IntInstruction.INT_DIVISION_BY_ZERO);
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
            cpu.interrupt(IntInstruction.INT_DIVISION_BY_ZERO);
        } else {
            cpu.getRegisterSet().getRegister("A").setValue((char) (source / (char) src));
            cpu.getRegisterSet().getRegister("Y").setValue((char) (source % (char) src));
        }

        return status;
    }
}
