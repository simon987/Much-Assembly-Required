package net.simon987.server.game;

import java.util.ArrayList;

public interface ControllableUnit {

    int getObjectId();

    void setKeyboardBuffer(ArrayList<Integer> kbBuffer);

    ArrayList<Integer> getKeyboardBuffer();

}
