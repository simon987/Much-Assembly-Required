package net.simon987.npcplugin;

import net.simon987.server.GameServer;
import net.simon987.server.game.objects.Action;

public class ExecuteCpuTask extends NPCTask {

    private static final int MAX_EXEC_TIME = GameServer.INSTANCE.getConfig().getInt("npc_exec_time");

    @Override
    public boolean checkCompleted() {
        return false;
    }

    @Override
    public void tick(NonPlayerCharacter npc) {

        HackedNPC hNpc = (HackedNPC) npc;

        //Execute code
        int timeout = Math.min(hNpc.getEnergy(), MAX_EXEC_TIME);
        hNpc.getCpu().reset();
        int cost = hNpc.getCpu().execute(timeout);
        hNpc.spendEnergy(cost);

        if (hNpc.getCurrentAction() == Action.WALKING) {
            if (hNpc.spendEnergy(100)) {
                if (hNpc.incrementLocation()) {
                    //Couldn't walk
                    hNpc.setCurrentAction(Action.IDLE);
                }
            } else {
                hNpc.setCurrentAction(Action.IDLE);
            }
        }

    }
}
