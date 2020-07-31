package net.simon987.mar.server.assembly.instruction;

import net.simon987.mar.server.TestExecutionResult;
import net.simon987.mar.server.assembly.TestHelper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IntInstructionTest {

    @Test
    public void intSimple1() {
        String code = "" +
                "isr:                   \n" +
                "   MOV X, 0x1234       \n" +
                "   IRET                \n" +
                ".text                  \n" +
                "MOV [32], isr          \n" +
                "INT 32                 \n" +
                "MOV Y, 0x4567          \n" +
                "brk                    \n";

        TestExecutionResult res = TestHelper.executeCode(code);

        assertEquals(0x1234, res.regValue("X"));
        assertEquals(0x4567, res.regValue("Y"));
    }

    @Test
    public void intExecutionLimit() {
        String code = "" +
                "isr:                   \n" +
                "   MOV X, 0x1234       \n" +
                "   BRK                 \n" +
                ".text                  \n" +
                "MOV [32], isr          \n" +
                "loop:                  \n" +
                "ADD A, 1               \n" +
                "JMP loop               \n" +
                "MOV Y, 0x4567          \n" +
                "brk                    \n";

        TestExecutionResult res = TestHelper.executeCode(code, 10);

        assertEquals(0x1234, res.regValue("X"));
        assertEquals(0, res.regValue("Y"));
    }
}
