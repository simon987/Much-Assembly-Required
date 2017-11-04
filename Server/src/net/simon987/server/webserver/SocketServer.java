package net.simon987.server.webserver;

import net.simon987.server.GameServer;
import net.simon987.server.ServerConfiguration;
import net.simon987.server.logging.LogManager;
import net.simon987.server.user.User;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class SocketServer extends WebSocketServer {

    private OnlineUserManager userManager = new OnlineUserManager();

    private SocketServerDatabase database;

    private MessageEventDispatcher messageEventDispatcher = new MessageEventDispatcher();

    public SocketServer(InetSocketAddress address, ServerConfiguration config) {
        super(address);

        database = new SocketServerDatabase(config);

        messageEventDispatcher.addHandler(new UserInfoRequestHandler());
        messageEventDispatcher.addHandler(new TerrainRequestHandler());
        messageEventDispatcher.addHandler(new ObjectsRequestHandler());
        messageEventDispatcher.addHandler(new CodeUploadHandler());
        messageEventDispatcher.addHandler(new CodeRequestHandler());
        messageEventDispatcher.addHandler(new KeypressHandler());

    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        LogManager.LOGGER.info("(WS) New Websocket connection " + conn.getRemoteSocketAddress());

        userManager.add(new OnlineUser(conn));
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        LogManager.LOGGER.info("(WS) Closed " + conn.getRemoteSocketAddress() + " with exit code " + code + " additional info: " + reason);
        userManager.add(new OnlineUser(conn));
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        OnlineUser onlineUser = userManager.getUser(conn);

        if (onlineUser != null) {

            if (onlineUser.isAuthenticated()) {

                messageEventDispatcher.dispatch(onlineUser, message);

            } else {
                LogManager.LOGGER.info("(WS) Received message from unauthenticated user " + conn.getRemoteSocketAddress());

                //We expect a 128 characters long token
                if(message.length() == 128) {

                    String username = database.validateAuthToken(message);

                    if (username != null) {
                        User user = GameServer.INSTANCE.getGameUniverse().getOrCreateUser(username);

                        LogManager.LOGGER.info("(WS) User was successfully authenticated: " + user.getUsername());

                        onlineUser.setUser(user);
                        onlineUser.setAuthenticated(true);

                        conn.send("{\"t\":\"auth\", \"m\":\"ok\"}");

                    } else {
                        LogManager.LOGGER.info("(WS) Unsuccessful authentication attempt " + conn.getRemoteSocketAddress());
                        conn.send("{\"t\":\"auth\", \"m\":\"failed\"}");
                        conn.close();
                    }
                }


            }

        } else {

            LogManager.LOGGER.info("(WS) FIXME: SocketServer:onMessage");

        }


    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        System.out.println("received ByteBuffer from " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("an error occured on connection " + conn.getRemoteSocketAddress() + ':' + ex);
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        LogManager.LOGGER.info("(WS) Server started successfully");
    }

    /**
     * Called every tick
     */
    public void tick() {

        JSONObject json = new JSONObject();
        json.put("t", "tick");

        LogManager.LOGGER.info("Notified " + userManager.getOnlineUsers().size() + " users");

        for (OnlineUser user : userManager.getOnlineUsers()) {

            if (user.getWebSocket().isOpen()) {
                //Send keyboard updated buffer
                try{
                    ArrayList<Integer> kbBuffer = user.getUser().getControlledUnit().getKeyboardBuffer();
                    JSONArray keys = new JSONArray();
                    keys.addAll(kbBuffer);
                    json.put("keys", keys);
                    //Send tick message
                    user.getWebSocket().send(json.toJSONString());
                } catch (NullPointerException e){
                    //User is online but not completely initialised
                }

            }
        }

    }

    public OnlineUserManager getUserManager() {
        return userManager;
    }
}