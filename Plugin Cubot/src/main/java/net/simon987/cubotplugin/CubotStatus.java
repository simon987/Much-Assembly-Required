package net.simon987.cubotplugin;

/**
 * Status of a Cubot (Special buff or debuff)
 */
public enum CubotStatus {

    DEFAULT(0),
    RADIATED(1),
    DAMAGED(2);

    public char val;

    CubotStatus(int val) {
        this.val = (char) val;
    }

}
