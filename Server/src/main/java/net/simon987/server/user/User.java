package net.simon987.server.user;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import net.simon987.server.GameServer;
import net.simon987.server.assembly.CPU;
import net.simon987.server.assembly.exception.CancelledException;
import net.simon987.server.event.GameEvent;
import net.simon987.server.event.UserCreationEvent;
import net.simon987.server.game.ControllableUnit;
import net.simon987.server.io.MongoSerialisable;

/**
 * Represents a User (or player) of the game
 */
public class User implements MongoSerialisable {

    private String username;

    private String userCode;

    private CPU cpu;

    private ControllableUnit controlledUnit;

    private boolean guest;

    public User() throws CancelledException {
        GameEvent event = new UserCreationEvent(this);
        GameServer.INSTANCE.getEventDispatcher().dispatch(event);
        if (event.isCancelled()) {
            throw new CancelledException();
        }


    }

    public User(ControllableUnit unit) {
        this.controlledUnit = unit;
    }

    @Override
    public BasicDBObject mongoSerialise() {

        BasicDBObject dbObject = new BasicDBObject();

        dbObject.put("_id", username); // a constant id ensures only one entry per user is kept and updated, instead of a new entry created every save for every user.
        dbObject.put("username", username);
        dbObject.put("code", userCode);
        dbObject.put("controlledUnit", controlledUnit.getObjectId());
        dbObject.put("cpu", cpu.mongoSerialise());

        return dbObject;

    }

    public static User deserialize(DBObject obj) throws CancelledException {

        User user = new User((ControllableUnit) GameServer.INSTANCE.getGameUniverse().getObject((long) obj.get("controlledUnit")));
        user.username = (String) obj.get("username");
        user.userCode = (String) obj.get("code");

        user.getControlledUnit().setParent(user);

        user.cpu = CPU.deserialize((DBObject) obj.get("cpu"), user);

        return user;
    }
    //----

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public CPU getCpu() {
        return cpu;
    }

    public void setCpu(CPU cpu) {
        this.cpu = cpu;
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
}
