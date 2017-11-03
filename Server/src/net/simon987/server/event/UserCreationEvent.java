package net.simon987.server.event;

import net.simon987.server.user.User;

public class UserCreationEvent extends GameEvent {

    public UserCreationEvent(User user) {
        setSource(user);
    }
}
