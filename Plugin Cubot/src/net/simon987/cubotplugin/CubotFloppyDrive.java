package net.simon987.cubotplugin;

import net.simon987.server.GameServer;
import net.simon987.server.assembly.CpuHardware;
import net.simon987.server.assembly.Status;
import org.json.simple.JSONObject;

public class CubotFloppyDrive extends CpuHardware {

    /**
     * Hardware ID (Should be unique)
     */
    static final char HWID = 0x000B;

    public static final int DEFAULT_ADDRESS = 0x000B;

    private static final int POLL = 1;
    private static final int READ_SECTOR = 2;
    private static final int WRITE_SECTOR = 3;

    private Cubot cubot;
    private FloppyDisk floppyDisk;

    public CubotFloppyDrive(Cubot cubot) {
        this.cubot = cubot;

        this.floppyDisk = new FloppyDisk();//todo remove
    }

    @Override
    public void handleInterrupt(Status status) {
        int a = getCpu().getRegisterSet().getRegister("A").getValue();

        if (a == POLL) {

            if (floppyDisk != null) {
                getCpu().getRegisterSet().getRegister("B").setValue(0);
            } else {
                getCpu().getRegisterSet().getRegister("B").setValue(1);
            }

        } else if (a == READ_SECTOR) {

            if (floppyDisk == null) {
                getCpu().getRegisterSet().getRegister("B").setValue(0);
            } else {
                getCpu().getRegisterSet().getRegister("B").setValue(1);

                int x = getCpu().getRegisterSet().getRegister("X").getValue();
                int y = getCpu().getRegisterSet().getRegister("Y").getValue();

                floppyDisk.readSector(x, cubot.getParent().getCpu().getMemory(), y);
            }


        } else if (a == WRITE_SECTOR) {
            if (floppyDisk == null) {
                getCpu().getRegisterSet().getRegister("B").setValue(0);
            } else {
                getCpu().getRegisterSet().getRegister("B").setValue(1);

                int x = getCpu().getRegisterSet().getRegister("X").getValue();
                int y = getCpu().getRegisterSet().getRegister("Y").getValue();

                floppyDisk.writeSector(x, cubot.getParent().getCpu().getMemory(), y);
            }
        }

    }

    @Override
    public char getId() {
        return HWID;
    }

    @Override
    public JSONObject serialise() {
        JSONObject json = new JSONObject();
        json.put("hwid", (int) HWID);
        json.put("cubot", cubot.getObjectId());

        if (floppyDisk != null) {
            json.put("floppy", floppyDisk.serialise());
        }

        return json;
    }

    public static CubotFloppyDrive deserialize(JSONObject hwJSON) {

        CubotFloppyDrive drive = new CubotFloppyDrive((Cubot) GameServer.INSTANCE.getGameUniverse().getObject((int) (long) hwJSON.get("cubot")));

        if (hwJSON.containsKey("floppy")) {
            drive.floppyDisk = FloppyDisk.deserialise((JSONObject) hwJSON.get("floppy"));
        } else {
            drive.floppyDisk = new FloppyDisk();
        }

        return drive;
    }

    public FloppyDisk getFloppy() {
        return floppyDisk;
    }
}
