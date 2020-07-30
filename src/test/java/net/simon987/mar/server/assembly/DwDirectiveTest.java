package net.simon987.mar.server.assembly;

import net.simon987.mar.server.TestExecutionResult;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DwDirectiveTest {
    @Test
    public void dwStringWikiExample() {
        String code = "" +
                "my_str: DW \"Hello\"  \n" +
                ".text                 \n" +
                "MOV A, [my_str]       \n" +
                "brk                   \n";

        TestExecutionResult res = TestHelper.executeCode(code);

        assertTrue(res.ar.exceptions.isEmpty());
        assertEquals('H', res.regValue("A"));
    }
}
