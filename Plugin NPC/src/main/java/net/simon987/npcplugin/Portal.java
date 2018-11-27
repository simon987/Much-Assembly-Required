package net.simon987.npcplugin;

import net.simon987.server.GameServer;
import net.simon987.server.game.objects.Enterable;
import net.simon987.server.game.objects.GameObject;
import net.simon987.server.game.objects.Structure;
import net.simon987.server.game.objects.Updatable;
import net.simon987.server.game.world.Location;
import net.simon987.server.game.world.World;
import org.bson.Document;


public class Portal extends Structure implements Enterable {

    /**
     * Destination location
     */
    private Location destination;

    public static final int MAP_INFO = 0x0801;

    public Portal() {
        super(1, 1);
    }

    public Portal(Document document) {
        super(document, 1, 1);

        destination = new Location(
                document.getInteger("dstWorldX"),
                document.getInteger("dstWorldY"),
                document.getString("dstDimension"),
                document.getInteger("dstX"),
                document.getInteger("dstY"));
        setX(document.getInteger("x"));
        setY(document.getInteger("y"));
    }

    /**
     * Called when an object attempts to walk directly into a Enterable object
     *
     * @param object The game object that attempted to enter
     * @return true if successful, false to block the object
     */
    @Override
    public boolean enter(GameObject object) {

        World world = GameServer.INSTANCE.getGameUniverse().getWorld(destination.worldX, destination.worldY, false, destination.dimension);

        if (object instanceof Updatable) {
            object.getWorld().decUpdatable();
            world.incUpdatable();
        }
        object.getWorld().removeObject(object);
        object.setWorld(world);
        world.addObject(object);

        object.setX(destination.x);
        object.setY(destination.y);

        return true;
    }

    @Override
    public char getMapInfo() {
        return MAP_INFO;
    }

    @Override
    public Document mongoSerialise() {
        Document dbObject = super.mongoSerialise();

        dbObject.put("dstWorldX", destination.worldX);
        dbObject.put("dstWorldY", destination.worldY);
        dbObject.put("dstX", destination.x);
        dbObject.put("dstY", destination.y);
        dbObject.put("dstDimension", destination.dimension);

        return dbObject;
    }

    public Location getDestination() {
        return destination;
    }

    public void setDestination(Location destination) {
        this.destination = destination;
    }
}
