package net.simon987.npcplugin.event;

import net.simon987.npcplugin.VaultExitPortal;
import net.simon987.server.event.GameEvent;
import net.simon987.server.game.objects.GameObject;

public class VaultCompleteEvent extends GameEvent {

    private VaultExitPortal portal;

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
