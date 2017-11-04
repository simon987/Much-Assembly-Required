package net.simon987.cubotplugin.event;

import net.simon987.cubotplugin.Cubot;
import net.simon987.server.GameServer;
import net.simon987.server.event.GameEvent;
import net.simon987.server.event.GameEventListener;
import net.simon987.server.event.UserCreationEvent;
import net.simon987.server.user.User;

import java.awt.*;

public class UserCreationListener implements GameEventListener {
    @Override
    public Class getListenedEventType() {
        return UserCreationEvent.class;
    }

    @Override
    public void handle(GameEvent event) {

        User user = (User)event.getSource();


        Cubot cubot = new Cubot();

        cubot.setWorld(GameServer.INSTANCE.getGameUniverse().getWorld(0,0));
        cubot.getWorld().getGameObjects().add(cubot);

        cubot.setObjectId(GameServer.INSTANCE.getGameUniverse().getNextObjectId());

        cubot.setParent(user);

        Point point = cubot.getWorld().getRandomPassableTile();

        cubot.setX(point.x);
        cubot.setY(point.y);

        user.setControlledUnit(cubot);

    }
}
