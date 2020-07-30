package net.simon987.mar.server.assembly;

import net.simon987.mar.server.TestExecutionResult;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OrgDirectiveTest {

    @Test
    public void orgWikiExample() {
        String code = "" +
                "ORG 0x8000            \n" +
                "my_var: DW 0x1234     \n" +
                ".text                 \n" +
                "MOV A, my_var       \n" +
                "brk                   \n";

        TestExecutionResult res = TestHelper.executeCode(code);

        assertTrue(res.ar.exceptions.isEmpty());
        assertEquals(0x8000, res.ar.origin);
        assertEquals(0x1234, res.memValue(0x8000));
        assertEquals(0x8000, res.regValue("A"));
    }
}
