package net.simon987.npcplugin;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import net.simon987.server.game.ControllableUnit;
import net.simon987.server.game.GameObject;
import net.simon987.server.game.Location;
import net.simon987.server.logging.LogManager;

/**
 * Final exit portal located in the 'last' World of a Vault dimension
 */
public class VaultExitPortal extends Portal {

    public static final int ID = 9;

    @Override
    public BasicDBObject mongoSerialise() {
        BasicDBObject dbObject = new BasicDBObject();

        dbObject.put("i", getObjectId());
        dbObject.put("x", getX());
        dbObject.put("y", getY());
        dbObject.put("t", ID);
        dbObject.put("dstWorldX", getDst().worldX);
        dbObject.put("dstWorldY", getDst().worldY);
        dbObject.put("dstX", getDst().x);
        dbObject.put("dstY", getDst().y);
        dbObject.put("dstDimension", getDst().dimension);

        return dbObject;
    }

    @Override
    public boolean enter(GameObject object) {

        LogManager.LOGGER.info(((ControllableUnit) object).getParent().getUsername() + " Completed vault " +
                object.getWorld().getDimension());

        NpcPlugin.getStatsDbManager().saveVaultCompletion((ControllableUnit) object, object.getWorld().getDimension());


        return super.enter(object);
    }

    public static Portal deserialize(DBObject obj) {

        VaultExitPortal portal = new VaultExitPortal();

        portal.setDst(new Location(
                (int) obj.get("dstWorldX"),
                (int) obj.get("dstWorldY"),
                (String) obj.get("dstDimension"),
                (int) obj.get("dstX"),
                (int) obj.get("dstY")));
        portal.setX((int) obj.get("x"));
        portal.setY((int) obj.get("y"));

        return portal;
    }
}
