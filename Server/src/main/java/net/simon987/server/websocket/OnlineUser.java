package net.simon987.server.websocket;

import net.simon987.server.user.User;
import org.eclipse.jetty.websocket.api.Session;

public class OnlineUser {


    private boolean authenticated = false;

    private Session webSocket;

    /**
     * Associated game user (if authenticated)
     */
    private User user;

    public OnlineUser(Session webSocket) {
        this.webSocket = webSocket;

    }

    public Session getWebSocket() {
        return webSocket;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

}
