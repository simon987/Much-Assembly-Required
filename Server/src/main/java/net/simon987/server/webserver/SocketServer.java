package net.simon987.server.webserver;

import net.simon987.server.GameServer;
import net.simon987.server.ServerConfiguration;
import net.simon987.server.game.ControllableUnit;
import net.simon987.server.logging.LogManager;
import net.simon987.server.user.User;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.DefaultSSLWebSocketServerFactory;
import org.java_websocket.server.WebSocketServer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;


public class SocketServer extends WebSocketServer {

    private OnlineUserManager userManager = new OnlineUserManager();

    private SocketServerDatabase database;

    private MessageEventDispatcher messageEventDispatcher = new MessageEventDispatcher();

    public SocketServer(InetSocketAddress address, ServerConfiguration config) {
        super(address);

        if (config.getInt("use_secure_webSocket") != 0) {

            SSLContext context = getContext(config.getString("cert_path"));
            if (context != null) {
                setWebSocketFactory(new DefaultSSLWebSocketServerFactory(context));

                LogManager.LOGGER.info("(WS) Enabled secure webSocket");
            } else {
                LogManager.LOGGER.severe("(WS) Failed to create SSL context");
            }
        }

        setConnectionLostTimeout(120);
        setReuseAddr(true); //To avoid BindException

        database = new SocketServerDatabase(config);

        messageEventDispatcher.addHandler(new UserInfoRequestHandler());
        messageEventDispatcher.addHandler(new TerrainRequestHandler());
        messageEventDispatcher.addHandler(new ObjectsRequestHandler());
        messageEventDispatcher.addHandler(new CodeUploadHandler());
        messageEventDispatcher.addHandler(new CodeRequestHandler());
        messageEventDispatcher.addHandler(new KeypressHandler());
        messageEventDispatcher.addHandler(new FloppyHandler());

    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        LogManager.LOGGER.info("(WS) New Websocket connection " + conn.getRemoteSocketAddress());
        userManager.add(new OnlineUser(conn));
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        LogManager.LOGGER.info("(WS) Closed " + conn.getRemoteSocketAddress() + " with exit code " + code + " additional info: " + reason);
        userManager.remove(userManager.getUser(conn));
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
                if (message.length() == 128) {

                    String username = database.validateAuthToken(message);

                    if (username != null) {
                        User user = GameServer.INSTANCE.getGameUniverse().getOrCreateUser(username, true);

                        LogManager.LOGGER.info("(WS) User was successfully authenticated: " + user.getUsername());

                        onlineUser.setUser(user);
                        onlineUser.setAuthenticated(true);

                        conn.send("{\"t\":\"auth\", \"m\":\"ok\"}");

                    } else {

                        User user = GameServer.INSTANCE.getGameUniverse().getOrCreateUser(GameServer.INSTANCE.getGameUniverse().getGuestUsername(), false);
                        onlineUser.setUser(user);
                        onlineUser.setAuthenticated(true);
                        onlineUser.setGuest(true);

                        LogManager.LOGGER.info("(WS) Created guest user " +
                                onlineUser.getUser().getUsername() + conn.getRemoteSocketAddress());

                        conn.send("{\"t\":\"auth\", \"m\":\"ok\"}");
                    }
                }

            }

        } else {

            LogManager.LOGGER.severe("(WS) FIXME: SocketServer:onMessage");

        }


    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        //System.out.println("received ByteBuffer from " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {

        if (ex instanceof BindException) {

            LogManager.LOGGER.severe("Address already in use");
            System.exit(-1);

        } else {
            LogManager.LOGGER.severe("an error occurred on connection " + conn + ": " + ex);
            userManager.remove(userManager.getUser(conn));

            ex.printStackTrace();
        }


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

        ArrayList<OnlineUser> onlineUsers = new ArrayList<>(userManager.getOnlineUsers()); //Avoid ConcurrentModificationException
        for (OnlineUser user : onlineUsers) {

            if (user.getWebSocket().isOpen()) {

                if (user.isGuest()) {

                    json.remove("c");
                    user.getWebSocket().send(json.toJSONString());

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
                        user.getWebSocket().send(json.toJSONString());
                    } catch (NullPointerException e) {
                        //User is online but not completely initialised
                    }

                }


            }
        }

    }

    public OnlineUserManager getUserManager() {
        return userManager;
    }


    /**
     * See https://github.com/TooTallNate/Java-WebSocket/blob/master/src/main/example/SSLServerLetsEncryptExample.java
     */
    /*
     *      * Copyright (c) 2010-2017 Nathan Rajlich
     *
     *  Permission is hereby granted, free of charge, to any person
     *  obtaining a copy of this software and associated documentation
     *  files (the "Software"), to deal in the Software without
     *  restriction, including without limitation the rights to use,
     *  copy, modify, merge, publish, distribute, sublicense, and/or sell
     *  copies of the Software, and to permit persons to whom the
     *  Software is furnished to do so, subject to the following
     *  conditions:
     *
     *  The above copyright notice and this permission notice shall be
     *  included in all copies or substantial portions of the Software.
     */
    private static SSLContext getContext(String pathTo) {
        SSLContext context;
        String password = "MAR";
        try {
            context = SSLContext.getInstance("TLS");

            byte[] certBytes = parseDERFromPEM(getBytes(new File(pathTo + File.separator + "cert.pem")),
                    "-----BEGIN CERTIFICATE-----", "-----END CERTIFICATE-----");
            byte[] keyBytes = parseDERFromPEM(getBytes(new File(pathTo + File.separator + "privkey.pem")),
                    "-----BEGIN PRIVATE KEY-----", "-----END PRIVATE KEY-----");

            X509Certificate cert = generateCertificateFromDER(certBytes);
            RSAPrivateKey key = generatePrivateKeyFromDER(keyBytes);

            KeyStore keystore = KeyStore.getInstance("JKS");
            keystore.load(null);
            keystore.setCertificateEntry("cert-alias", cert);
            keystore.setKeyEntry("key-alias", key, password.toCharArray(), new Certificate[]{cert});

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(keystore, password.toCharArray());

            KeyManager[] km = kmf.getKeyManagers();

            context.init(km, null, null);
        } catch (Exception e) {
            context = null;
        }
        return context;
    }

    private static byte[] parseDERFromPEM(byte[] pem, String beginDelimiter, String endDelimiter) {
        String data = new String(pem);
        String[] tokens = data.split(beginDelimiter);
        tokens = tokens[1].split(endDelimiter);
        return DatatypeConverter.parseBase64Binary(tokens[0]);
    }

    private static RSAPrivateKey generatePrivateKeyFromDER(byte[] keyBytes) throws InvalidKeySpecException, NoSuchAlgorithmException {
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);

        KeyFactory factory = KeyFactory.getInstance("RSA");

        return (RSAPrivateKey) factory.generatePrivate(spec);
    }

    private static X509Certificate generateCertificateFromDER(byte[] certBytes) throws CertificateException {
        CertificateFactory factory = CertificateFactory.getInstance("X.509");

        return (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(certBytes));
    }

    private static byte[] getBytes(File file) {
        byte[] bytesArray = new byte[(int) file.length()];

        FileInputStream fis;
        try {
            fis = new FileInputStream(file);
            fis.read(bytesArray); //read file into bytes[]
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytesArray;
    }
}