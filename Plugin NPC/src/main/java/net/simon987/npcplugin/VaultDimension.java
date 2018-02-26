package net.simon987.npcplugin;

import net.simon987.server.GameServer;
import net.simon987.server.game.Direction;
import net.simon987.server.game.World;

import java.util.ArrayList;

public class VaultDimension {

    /**
     * Name of the dimension
     */
    private String name;

    public VaultDimension(long vaultDoorId) {

        name = "v" + vaultDoorId + "-";

        VaultWorldGenerator generator = new VaultWorldGenerator();


        ArrayList<Direction> openings = new ArrayList<>();
        openings.add(Direction.WEST);
        openings.add(Direction.SOUTH);
        openings.add(Direction.EAST);
        openings.add(Direction.NORTH);

        World vWorld = generator.generateVaultWorld(0x7FFF, 0x7FFF, openings, name);

        GameServer.INSTANCE.getGameUniverse().addWorld(vWorld);
    }

}
