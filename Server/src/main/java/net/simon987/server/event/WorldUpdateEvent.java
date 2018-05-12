package net.simon987.server.event;

import net.simon987.server.game.world.World;

public class WorldUpdateEvent extends GameEvent {

    private World world;

    public WorldUpdateEvent(World world) {
        this.world = world;
    }

    public World getWorld() {
        return world;
    }
}
