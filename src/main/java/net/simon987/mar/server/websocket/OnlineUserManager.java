package net.simon987.mar.server.websocket;

import net.simon987.mar.server.user.User;
import org.eclipse.jetty.websocket.api.Session;

import java.util.ArrayList;
import java.util.List;

public class OnlineUserManager {

    private final List<OnlineUser> onlineUsers = new ArrayList<>(10);


    public OnlineUser getUser(Session socket) {

        List<OnlineUser> _onlineUsers = new ArrayList<>(onlineUsers);

        for (OnlineUser user : _onlineUsers) {
            if (user.getWebSocket().equals(socket)) {
                return user;
            }
        }

        return null;
    }

    public List<OnlineUser> getUser(User user) {

        List<OnlineUser> _onlineUsers = new ArrayList<>(onlineUsers);

        List<OnlineUser> result = new ArrayList<>();

        for (OnlineUser onlineUser : _onlineUsers) {
            if (onlineUser.getUser().equals(user)) {
                result.add(onlineUser);
            }
        }

        return result;
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

    public List<OnlineUser> getOnlineUsers() {
        return onlineUsers;
    }
}
