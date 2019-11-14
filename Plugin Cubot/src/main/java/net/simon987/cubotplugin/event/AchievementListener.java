package net.simon987.cubotplugin.event;

import net.simon987.server.event.GameEvent;
import net.simon987.server.event.GameEventListener;
import net.simon987.server.game.objects.ControllableUnit;
import net.simon987.server.game.objects.GameObject;
import net.simon987.server.logging.LogManager;

public class AchievementListener implements GameEventListener {
    @Override
    public Class getListenedEventType() {
        return AchievementEvent.class;
    }

    @Override
    public void handle(GameEvent event) {
        AchievementEvent achievementEvent = (AchievementEvent) event;
        GameObject object = achievementEvent.getSource();
        if (object instanceof ControllableUnit) {
            final String achievement = achievementEvent.getAchievement();
            final ControllableUnit unit = (ControllableUnit) object;
            LogManager.LOGGER.info(unit.getParent().getUsername() + " Completed achievement: " + achievement);

            ((ControllableUnit) object).getParent().getStats().addToStringSet("achievements", achievement);
        }
    }
}
