package net.simon987.server;

import net.simon987.server.logging.LogManager;
import net.simon987.server.webserver.SocketServer;

import java.io.File;
import java.net.InetSocketAddress;


public class Main {
    public static void main(String[] args){


        //TODO: Docs
        /*
         * - Intel 8086 p.14 design
         * - Memory: Storage organisation: From a storage pov, 8086 memory spaces are
         * organised as identical arrays of 16-bit words
         * - Microprocessor
         * - Instruction set
         * -
         */

        //---------------------------------

        //TODO: Random number generator
        //TODO: favicon
        //TODO: Email verification
        //TODO: Real account page
        //  Change/reset password
        //TODO: Object information Window (Hover, click ?)
        //TODO: Inventory indicator (Multiple items)
        //TODO: Software Interrupts (PIC): Interupt flag?
        /*
         * - INT/INTO instruction
         * - IRET instruction
         */
        //TODO: Clock hardware
        //TODO: Floppy drive hardware (and item?)
        //TODO: LEA instruction
        //TODO: XCHG instruction
        //TODO: SAL/SAR instruction
        //TODO: ROL/ROR/RCL/RCR instruction
        //TODO: LOOP/LOOPE/LOOPX/LOOPNE/LOOPNZ ?
        //TODO: World goto (form)
        //TODO: Save backup (keep X saves, Zip em)
        //TODO: Log backup (keep X backups, Zip em)
        //TODO: More tests
        //TODO: Handle client disconnects
        //TODO: Cache objects requests?
        //TODO: Ability to toggle debug stuff
        //TODO: Data segment, DB, DW, DD, DQ
        //TODO: Set client animation speed relative to TICK_LENGTH
        //TODO: Withdraw animation / action
        //TODO: Prevent World creation out of bounds, warp around universe
        //TODO: Multiple Biomass style (and yield, rarity)
        //TODO: Clean sprites
        //TODO: Auto-resize
        //TODO: Battery Hardware


        LogManager.initialize();
        ServerConfiguration config = new ServerConfiguration(new File("config.properties"));

        //Load
        GameServer.INSTANCE.getGameUniverse().load(new File("save.json"));

        SocketServer socketServer = new SocketServer(new InetSocketAddress(config.getString("webSocket_host"),
                config.getInt("webSocket_port")), config);

        GameServer.INSTANCE.setSocketServer(socketServer);

        (new Thread(socketServer)).start();
        (new Thread(GameServer.INSTANCE)).start();
    }
}
