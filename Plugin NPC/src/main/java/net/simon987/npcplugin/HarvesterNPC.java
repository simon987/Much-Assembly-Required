package net.simon987.npcplugin;

import net.simon987.server.GameServer;
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
                getFactory().getNpcs().remove(this);
            }
        }
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

    public static HarvesterNPC deserialize(JSONObject json) {

        HarvesterNPC npc = new HarvesterNPC();
        npc.setObjectId((long) json.get("i"));
        npc.setX((int) (long) json.get("x"));
        npc.setY((int) (long) json.get("y"));
        npc.setHp((int) (long) json.get("hp"));
        npc.setDirection(Direction.getDirection((int) (long) json.get("direction")));
        npc.energy = (int) (long) json.get("energy");
        npc.maxEnergy = GameServer.INSTANCE.getConfig().getInt("battery_max_energy");

        return npc;

    }
}
