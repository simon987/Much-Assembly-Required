package net.simon987.mar.server.event;

import net.simon987.mar.server.user.User;

public class UserCreationEvent extends GameEvent {

    public UserCreationEvent(User user) {
        setSource(user);
    }
}
