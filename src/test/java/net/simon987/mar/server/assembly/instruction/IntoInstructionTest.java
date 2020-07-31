package net.simon987.mar.server.assembly.instruction;

import net.simon987.mar.server.TestExecutionResult;
import net.simon987.mar.server.assembly.TestHelper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IntoInstructionTest {

    @Test
    public void intoSimple1() {
        String code = "" +
                "isr:                   \n" +
                "   MOV X, 0x1234       \n" +
                "   IRET                \n" +
                ".text                  \n" +
                "MOV [4], isr           \n" +
                "INTO                   \n" +
                "MOV Y, 0x4567          \n" +
                "brk                    \n";

        TestExecutionResult res = TestHelper.executeCode(code);

        assertEquals(0, res.regValue("X"));
        assertEquals(0x4567, res.regValue("Y"));
    }

    @Test
    public void intoSimple2() {
        String code = "" +
                "isr:                   \n" +
                "   MOV X, 0x1234       \n" +
                "   IRET                \n" +
                ".text                  \n" +
                "MOV [4], isr           \n" +
                "MOV A, 0x7FFF          \n" +
                "ADD A, 1               \n" +
                "INTO                   \n" +
                "MOV Y, 0x4567          \n" +
                "brk                    \n";

        TestExecutionResult res = TestHelper.executeCode(code);

        assertEquals(0x1234, res.regValue("X"));
        assertEquals(0x4567, res.regValue("Y"));
    }
}
