package net.simon987.server.assembly;

/**
 * RegisterSet with default values
 */
class DefaultRegisterSet extends RegisterSet {


    DefaultRegisterSet() {
        super();

        addRegister(1, new Register("A"));
        addRegister(2, new Register("B"));
        addRegister(3, new Register("C"));
        addRegister(4, new Register("D"));
        addRegister(5, new Register("X"));
        addRegister(6, new Register("Y"));
        addRegister(7, new Register("SP"));
        addRegister(8, new Register("BP"));

    }
}
