package net.simon987.server.websocket;

import net.simon987.server.game.ControllableUnit;
import net.simon987.server.logging.LogManager;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

@WebSocket
public class SocketServer {

    private OnlineUserManager onlineUserManager = new OnlineUserManager();

    private MessageDispatcher messageDispatcher = new MessageDispatcher();

    public SocketServer() {

        messageDispatcher.addHandler(new UserInfoRequestHandler());
        messageDispatcher.addHandler(new TerrainRequestHandler());
        messageDispatcher.addHandler(new ObjectsRequestHandler());
        messageDispatcher.addHandler(new CodeUploadHandler());
        messageDispatcher.addHandler(new CodeRequestHandler());
        messageDispatcher.addHandler(new KeypressHandler());
        messageDispatcher.addHandler(new FloppyHandler());
        messageDispatcher.addHandler(new DebugCommandHandler());
    }

    @OnWebSocketConnect
    public void onOpen(Session session) {
        LogManager.LOGGER.info("(WS) New Websocket connection " + session.getRemoteAddress());
        onlineUserManager.add(new OnlineUser(session));
    }

    @OnWebSocketClose
    public void onClose(Session session, int code, String reason) {
        LogManager.LOGGER.info("(WS) Closed " + session.getRemoteAddress() + " with exit code " + code + " additional info: " + reason);
        onlineUserManager.remove(onlineUserManager.getUser(session));
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        OnlineUser onlineUser = onlineUserManager.getUser(session);

        if (onlineUser != null) {

            if (onlineUser.isAuthenticated()) {

                messageDispatcher.dispatch(onlineUser, message);

            } else {

                LogManager.LOGGER.info("(WS) Received message from unauthenticated user " + session.getRemoteAddress());
                //todo

            }

        } else {

            LogManager.LOGGER.severe("(WS) FIXME: SocketServer:onMessage");

        }
    }

    /**
     * Called every tick
     */
    public void tick() {

        JSONObject json = new JSONObject();
        json.put("t", "tick");

        ArrayList<OnlineUser> onlineUsers = new ArrayList<>(onlineUserManager.getOnlineUsers()); //Avoid ConcurrentModificationException
        for (OnlineUser user : onlineUsers) {

            if (user.getWebSocket().isOpen()) {

                if (user.isGuest()) {

                    json.remove("c");
                    try {
                        user.getWebSocket().getRemote().sendString((json.toJSONString()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    try {
                        ControllableUnit unit = user.getUser().getControlledUnit();

                        //Send keyboard updated buffer
                        ArrayList<Integer> kbBuffer = unit.getKeyboardBuffer();
                        JSONArray keys = new JSONArray();
                        keys.addAll(kbBuffer);
                        json.put("keys", keys);

                        //Send console buffer
                        if (unit.getConsoleMessagesBuffer().size() > 0) {

                            JSONArray buff = new JSONArray();

                            for (char[] message : unit.getConsoleMessagesBuffer()) {
                                buff.add(new String(message));
                            }

                            json.put("c", buff);
                        } else {
                            json.remove("c");
                        }

                        json.put("cm", unit.getConsoleMode());

                        //Send tick message
                        user.getWebSocket().getRemote().sendString(json.toJSONString());
                    } catch (NullPointerException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }
}