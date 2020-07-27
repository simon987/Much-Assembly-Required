package net.simon987.mar.server.assembly.instruction;

import net.simon987.mar.server.assembly.Status;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class BrkInstructionTest {
    @Test
    public void execute() throws Exception {

        //Status
        Status status = new Status();
        status.clear();

        BrkInstruction brkInstruction = new BrkInstruction();

        brkInstruction.execute(status);

        assertTrue(status.isBreakFlag());
    }

}
