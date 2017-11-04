package net.simon987.server.game;

import net.simon987.server.user.User;

import java.util.ArrayList;

public interface ControllableUnit {

    int getObjectId();

    void setKeyboardBuffer(ArrayList<Integer> kbBuffer);

    void setParent(User user);

    ArrayList<Integer> getKeyboardBuffer();

}
