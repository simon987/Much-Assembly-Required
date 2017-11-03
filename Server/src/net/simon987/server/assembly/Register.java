package net.simon987.server.assembly;

/**
 * Represents a register in a cpu
 */
public class Register {

    /**
     * Name of the register
     */
    private String name;

    /**
     * 16-bit value of the register
     */
    private char value = 0;

    /**
     * Create a new Register
     *
     * @param name Name of the register, always set in Upper case
     */
    public Register(String name) {
        this.name = name.toUpperCase();
    }

    public String getName() {
        return name;
    }

    public char getValue() {
        return value;
    }

    /**
     * Set value of the register.
     *
     * @param value value to set. It is casted to char
     */
    public void setValue(int value) {
        this.value = (char) value;
    }

    @Override
    public String toString() {

        return name + "=" + value;

    }
}
