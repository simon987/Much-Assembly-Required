package net.simon987.server.websocket;

import net.simon987.server.user.User;
import org.eclipse.jetty.websocket.api.Session;

public class OnlineUser {


    private boolean authenticated = false;

    private Session webSocket;

    private boolean guest;

    private boolean moderator;

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

    public void setGuest(boolean guest) {
        this.guest = guest;
        user.setGuest(guest);
    }

    public boolean isGuest() {
        return guest;
    }

    public boolean isModerator() {
        return moderator;
    }

    public void setModerator(boolean moderator) {
        this.moderator = moderator;
    }
}
