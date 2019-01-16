package net.simon987.server.assembly;

import net.simon987.server.FakeConfiguration;
import net.simon987.server.IServerConfiguration;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DWTest {

    @Test
    public void TestSemiColonInString() {

        IServerConfiguration configuration = new FakeConfiguration();

        configuration.setInt("memory_size", 1000);
        configuration.setInt("org_offset", 400);

        Assembler assembler = new Assembler(new DefaultInstructionSet(), new DefaultRegisterSet(), configuration);

        AssemblyResult ar = assembler.parse("DW \";\"");
        assertEquals(0, ar.exceptions.size());
    }
}
