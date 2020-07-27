package net.simon987.mar.npcplugin;


public abstract class NPCTask {

    public abstract boolean checkCompleted();

    public abstract void tick(NonPlayerCharacter npc);

}
