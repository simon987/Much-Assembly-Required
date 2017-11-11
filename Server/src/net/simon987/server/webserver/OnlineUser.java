package net.simon987.server.webserver;

import net.simon987.server.user.User;
import org.java_websocket.WebSocket;

public class OnlineUser {


    private boolean authenticated = false;

    private WebSocket webSocket;

    private boolean guest;

    /**
     * Associated game user (if authenticated)
     */
    private User user;

    public OnlineUser(WebSocket webSocket) {
        this.webSocket = webSocket;

    }

    public WebSocket getWebSocket() {
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

    public void setGuest(boolean guest) {
        this.guest = guest;
        user.setGuest(guest);
    }

    public boolean isGuest() {
        return guest;
    }
}
