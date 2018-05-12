package net.simon987.cubotplugin;

import net.simon987.server.assembly.Status;
import org.bson.Document;

public class CubotFloppyDrive extends CubotHardware {

    /**
     * Hardware ID (Should be unique)
     */
    static final char HWID = 0x000B;

    public static final int DEFAULT_ADDRESS = 0x000B;

    private static final int FLOPPY_POLL = 1;
    private static final int FLOPPY_READ_SECTOR = 2;
    private static final int FLOPPY_WRITE_SECTOR = 3;

    private FloppyDisk floppyDisk;

    public CubotFloppyDrive(Cubot cubot) {
        super(cubot);

        floppyDisk = new FloppyDisk();
    }

    public CubotFloppyDrive(Document document) {
        super(document);

        if (document.containsKey("floppy")) {
            floppyDisk = new FloppyDisk((Document) document.get("floppy"));
        } else {
            floppyDisk = new FloppyDisk();
        }
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


    public FloppyDisk getFloppy() {
        return floppyDisk;
    }
}
