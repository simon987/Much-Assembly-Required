package net.simon987.mar.server.websocket;

import net.simon987.mar.server.GameServer;
import net.simon987.mar.server.assembly.CPU;
import net.simon987.mar.server.logging.LogManager;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.Map;

public class DebugStepHandler implements MessageHandler {

    public static String pausedStatePrompt(Integer line, boolean stateSent) {
        JSONObject response = new JSONObject();
        response.put("t", "paused");
        response.put("line", line == null ? -1 : line);
        response.put("stateSent", stateSent);
        return response.toJSONString();
    }

    @Override
    public void handle(OnlineUser user, JSONObject json) throws IOException {

        if (json.get("t").equals("debugStep")) {

            LogManager.LOGGER.fine("(WS) Debug step from " + user.getUser().getUsername());

            if (user.getUser().isGuest()) {
                return;
            }

            CPU cpu = user.getUser().getControlledUnit().getCpu();

            if (!cpu.isPaused()) {
                return;
            }

            if (json.get("mode").equals("step")) {
                GameServer.INSTANCE.execLock.readLock().lock();
                cpu.step();
                GameServer.INSTANCE.execLock.readLock().unlock();

                Map<Integer, Integer> lineMap = user.getUser().getCodeLineMap();
                Integer line = lineMap.get(cpu.getIp());

                // Automatically send state when stepping through code to reduce latency
                user.getWebSocket().getRemote().sendString(pausedStatePrompt(line, true));
                StateRequestHandler.sendState(user);

            } else if (json.get("mode").equals("continue")) {
                cpu.setTrapFlag(false);
                GameServer.INSTANCE.executeUserCode(user.getUser());
            }
        }
    }
}
