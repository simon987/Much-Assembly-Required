package net.simon987.mar.cubot.event;

import net.simon987.mar.cubot.Cubot;
import net.simon987.mar.cubot.CubotStatus;
import net.simon987.mar.server.GameServer;
import net.simon987.mar.server.IServerConfiguration;
import net.simon987.mar.server.assembly.Assembler;
import net.simon987.mar.server.assembly.AssemblyResult;
import net.simon987.mar.server.assembly.CPU;
import net.simon987.mar.server.assembly.exception.CancelledException;
import net.simon987.mar.server.event.CpuInitialisationEvent;
import net.simon987.mar.server.event.GameEvent;
import net.simon987.mar.server.event.GameEventListener;
import net.simon987.mar.server.event.UserCreationEvent;
import net.simon987.mar.server.logging.LogManager;
import net.simon987.mar.server.user.User;
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
        IServerConfiguration config = GameServer.INSTANCE.getConfig();

        Point point = null;
        while (point == null || cubot.getWorld() == null) {
            int spawnX = config.getInt("new_user_worldX") + random.nextInt(5);
            int spawnY = config.getInt("new_user_worldY") + random.nextInt(5);
            String dimension = config.getString("new_user_dimension");
            cubot.setWorld(GameServer.INSTANCE.getUniverse().getWorld(spawnX, spawnY, true, dimension));

            point = cubot.getWorld().getRandomPassableTile();
        }

        cubot.setX(point.x);
        cubot.setY(point.y);
        cubot.getWorld().addObject(cubot);
        cubot.getWorld().incUpdatable();

        cubot.setParent(user);
        user.setControlledUnit(cubot);

        //Create CPU
        try {
            CPU cpu = new CPU(config);
            cubot.setCpu(cpu);
            cubot.getCpu().setHardwareHost(cubot);
            user.setUserCode(config.getString("new_user_code"));

            GameEvent initEvent = new CpuInitialisationEvent(cpu, cubot);
            GameServer.INSTANCE.getEventDispatcher().dispatch(initEvent);
            if (initEvent.isCancelled()) {
                throw new CancelledException();
            }

            //Compile user code
            AssemblyResult ar = new Assembler(cpu.getInstructionSet(), cpu.getRegisterSet(),
                    config).parse(user.getUserCode());

            cubot.getCpu().getMemory().clear();

            //Write assembled code to mem
            char[] assembledCode = ar.getWords();

            cubot.getCpu().getMemory().write((char) ar.origin, assembledCode, 0, assembledCode.length);
            cubot.getCpu().setCodeSectionOffset(ar.getCodeSectionOffset());
        } catch (CancelledException e) {
            e.printStackTrace();
        }

        cubot.setHp(config.getInt("cubot_max_hp"));
        cubot.setMaxHp(config.getInt("cubot_max_hp"));
        cubot.setMaxShield(config.getInt("cubot_max_shield"));

        LogManager.LOGGER.fine("Handled User creation event");
    }
}
