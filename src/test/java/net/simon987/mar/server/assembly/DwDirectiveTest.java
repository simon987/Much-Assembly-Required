package net.simon987.mar.server.assembly;

import net.simon987.mar.server.FakeConfiguration;
import net.simon987.mar.server.IServerConfiguration;
import net.simon987.mar.server.TestExecutionResult;
import org.junit.Test;

import static org.junit.Assert.*;

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

    @Test
    public void semiColonInString() {
        String code = "DW \";\"";

        TestExecutionResult res = TestHelper.executeCode(code);

        assertTrue(res.ar.exceptions.isEmpty());
    }

    @Test
    public void nullEscape() {
        String code = "DW \"\\0\\0\"";

        TestExecutionResult res = TestHelper.executeCode(code);

        assertTrue(res.ar.exceptions.isEmpty());
    }

    @Test
    public void unicode1() {
        String code = "DW \"\\u0123\"";

        TestExecutionResult res = TestHelper.executeCode(code);

        assertTrue(res.ar.exceptions.isEmpty());
        assertEquals(0x0123 ,res.memValue(res.ar.origin));
    }

    @Test
    public void escapeQuote() {
        String code = "DW \"\\\"\"";

        TestExecutionResult res = TestHelper.executeCode(code);

        assertTrue(res.ar.exceptions.isEmpty());
        assertEquals('"' ,res.memValue(res.ar.origin));
    }
}
