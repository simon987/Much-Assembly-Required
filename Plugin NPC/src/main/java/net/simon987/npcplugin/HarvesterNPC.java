package net.simon987.npcplugin;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import net.simon987.server.GameServer;
import net.simon987.server.event.ObjectDeathEvent;
import net.simon987.server.game.Direction;
import org.json.simple.JSONObject;


public class HarvesterNPC extends NonPlayerCharacter {

    public static final int ID = 10;

    public static final int MAX_HEALTH = GameServer.INSTANCE.getConfig().getInt("harvester_hp_max");
    public static final int HEAL_RATE = GameServer.INSTANCE.getConfig().getInt("harvester_regen");


    public HarvesterNPC() {
        setTask(new HarvestTask());

        setHp(MAX_HEALTH);
        setMaxHp(MAX_HEALTH);
        setHealRate(HEAL_RATE);
    }

    @Override
    public void update() {

        super.update();

        if (getFactory() != null) {
            if (getTask().checkCompleted()) {

                setTask(new HarvestTask());

            } else {
                getTask().tick(this);
            }

            //Self-destroy when age limit is reached
            if (getAge() >= NonPlayerCharacter.LIFETIME) {
                setDead(true);
            }
        }
    }

    @Override
    public void onDeadCallback() {

        if (getFactory() != null && getFactory().getNpcs() != null) {
            getFactory().getNpcs().remove(this);
        }

        GameServer.INSTANCE.getEventDispatcher().dispatch(
                new ObjectDeathEvent(this, ID));
    }

    @Override
    public JSONObject serialise() {
        JSONObject json = super.serialise();

        json.put("i", getObjectId());
        json.put("x", getX());
        json.put("y", getY());
        json.put("direction", getDirection().ordinal());
        json.put("hp", getHp());
        json.put("energy", energy);
        json.put("action", getAction().ordinal());
        json.put("t", ID);

        return json;
    }

    @Override
    public BasicDBObject mongoSerialise() {
        BasicDBObject dbObject = new BasicDBObject();

        dbObject.put("i", getObjectId());
        dbObject.put("x", getX());
        dbObject.put("y", getY());
        dbObject.put("direction", getDirection().ordinal());
        dbObject.put("hp", getHp());
        //  dbObject.put("energy", energy);
        dbObject.put("action", getAction().ordinal());
        dbObject.put("t", ID);

        return dbObject;
    }

    public static HarvesterNPC deserialize(DBObject obj) {

        HarvesterNPC npc = new HarvesterNPC();
        npc.setObjectId((long) obj.get("i"));
        npc.setX((int) obj.get("x"));
        npc.setY((int) obj.get("y"));
        npc.setHp((int) obj.get("hp"));
        npc.setDirection(Direction.getDirection((int) obj.get("direction")));
        // npc.energy = (int) obj.get("energy");
        // npc.maxEnergy = GameServer.INSTANCE.getConfig().getInt("battery_max_energy");

        return npc;

    }
}
