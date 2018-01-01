package net.simon987.server.game;

/**
 * Objects that can be attacked or healed
 */
public interface Attackable {

    void setHealRate(int hp);

    int getHp();
    void setHp(int hp);

    int getMaxHp();
    void setMaxHp(int hp);

    void heal(int amount);
    void damage(int amount);

}
