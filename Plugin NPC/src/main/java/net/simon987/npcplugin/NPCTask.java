package net.simon987.npcplugin;


public abstract class NPCTask {

    public abstract boolean checkCompleted();

    public abstract void tick(NonPlayerCharacter npc);

}
