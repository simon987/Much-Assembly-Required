package net.simon987.server.user;

import net.simon987.server.GameServer;
import net.simon987.server.assembly.CPU;
import net.simon987.server.assembly.exception.CancelledException;
import net.simon987.server.event.GameEvent;
import net.simon987.server.event.UserCreationEvent;
import net.simon987.server.game.ControllableUnit;
import net.simon987.server.io.JSONSerialisable;
import org.json.simple.JSONObject;

/**
 * Represents a User (or player) of the game
 */
public class User implements JSONSerialisable{

    private String username;

    private String userCode;

    private CPU cpu;

    private ControllableUnit controlledUnit;

    private boolean guest;

    public User() throws CancelledException {
        GameEvent event = new UserCreationEvent(this);
        GameServer.INSTANCE.getEventDispatcher().dispatch(event);
        if(event.isCancelled()) {
            throw new CancelledException();
        }


    }

    public User(ControllableUnit unit) {
        this.controlledUnit = unit;
    }

    @Override
    public JSONObject serialise() {

        JSONObject json = new JSONObject();

        json.put("username", username);
        json.put("code", userCode);
        json.put("controlledUnit", controlledUnit.getObjectId());
        json.put("cpu", cpu.serialise());

        return json;


    }

    public static User deserialize(JSONObject userJson) throws CancelledException {

        User user = new User((ControllableUnit)GameServer.INSTANCE.getGameUniverse().getObject((int)(long)userJson.get("controlledUnit")));
        user.username = (String)userJson.get("username");
        user.userCode = (String)userJson.get("code");

        user.getControlledUnit().setParent(user);

        user.cpu = CPU.deserialize((JSONObject)userJson.get("cpu"), user);

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
