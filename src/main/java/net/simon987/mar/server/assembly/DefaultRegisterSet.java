package net.simon987.mar.server.assembly;

/**
 * RegisterSet with default values
 */
class DefaultRegisterSet extends RegisterSet {


    DefaultRegisterSet() {
        super();

        put(1, new Register("A"));
        put(2, new Register("B"));
        put(3, new Register("C"));
        put(4, new Register("D"));
        put(5, new Register("X"));
        put(6, new Register("Y"));
        put(7, new Register("SP"));
        put(8, new Register("BP"));
    }
}
