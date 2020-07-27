package net.simon987.mar.construction;

import net.simon987.mar.server.game.item.ItemIron;
import org.bson.Document;

public class ObstacleBlueprint extends BluePrint {

    public ObstacleBlueprint() {
        super();

        this.requiredItems.put(ItemIron.ID, 2); //TODO: load from config?
        this.targetObject = Obstacle.class;
    }

    public ObstacleBlueprint(Document document) {
        this.requiredItems.put(ItemIron.ID, 2); //TODO: load from config?
        this.targetObject = Obstacle.class;
    }
}
