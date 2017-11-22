package net.simon987.server.assembly;

/**
 * A Target is a location that can be read and written to during
 * the execution of an instruction.
 * <p>
 * For example: MOV dstTARGET, srcTARGET
 * <p>
 * A target is usually Memory or Register
 */
public interface Target {

    /**
     * Get a value from a Target at an address.
     *
     * @param address Address of the value. Can refer to a memory address or the index
     *                of a register
     * @return value at specified address
     */
    int get(int address);

    /**
     * Set a value at an address
     *
     * @param address address of the value to change
     * @param value   value to set
     */
    void set(int address, int value);

}
