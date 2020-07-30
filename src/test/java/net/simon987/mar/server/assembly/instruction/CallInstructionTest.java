package net.simon987.mar.server.assembly.instruction;

import net.simon987.mar.server.FakeConfiguration;
import net.simon987.mar.server.IServerConfiguration;
import net.simon987.mar.server.TestExecutionResult;
import net.simon987.mar.server.assembly.*;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CallInstructionTest {

    @Test
    public void callSimple1() {
        String code = "" +
                "my_routine:            \n" +
                "   MOV X, 0x1234       \n" +
                "   RET                 \n" +
                ".text                  \n" +
                "CALL my_routine        \n" +
                "MOV Y, 0x4567          \n" +
                "brk                    \n";

        TestExecutionResult res = TestHelper.executeCode(code);

        assertEquals(0x1234, res.regValue("X"));
        assertEquals(0x4567, res.regValue("Y"));
    }
}
