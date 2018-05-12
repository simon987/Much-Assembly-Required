package net.simon987.server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.util.ArrayList;

public class OnlineUserManager {

    /**
     * List of online users.
     */
    private ArrayList<OnlineUser> onlineUsers = new ArrayList<>(10);


    public OnlineUser getUser(Session socket) {

        ArrayList<OnlineUser> _onlineUsers = new ArrayList<>(onlineUsers);

        for (OnlineUser user : _onlineUsers) {
            if (user.getWebSocket().equals(socket)) {
                return user;
            }
        }

        return null;
    }

    /**
     * Add an user to the list
     *
     * @param user user to add
     */
    public void add(OnlineUser user) {
        onlineUsers.add(user);
    }

    /**
     * Remove an user to the list
     *
     * @param user user to remove
     */
    public void remove(OnlineUser user) {
        onlineUsers.remove(user);
    }

    public ArrayList<OnlineUser> getOnlineUsers() {
        return onlineUsers;
    }
}
