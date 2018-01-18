package net.simon987.server.assembly.instruction;

import net.simon987.server.assembly.Status;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BrkInstructionTest {
    @Test
    public void execute() throws Exception {

        //Status
        Status status = new Status();
        status.clear();

        BrkInstruction brkInstruction = new BrkInstruction();

        brkInstruction.execute(status);

        assertEquals(true, status.isBreakFlag());

    }

}