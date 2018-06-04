package net.simon987.cubotplugin.event;

import net.simon987.cubotplugin.Cubot;
import net.simon987.cubotplugin.CubotStatus;
import net.simon987.server.GameServer;
import net.simon987.server.ServerConfiguration;
import net.simon987.server.assembly.Assembler;
import net.simon987.server.assembly.AssemblyResult;
import net.simon987.server.assembly.CPU;
import net.simon987.server.assembly.exception.CancelledException;
import net.simon987.server.event.GameEvent;
import net.simon987.server.event.GameEventListener;
import net.simon987.server.event.UserCreationEvent;
import net.simon987.server.logging.LogManager;
import net.simon987.server.user.User;
import org.bson.types.ObjectId;

import java.awt.*;
import java.util.Random;

public class UserCreationListener implements GameEventListener {
    @Override
    public Class getListenedEventType() {
        return UserCreationEvent.class;
    }

    @Override
    public void handle(GameEvent event) {

        Random random = new Random();

        User user = (User) event.getSource();
        Cubot cubot = new Cubot();
        cubot.addStatus(CubotStatus.FACTORY_NEW);
        cubot.setObjectId(new ObjectId());
        ServerConfiguration config = GameServer.INSTANCE.getConfig();

        Point point = null;
        while (point == null || cubot.getWorld() == null) {
            int spawnX = config.getInt("new_user_worldX") + random.nextInt(5);
            int spawnY = config.getInt("new_user_worldY") + random.nextInt(5);
            String dimension = config.getString("new_user_dimension");
            cubot.setWorld(GameServer.INSTANCE.getGameUniverse().getWorld(spawnX, spawnY, true, dimension));

            point = cubot.getWorld().getRandomPassableTile();
        }

        cubot.setX(point.x);
        cubot.setY(point.y);
        cubot.getWorld().addObject(cubot);
        cubot.getWorld().incUpdatable();

        cubot.setEnergy(config.getInt("battery_max_energy"));
        cubot.setMaxEnergy(config.getInt("battery_max_energy"));

        cubot.setHp(config.getInt("cubot_max_hp"));
        cubot.setMaxHp(config.getInt("cubot_max_hp"));
        cubot.setMaxShield(config.getInt("cubot_max_shield"));

        cubot.setParent(user);
        user.setControlledUnit(cubot);

        //Create CPU
        try {
            cubot.setCpu(new CPU(GameServer.INSTANCE.getConfig(), cubot));
            cubot.getCpu().setHardwareHost(cubot);
            user.setUserCode(config.getString("new_user_code"));

            //Compile user code
            AssemblyResult ar = new Assembler(cubot.getCpu().getInstructionSet(), cubot.getCpu().getRegisterSet(),
                    GameServer.INSTANCE.getConfig()).parse(user.getUserCode());

            cubot.getCpu().getMemory().clear();

            //Write assembled code to mem
            char[] assembledCode = ar.getWords();

            cubot.getCpu().getMemory().write((char) ar.origin, assembledCode, 0, assembledCode.length);
            cubot.getCpu().setCodeSectionOffset(ar.getCodeSectionOffset());
        } catch (CancelledException e) {
            e.printStackTrace();
        }

        LogManager.LOGGER.fine("(Plugin) Handled User creation event (Cubot Plugin)");
    }
}
