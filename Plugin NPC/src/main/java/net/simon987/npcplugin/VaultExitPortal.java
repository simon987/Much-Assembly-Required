package net.simon987.npcplugin;

import net.simon987.server.game.objects.GameObject;
import net.simon987.server.game.world.Location;
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

        //TODO: Trigger vault complete event instead
        return super.enter(object);
    }
}
