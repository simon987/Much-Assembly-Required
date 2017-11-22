package net.simon987.server.event;

import net.simon987.server.assembly.CPU;
import net.simon987.server.user.User;

public class CpuInitialisationEvent extends GameEvent {

    private User user;

    public CpuInitialisationEvent(CPU cpu, User user) {
        setSource(cpu);
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
