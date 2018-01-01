package net.simon987.server.assembly;

/**
 * Represents the state of the processor
 */
public class Status {

    /**
     * Set to true when the result of
     * an 8/16bit operation is too big to fit
     * in a 8/16bit register or memory
     */
    private boolean carryFlag = false;

    /**
     * Set to true when the result of an operation
     * is equal to zero
     */
    private boolean zeroFlag;

    /**
     * Set to true when the most significant
     * (highest) bit of the result is set
     */
    private boolean signFlag = false;

    /**
     * Set to true when the result of
     * a signed operation
     * * (+) + (+) = (-)
     * * (-) + (-) = (+)
     */
    private boolean overflowFlag = false;

    /**
     * CPU execution will stop when this flag
     * is set
     */
    private boolean breakFlag = false;

    /**
     * Set when an error occurred (division by 0, )
     */
    private boolean errorFlag = false;

    /**
     * Unset all flags
     */
    public void clear() {
        carryFlag = false;
        zeroFlag = false;
        signFlag = false;
        overflowFlag = false;
        breakFlag = false;
        errorFlag = false;
    }

    public String toString() {
        return "" + (signFlag ? 1 : 0) + ' ' +
                (zeroFlag ? 1 : 0) + ' ' +
                (carryFlag ? 1 : 0) + ' ' +
                (overflowFlag ? 1 : 0) + '\n';
    }

    /**
     * Set to true when the result of
     * an 8/16bit operation is too big to fit
     * in a 8/16bit register or memory
     */
    public boolean isCarryFlag() {
        return carryFlag;
    }

    public void setCarryFlag(boolean carryFlag) {
        this.carryFlag = carryFlag;
    }

    /**
     * Set to true when the result of an operation
     * is equal to zero
     */
    public boolean isZeroFlag() {
        return zeroFlag;
    }

    public void setZeroFlag(boolean zeroFlag) {
        this.zeroFlag = zeroFlag;
    }

    /**
     * Set to true when the most significant
     * (highest) bit of the result is set
     */
    public boolean isSignFlag() {
        return signFlag;
    }

    public void setSignFlag(boolean signFlag) {
        this.signFlag = signFlag;
    }

    /**
     * Set to true when the result of
     * a signed operation
     * * (+) + (+) = (-)
     * * (-) + (-) = (+)
     */
    public boolean isOverflowFlag() {
        return overflowFlag;
    }

    public void setOverflowFlag(boolean overflowFlag) {
        this.overflowFlag = overflowFlag;
    }

    /**
     * CPU execution will stop when this flag
     * is set
     */
    public boolean isBreakFlag() {
        return breakFlag;
    }

    public void setBreakFlag(boolean breakFlag) {
        this.breakFlag = breakFlag;
    }

    public boolean isErrorFlag() {
        return errorFlag;
    }

    public void setErrorFlag(boolean errorFlag) {
        this.errorFlag = errorFlag;
    }

    public char toByte() {
        char stat = 0; 
        stat = (char) (stat | ((signFlag ? 1 : 0) << 3));
        stat = (char) (stat | ((zeroFlag ? 1 : 0) << 2));
        stat = (char) (stat | ((carryFlag ? 1 : 0) << 1));
        stat = (char) (stat | (overflowFlag ? 1 : 0));
        return stat;
    }
    
    public void fromByte(char stat) {
        setSignFlag((stat & (1 << 3)) != 0);
        setZeroFlag((stat & (1 << 2)) != 0);
        setCarryFlag((stat & (1 << 1)) != 0);
        setOverflowFlag((stat & 1) != 0);    	
    }
}
