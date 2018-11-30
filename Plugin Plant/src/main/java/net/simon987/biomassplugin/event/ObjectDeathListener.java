package net.simon987.biomassplugin.event;

import net.simon987.biomassplugin.BiomassBlob;
import net.simon987.server.ServerConfiguration;
import net.simon987.server.event.GameEvent;
import net.simon987.server.event.GameEventListener;
import net.simon987.server.event.ObjectDeathEvent;
import net.simon987.server.game.objects.GameObject;
import net.simon987.server.game.world.World;
import net.simon987.server.logging.LogManager;
import org.bson.types.ObjectId;

/**
 * Handles ObjectDeathEvent events
 */
public class ObjectDeathListener implements GameEventListener {

    private int biomassDropCount;

    public ObjectDeathListener(ServerConfiguration config) {
        biomassDropCount = config.getInt("harvester_biomass_drop_count");
    }

    @Override
    public Class getListenedEventType() {
        return ObjectDeathEvent.class;
    }

    @Override
    public void handle(GameEvent event) {

        if (event.getSource().getClass().getCanonicalName().equals("net.simon987.npcplugin.HarvesterNPC")) {
            //An HarvesterNPC ObjectDeathEvent is received
            GameObject dyingHarvesterNPC = (GameObject)event.getSource();

            //Don't spawn biomass on World border
            if (dyingHarvesterNPC.getX() != 0 && dyingHarvesterNPC.getX() != dyingHarvesterNPC.getWorld().getWorldSize() - 1 &&
                    dyingHarvesterNPC.getY() != 0 && dyingHarvesterNPC.getY() != dyingHarvesterNPC.getWorld().getWorldSize() - 1) {
                //Create a new biomass
                BiomassBlob newBiomassBlob = createBiomassBlobAt(
                        dyingHarvesterNPC.getX(), dyingHarvesterNPC.getY(), dyingHarvesterNPC.getWorld());
                //Add it to the world game objects
                dyingHarvesterNPC.getWorld().addObject(newBiomassBlob);
                LogManager.LOGGER.fine("Spawned biomass at (" + newBiomassBlob.getX() +
                        ", " + newBiomassBlob.getY() + ")");
            }
        }
    }

    /**
     * Create and return a biomass at the given x, y coordinates and in the world
     * @param x x coord of biomass location
     * @param y y coord of biomass location
     * @param world world in which the biomass will be created in
     * @return the new BiomassBlob created
     */
    private BiomassBlob createBiomassBlobAt(int x, int y, World world) {

        BiomassBlob biomassBlob = new BiomassBlob();
        biomassBlob.setObjectId(new ObjectId());
        biomassBlob.setBiomassCount(biomassDropCount);
        biomassBlob.setX(x);
        biomassBlob.setY(y);
        biomassBlob.setWorld(world);

        return biomassBlob;
    }
}
