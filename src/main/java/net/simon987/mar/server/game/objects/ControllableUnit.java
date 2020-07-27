package net.simon987.mar.server.game.objects;

import net.simon987.mar.server.assembly.CPU;
import net.simon987.mar.server.assembly.Memory;
import net.simon987.mar.server.game.item.Item;
import net.simon987.mar.server.game.world.World;
import net.simon987.mar.server.user.User;
import org.bson.types.ObjectId;

import java.awt.*;
import java.util.ArrayList;

public interface ControllableUnit extends MessageReceiver, Rechargeable, Attackable, HardwareHost {

    ObjectId getObjectId();

    void setKeyboardBuffer(ArrayList<Integer> kbBuffer);

    void setParent(User user);

    User getParent();

    ArrayList<Integer> getKeyboardBuffer();

    Memory getFloppyData();

    boolean spendEnergy(int energy);

    int getEnergy();

    int getX();

    int getY();

    void setAction(Action action);

    void setCurrentAction(Action action);

    Action getCurrentAction();

    World getWorld();

    ArrayList<char[]> getConsoleMessagesBuffer();

    int getConsoleMode();

    CPU getCpu();

    void giveItem(Item item);

    Point getFrontTile();

    void setDirection(Direction direction);
}

