package net.simon987.mar.server.assembly.instruction;

import net.simon987.mar.server.FakeHardwareHost;
import net.simon987.mar.server.TestExecutionResult;
import net.simon987.mar.server.assembly.TestHelper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HwiInstructionTest {

    @Test
    public void hwiSimple1() {
        String code = "" +
                "MOV A, 0x123           \n" +
                "HWI 0x4567             \n" +
                "MOV A, 0x789           \n" +
                "brk                    \n";

        TestExecutionResult res = TestHelper.executeCode(code);

        FakeHardwareHost.HwiCall call = res.hwiHistory.get(0);
        assertEquals(0x4567, call.address);
        assertEquals(0x123, call.state.registers.getRegister("A").getValue());
    }
}
