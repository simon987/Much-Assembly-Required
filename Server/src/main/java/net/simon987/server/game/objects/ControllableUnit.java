package net.simon987.server.game.objects;

import net.simon987.server.assembly.CPU;
import net.simon987.server.assembly.Memory;
import net.simon987.server.game.item.Item;
import net.simon987.server.game.world.World;
import net.simon987.server.user.User;
import org.bson.types.ObjectId;

import java.util.ArrayList;

public interface ControllableUnit {

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

    void setAction(Action listening);

    World getWorld();

    ArrayList<char[]> getConsoleMessagesBuffer();

    int getConsoleMode();

    CPU getCpu();

    void giveItem(Item item);
}
