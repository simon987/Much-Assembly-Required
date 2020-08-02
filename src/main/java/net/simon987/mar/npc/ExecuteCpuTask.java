package net.simon987.mar.npc;

import net.simon987.mar.server.GameServer;
import net.simon987.mar.server.game.objects.Action;

public class ExecuteCpuTask extends NPCTask {

    private static final int MAX_EXEC_INSTRUCTIONS = GameServer.INSTANCE.getConfig().getInt("npc_exec_instructions");

    @Override
    public boolean checkCompleted() {
        return false;
    }

    @Override
    public void tick(NonPlayerCharacter npc) {

        HackedNPC hNpc = (HackedNPC) npc;

        //Execute code
        int allocation = Math.min(hNpc.getEnergy() * 10000, MAX_EXEC_INSTRUCTIONS);
        hNpc.getCpu().reset();
        hNpc.getCpu().setInstructionAlloction(allocation);
        int cost = hNpc.getCpu().execute();
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
