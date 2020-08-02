package net.simon987.mar.server.user;

import net.simon987.mar.server.GameServer;
import net.simon987.mar.server.assembly.exception.CancelledException;
import net.simon987.mar.server.event.GameEvent;
import net.simon987.mar.server.event.UserCreationEvent;
import net.simon987.mar.server.game.objects.ControllableUnit;
import net.simon987.mar.server.io.MongoSerializable;
import org.bson.Document;

import java.util.*;

public class User implements MongoSerializable {

    private String username;

    private String userCode;
    private String password;
    private String accessToken;

    private ControllableUnit controlledUnit;

    private boolean guest = false;
    private boolean moderator = false;

    private UserStats stats;

    private Map<Integer, Integer> codeLineMap;

    private List<String> disassemblyLines;

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
        dbObject.put("disassembly", disassemblyLines);

        List<List<Integer>> codeLineList = new ArrayList<>();
        if (codeLineMap != null) {
            for (int offset: codeLineMap.keySet()) {
                codeLineList.add(Arrays.asList(offset, codeLineMap.get(offset)));
            }
        }
        dbObject.put("codeLineMap", codeLineList);


        return dbObject;
    }

    public static User deserialize(Document obj) throws CancelledException {

        User user = new User((ControllableUnit) GameServer.INSTANCE.getUniverse().getObject(obj.getObjectId("controlledUnit")));
        user.getControlledUnit().setParent(user);
        user.username = (String) obj.get("username");
        user.userCode = (String) obj.get("code");

        user.password = (String) obj.get("password");
        user.moderator = (boolean) obj.get("moderator");
        user.stats = new UserStats((Document) obj.get("stats"));

        List<List<Integer>> codeLineList = (List<List<Integer>>) obj.get("codeLineMap");
        if (codeLineList != null) {
            user.codeLineMap = new HashMap<>(codeLineList.size());
            for (List<Integer> tuple: codeLineList) {
                user.codeLineMap.put(tuple.get(0), tuple.get(1));
            }
        }
        user.disassemblyLines = (List<String>) obj.get("disassembly");

        return user;
    }

    public void addTime(int time){
        this.stats.incrementStat("executionTime", time);
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public void setCodeLineMap(Map<Integer, Integer> codeLineMap) {
        this.codeLineMap = codeLineMap;
    }

    public Map<Integer, Integer> getCodeLineMap() {
        return codeLineMap;
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

    public List<String> getDisassembly() {
        return disassemblyLines;
    }

    public void setDisassembly(List<String> disassemblyLines) {
        this.disassemblyLines = disassemblyLines;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }
}
