package net.simon987.server.game.objects;

public interface Rechargeable {

    int getEnergy();

    void setEnergy(int energy);

    boolean spendEnergy(int spent);

    void storeEnergy(int amount);

    void setMaxEnergy(int maxEnergy);

    int getMaxEnergy();
}
