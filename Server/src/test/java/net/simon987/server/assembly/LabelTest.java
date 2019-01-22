package net.simon987.server.assembly;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LabelTest {

    @Test
    public void TestNumericLabel() {

        Assembler asm = TestHelper.getTestAsm();

        AssemblyResult ar = asm.parse("999:");

        assertEquals(ar.labels.size(), 0);
    }

    @Test
    public void TestValidLabel() {

        Assembler asm = TestHelper.getTestAsm();

        AssemblyResult ar = asm.parse("\ttest_label: dw 1 ; comment");

        assertNotNull(ar.labels.get("test_label"));
    }

}
