package net.simon987.server.websocket;

import net.simon987.server.GameServer;
import net.simon987.server.game.objects.ControllableUnit;
import net.simon987.server.logging.LogManager;
import net.simon987.server.user.User;
import net.simon987.server.web.GuestPolicy;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebSocket
public class SocketServer {

    private OnlineUserManager onlineUserManager = new OnlineUserManager();

    private MessageDispatcher messageDispatcher = new MessageDispatcher();

    private static final String AUTH_OK_MESSAGE = "{\"t\":\"auth\", \"m\":\"ok\"}";
    private static final String FORBIDDEN_MESSAGE = "{\"t\":\"auth\", \"m\":\"forbidden\"}";
    private static final int AUTH_TOKEN_LEN = 128;

    private GuestPolicy guestPolicy;

    public SocketServer(GuestPolicy guestPolicy) {

        this.guestPolicy = guestPolicy;

        messageDispatcher.addHandler(new UserInfoRequestHandler());
        messageDispatcher.addHandler(new TerrainRequestHandler());
        messageDispatcher.addHandler(new ObjectsRequestHandler());
        messageDispatcher.addHandler(new CodeUploadHandler());
        messageDispatcher.addHandler(new CodeRequestHandler());
        messageDispatcher.addHandler(new KeypressHandler());
        messageDispatcher.addHandler(new DebugCommandHandler());

    }

    @OnWebSocketConnect
    public void onOpen(Session session) {
        LogManager.LOGGER.info("(WS) New Websocket connection " + session.getRemoteAddress().getAddress());
        onlineUserManager.add(new OnlineUser(session));
    }

    @OnWebSocketClose
    public void onClose(Session session, int code, String reason) {
        LogManager.LOGGER.info("(WS) Closed " + session.getRemoteAddress().getAddress() + " with exit code " + code + " additional info: " + reason);
        onlineUserManager.remove(onlineUserManager.getUser(session));
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        OnlineUser onlineUser = onlineUserManager.getUser(session);

        //Shouldn't happen
        if (onlineUser == null) {
            LogManager.LOGGER.severe("(WS) FIXME: SocketServer:onMessage");
            return;
        }

        //Authenticated user
        if (onlineUser.isAuthenticated()) {
            messageDispatcher.dispatch(onlineUser, message);
            return;
        }

        //Handle auth request
        if (message.length() == AUTH_TOKEN_LEN) {
            LogManager.LOGGER.info("(WS) Received message from unauthenticated user " + session.getRemoteAddress().getAddress());

            User user = GameServer.INSTANCE.getUserManager().validateAuthToken(message);

            if (user != null) {
                doPostAuthUser(session, onlineUser, user);
            } else if (this.guestPolicy != GuestPolicy.BLOCK) {
                doPostAuthGuest(session, onlineUser);
            } else {
                LogManager.LOGGER.info("(WS) Blocked guest user " + session.getRemoteAddress().getAddress());
                kickOnlineUser(session);
            }
        }

        //Ignore other cases
    }

    private void kickOnlineUser(Session session) {
        sendString(session, FORBIDDEN_MESSAGE);
        session.close();
    }

    private void doPostAuthGuest(Session session, OnlineUser onlineUser) {
        User guestUser = GameServer.INSTANCE.getGameUniverse().getOrCreateUser(GameServer.INSTANCE.getGameUniverse().getGuestUsername(), false);
        onlineUser.setUser(guestUser);
        onlineUser.setAuthenticated(true);
        onlineUser.getUser().setGuest(true);

        LogManager.LOGGER.info("(WS) Created guest user " +
                onlineUser.getUser().getUsername() + session.getRemoteAddress().getAddress());

        sendString(session, AUTH_OK_MESSAGE);
    }

    private void doPostAuthUser(Session session, OnlineUser onlineUser, User user) {
        LogManager.LOGGER.info("(WS) User was successfully authenticated: " + user.getUsername());

        onlineUser.setUser(user);
        onlineUser.setAuthenticated(true);

        sendString(session, AUTH_OK_MESSAGE);
    }

    /**
     * Called every tick
     */
    public void tick() {

        //Avoid ConcurrentModificationException
        ArrayList<OnlineUser> onlineUsers = new ArrayList<>(onlineUserManager.getOnlineUsers());

        for (OnlineUser user : onlineUsers) {
            if (user.getWebSocket().isOpen() && user.getUser() != null) {

                JSONObject json = new JSONObject();
                json.put("t", "tick");

                if (user.getUser().isGuest()) {

                    sendJSONObject(user, json);

                } else {
                    ControllableUnit unit = user.getUser().getControlledUnit();

                    json.put("c", charArraysToJSON(unit.getConsoleMessagesBuffer()));
                    json.put("keys", intListToJSON(unit.getKeyboardBuffer()));
                    json.put("cm", unit.getConsoleMode());

                    sendJSONObject(user, json);
                }
            }
        }
    }

    private void sendString(Session session, String message) {
        try {
            session.getRemote().sendString(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendJSONObject(OnlineUser user, JSONObject json) {
        try {
            user.getWebSocket().getRemote().sendString((json.toJSONString()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            //Ignore
        }
    }

    private JSONArray charArraysToJSON(List<char[]> charArrays) {

        JSONArray jsonMessages = new JSONArray();

        for (char[] message : charArrays) {
            jsonMessages.add(new String(message));
        }

        return jsonMessages;
    }

    private JSONArray intListToJSON(List<Integer> ints) {

        JSONArray jsonInts = new JSONArray();
        jsonInts.addAll(ints);

        return jsonInts;
    }
}