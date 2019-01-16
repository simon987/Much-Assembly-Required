package net.simon987.server.assembly;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestComment {

    @Test
    public void TestCommentInQuotes() {

        Assembler asm = TestHelper.getTestAsm();

        AssemblyResult r1 = asm.parse("dw \";\", 12");
        assertEquals(r1.bytes.length, 4);
    }

    @Test
    public void TestRegularComment() {

        Assembler asm = TestHelper.getTestAsm();

        AssemblyResult r1 = asm.parse("register_SP: DW \"SP=\",0 ; register_A + 28");
        assertEquals(8, r1.bytes.length);
        assertEquals(0, r1.exceptions.size());
    }

    @Test
    public void TestStandaloneComment() {

        Assembler asm = TestHelper.getTestAsm();

        AssemblyResult r1 = asm.parse("; Set display_mode to DECIMAL_MODE");
        assertEquals(0, r1.bytes.length);
        assertEquals(0, r1.exceptions.size());
    }

}
