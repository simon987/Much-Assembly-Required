package net.simon987.cubotplugin;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import net.simon987.server.GameServer;
import net.simon987.server.assembly.CpuHardware;
import net.simon987.server.assembly.Status;

public class CubotFloppyDrive extends CpuHardware {

    /**
     * Hardware ID (Should be unique)
     */
    static final char HWID = 0x000B;

    public static final int DEFAULT_ADDRESS = 0x000B;

    private static final int FLOPPY_POLL = 1;
    private static final int FLOPPY_READ_SECTOR = 2;
    private static final int FLOPPY_WRITE_SECTOR = 3;

    private Cubot cubot;
    private FloppyDisk floppyDisk;

    public CubotFloppyDrive(Cubot cubot) {
        this.cubot = cubot;

        floppyDisk = new FloppyDisk();
    }

    @Override
    public void handleInterrupt(Status status) {
        int a = getCpu().getRegisterSet().getRegister("A").getValue();

        if (a == FLOPPY_POLL) {

            if (floppyDisk != null) {
                getCpu().getRegisterSet().getRegister("B").setValue(0);
            } else {
                getCpu().getRegisterSet().getRegister("B").setValue(1);
            }

        } else if (a == FLOPPY_READ_SECTOR) {

            if (floppyDisk == null) {
                getCpu().getRegisterSet().getRegister("B").setValue(0);
            } else {
                if (cubot.spendEnergy(1)) {
                    getCpu().getRegisterSet().getRegister("B").setValue(1);

                    int x = getCpu().getRegisterSet().getRegister("X").getValue();
                    int y = getCpu().getRegisterSet().getRegister("Y").getValue();

                    floppyDisk.readSector(x, cubot.getParent().getCpu().getMemory(), y);
                }
            }


        } else if (a == FLOPPY_WRITE_SECTOR) {
            if (floppyDisk == null) {
                getCpu().getRegisterSet().getRegister("B").setValue(0);
            } else {
                if (cubot.spendEnergy(1)) {
                    getCpu().getRegisterSet().getRegister("B").setValue(1);

                    int x = getCpu().getRegisterSet().getRegister("X").getValue();
                    int y = getCpu().getRegisterSet().getRegister("Y").getValue();

                    floppyDisk.writeSector(x, cubot.getParent().getCpu().getMemory(), y);
                }
            }
        }

    }

    @Override
    public char getId() {
        return HWID;
    }

    @Override
    public BasicDBObject mongoSerialise() {

        BasicDBObject dbObject = new BasicDBObject();

        dbObject.put("hwid", (int) HWID);
        dbObject.put("cubot", cubot.getObjectId());

        if (floppyDisk != null) {
            dbObject.put("floppy", floppyDisk.mongoSerialise());
        }

        return dbObject;
    }

    public static CubotFloppyDrive deserialize(DBObject obj) {

        CubotFloppyDrive drive = new CubotFloppyDrive((Cubot) GameServer.INSTANCE.getGameUniverse().getObject((long) obj.get("cubot")));

        if (obj.containsField("floppy")) {
            drive.floppyDisk = FloppyDisk.deserialise((DBObject) obj.get("floppy"));
        }

        return drive;
    }

    public FloppyDisk getFloppy() {
        return floppyDisk;
    }
}
