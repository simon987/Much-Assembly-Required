package net.simon987.npcplugin;

import net.simon987.server.game.objects.ControllableUnit;
import net.simon987.server.game.objects.GameObject;
import net.simon987.server.game.world.Location;
import net.simon987.server.logging.LogManager;
import org.bson.Document;

/**
 * Final exit portal located in the 'last' World of a Vault dimension
 */
public class VaultExitPortal extends Portal {

    public VaultExitPortal() {

    }

    public VaultExitPortal(Document document) {
        super(document);

        setDestination(new Location(
                document.getInteger("dstWorldX"),
                document.getInteger("dstWorldY"),
                document.getString("dstDimension"),
                document.getInteger("dstX"),
                document.getInteger("dstY")));
        setX(document.getInteger("x"));
        setY(document.getInteger("y"));
    }

    @Override
    public boolean enter(GameObject object) {

        if (object instanceof ControllableUnit) {
            LogManager.LOGGER.info(((ControllableUnit) object).getParent().getUsername() + " Completed vault " +
                    object.getWorld().getDimension());

            ((ControllableUnit) object).getParent().getStats().addToStringSet("completedVaults", getWorld().getDimension());
        }

        return super.enter(object);
    }
}
