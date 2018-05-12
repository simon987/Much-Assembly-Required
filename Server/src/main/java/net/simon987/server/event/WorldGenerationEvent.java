package net.simon987.server.event;

import net.simon987.server.game.world.World;

public class WorldGenerationEvent extends GameEvent {

    public WorldGenerationEvent(World world) {
        setSource(world);
    }

    public World getWorld() {
        return (World) getSource();
    }

}
