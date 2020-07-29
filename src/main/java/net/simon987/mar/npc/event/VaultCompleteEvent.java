package net.simon987.mar.npc.event;

import net.simon987.mar.npc.VaultExitPortal;
import net.simon987.mar.server.event.GameEvent;
import net.simon987.mar.server.game.objects.GameObject;

public class VaultCompleteEvent extends GameEvent {

    private final VaultExitPortal portal;

    public VaultCompleteEvent(GameObject object, VaultExitPortal portal) {

        //TODO: Add completion time?
        setSource(object);
        this.portal = portal;
    }

    @Override
    public GameObject getSource() {
        return (GameObject) super.getSource();
    }

    public VaultExitPortal getPortal() {
        return portal;
    }
}
