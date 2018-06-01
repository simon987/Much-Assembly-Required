package net.simon987.server.game.debug;

import net.simon987.server.GameServer;
import net.simon987.server.event.DebugCommandEvent;
import net.simon987.server.event.GameEvent;
import net.simon987.server.event.GameEventListener;
import net.simon987.server.game.objects.ControllableUnit;
import net.simon987.server.user.User;

public class UserInfoCommandListener implements GameEventListener {

    @Override
    public Class getListenedEventType() {
        return DebugCommandEvent.class;
    }

    @Override
    public void handle(GameEvent event) {

        DebugCommandEvent e = (DebugCommandEvent) event;

        if (e.getName().equals("userInfo")) {

            User user = GameServer.INSTANCE.getGameUniverse().getUser(e.getString("username"));

            if (user != null) {

                String message = "Showing information for user " + e.getString("username") + "\n";

                message += "isGuest: " + user.isGuest() + "\n";

                ControllableUnit unit = user.getControlledUnit();
                message += "ControlledUnit: " + unit.getObjectId() + " at (" + unit.getX() + ", " + unit.getY() + ")\n";

                message += "CPU:" + user.getControlledUnit().getCpu() + "\n";
                message += "Code: " + user.getUserCode();

                e.reply(message);


            } else {
                e.reply("User not found");
            }
        }
    }

}
