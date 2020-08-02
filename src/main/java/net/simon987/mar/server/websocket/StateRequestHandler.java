package net.simon987.mar.server.websocket;

import net.simon987.mar.server.assembly.CPU;
import net.simon987.mar.server.logging.LogManager;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.Map;

public class StateRequestHandler implements MessageHandler {
    @Override
    public void handle(OnlineUser user, JSONObject json) throws IOException {

        if (json.get("t").equals("stateRequest")) {

            LogManager.LOGGER.fine("(WS) State request from " + user.getUser().getUsername());

            if (user.getUser().isGuest()) {
                return;
            }

            sendState(user);
        }
    }

    public static void sendState(OnlineUser user) throws IOException {
        JSONObject response = new JSONObject();

        CPU cpu = user.getUser().getControlledUnit().getCpu();

        if (!cpu.isPaused()) {
            return;
        }

        response.put("t", "state");
        response.put("memory", cpu.getMemory().toString());
        response.put("status", cpu.getStatus().toString());
        response.put("registers", cpu.getRegisterSet().toString());
        Map<Integer, Integer> codeLineMap = user.getUser().getCodeLineMap();
        Integer line = codeLineMap == null ? null : codeLineMap.get(cpu.getIp());
        response.put("line", line == null ? 0 : line);

        user.getWebSocket().getRemote().sendString(response.toJSONString());
    }
}
