package net.simon987.server.assembly;

/**
 * Section of a user-created program.
 * The execution will start at the beginning of the code
 * segment.
 */
public enum Section {

    /**
     * Code section of the program. Contains executable code
     */
    TEXT,

    /**
     * Data section of the program. Contains initialised data
     */
    DATA

}
