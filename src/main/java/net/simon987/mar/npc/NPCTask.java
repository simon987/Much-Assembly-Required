package net.simon987.mar.npc;


public abstract class NPCTask {

    public abstract boolean checkCompleted();

    public abstract void tick(NonPlayerCharacter npc);

}
