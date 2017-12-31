package net.simon987.server.game;

import net.simon987.server.assembly.Memory;
import net.simon987.server.user.User;

import java.util.ArrayList;

public interface ControllableUnit {

    long getObjectId();

    void setKeyboardBuffer(ArrayList<Integer> kbBuffer);

    void setParent(User user);

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
}
