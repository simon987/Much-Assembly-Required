package net.simon987.npcplugin;

import net.simon987.server.GameServer;
import net.simon987.server.event.ObjectDeathEvent;
import net.simon987.server.game.objects.Direction;
import org.bson.Document;
import org.json.simple.JSONObject;


public class HarvesterNPC extends NonPlayerCharacter {

    public static final int MAX_HEALTH = GameServer.INSTANCE.getConfig().getInt("harvester_hp_max");
    public static final int HEAL_RATE = GameServer.INSTANCE.getConfig().getInt("harvester_regen");

    public HarvesterNPC() {
        setTask(new HarvestTask());

        setHp(MAX_HEALTH);
        setMaxHp(MAX_HEALTH);
        setHealRate(HEAL_RATE);
    }

    public HarvesterNPC(Document document) {
        super(document);

        setTask(new HarvestTask());

        setDirection(Direction.getDirection(document.getInteger("direction")));
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
    public boolean onDeadCallback() {

        getWorld().decUpdatable();

        if (getFactory() != null && getFactory().getNpcs() != null) {
            getFactory().getNpcs().remove(this);
        }

        GameServer.INSTANCE.getEventDispatcher().dispatch(new ObjectDeathEvent(this));

        return false;
    }

    @Override
    public JSONObject jsonSerialise() {
        JSONObject json = super.jsonSerialise();

        json.put("direction", getDirection().ordinal());
        json.put("hp", getHp());
        json.put("action", getAction().ordinal());

        return json;
    }

    @Override
    public JSONObject debugJsonSerialise() {
        JSONObject json = jsonSerialise();

        json.put("taskCompleted", getTask().checkCompleted());

        return json;
    }

    @Override
    public Document mongoSerialise() {
        Document dbObject = super.mongoSerialise();

        dbObject.put("direction", getDirection().ordinal());
        dbObject.put("hp", getHp());
        dbObject.put("action", getAction().ordinal());

        return dbObject;
    }
}
