package net.simon987.cubotplugin.event;

import net.simon987.server.event.GameEvent;
import net.simon987.server.game.objects.GameObject;

public class AchievementEvent extends GameEvent {

    private final String achievement;

    public AchievementEvent(GameObject source, String achievement) {
        this.achievement = achievement;
        setSource(source);
    }

    public String getAchievement() {
        return achievement;
    }

    @Override
    public GameObject getSource() {
        return (GameObject) super.getSource();
    }
}
