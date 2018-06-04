package net.simon987.server.user;

import net.simon987.server.GameServer;
import net.simon987.server.assembly.exception.CancelledException;
import net.simon987.server.event.GameEvent;
import net.simon987.server.event.UserCreationEvent;
import net.simon987.server.game.objects.ControllableUnit;
import net.simon987.server.io.MongoSerializable;
import org.bson.Document;

/**
 * Represents a User (or player) of the game
 */
public class User implements MongoSerializable {

    private String username;

    private String userCode;
    private String password;
    private String accessToken;

    private ControllableUnit controlledUnit;

    private boolean guest = false;
    private boolean moderator = false;

    private UserStats stats;

    public User() throws CancelledException {
        GameEvent event = new UserCreationEvent(this);
        GameServer.INSTANCE.getEventDispatcher().dispatch(event);
        if (event.isCancelled()) {
            throw new CancelledException();
        }

        this.stats = new UserStats();
    }

    public User(ControllableUnit unit) {
        this.controlledUnit = unit;
    }

    @Override
    public Document mongoSerialise() {

        Document dbObject = new Document();

        dbObject.put("_id", username); // a constant id ensures only one entry per user is kept and updated, instead of a new entry created every save for every user.
        dbObject.put("username", username);
        dbObject.put("code", userCode);
        dbObject.put("controlledUnit", controlledUnit.getObjectId());
        dbObject.put("password", password);
        dbObject.put("moderator", moderator);
        dbObject.put("stats", stats.mongoSerialise());

        return dbObject;

    }

    public static User deserialize(Document obj) throws CancelledException {

        User user = new User((ControllableUnit) GameServer.INSTANCE.getGameUniverse().getObject(obj.getObjectId("controlledUnit")));
        user.getControlledUnit().setParent(user);
        user.username = (String) obj.get("username");
        user.userCode = (String) obj.get("code");
        user.password = (String) obj.get("password");
        user.moderator = (boolean) obj.get("moderator");
        user.stats = new UserStats((Document) obj.get("stats"));

        return user;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUsername() {
        return username;
    }

    public ControllableUnit getControlledUnit() {
        return controlledUnit;
    }

    public void setControlledUnit(ControllableUnit controlledUnit) {
        this.controlledUnit = controlledUnit;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isGuest() {
        return guest;
    }

    public void setGuest(boolean guest) {
        this.guest = guest;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public boolean isModerator() {
        return moderator;
    }

    public void setModerator(boolean moderator) {
        this.moderator = moderator;
    }

    public UserStats getStats() {
        return stats;
    }
}
