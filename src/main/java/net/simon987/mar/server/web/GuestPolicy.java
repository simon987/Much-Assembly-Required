package net.simon987.mar.server.web;

public enum GuestPolicy {
    /**
     * Allow guests, must login to have Cubot
     */
    ALLOW,
    /**
     * Block guests completely
     */
    BLOCK,
}
