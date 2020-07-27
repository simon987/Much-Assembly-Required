package net.simon987.mar.server.event;

import net.simon987.mar.server.game.world.World;

public class WorldUpdateEvent extends GameEvent {

    private final World world;

    public WorldUpdateEvent(World world) {
        this.world = world;
    }

    public World getWorld() {
        return world;
    }
}
