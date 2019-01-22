package net.simon987.server.assembly;

import net.simon987.server.FakeConfiguration;
import net.simon987.server.IServerConfiguration;

class TestHelper {

    static Assembler getTestAsm() {

        IServerConfiguration configuration = new FakeConfiguration();

        configuration.setInt("memory_size", 1000);
        configuration.setInt("org_offset", 400);

        return new Assembler(new DefaultInstructionSet(), new DefaultRegisterSet(), configuration);
    }

}
