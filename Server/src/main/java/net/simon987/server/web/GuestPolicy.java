package net.simon987.server.web;

public enum GuestPolicy {
    /**
     * Allow guests, must login to have Cubot
     */
    ALLOW,
    /**
     * Block guests completely
     */
    BLOCK,
    /**
     * Allow guests, can have Cubot, but it is not saved in database
     */
    TEMPORARY
}
