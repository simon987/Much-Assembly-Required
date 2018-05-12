package net.simon987.cubotplugin;


import net.simon987.server.assembly.Memory;
import net.simon987.server.io.MongoSerializable;
import org.bson.Document;

/**
 * Represents a floppy disk that is inside a floppy drive.
 * Floppies contains 80 tracks with 18 sectors per track.
 * That's 1440 sectors of 512 words. (total 1,474,560 bytes / 737,280 words / 1.44Mb)
 */
public class FloppyDisk implements MongoSerializable {

    /**
     * Contents of the disk
     */
    private Memory memory;

    /**
     * Current location of the read/write head.
     * Used to calculate seek time
     */
    private int rwHeadTrack = 0;


    public FloppyDisk() {
        this.memory = new Memory(512 * 1440);
    }

    public FloppyDisk(Document document) {
        this.rwHeadTrack = document.getInteger("rwHeadTrack");
        this.memory = new Memory((Document) document.get("memory"));
    }

    /**
     * Read 512 words from the specified sector to cpu memory at specified address
     *
     * @param sector     sector to read (0-1440)
     * @param cpuMemory  Cpu memory to write to
     * @param ramAddress address of the data to write in CPU memory
     * @return Whether or not the read operation was in the same track as the last r/w
     */
    public boolean readSector(int sector, Memory cpuMemory, int ramAddress) {

        if (sector <= 1440) {
            cpuMemory.write(ramAddress, memory.getWords(), sector * 512, 512);

            //Calculate seek time
            int deltaTrack = (sector / 80) - rwHeadTrack;

            if (deltaTrack != 0) {
                rwHeadTrack = (sector / 80);
                return false;
            } else {
                return true;
            }
        }
        return false;

    }

    /**
     * Write 512 words to the specified sector from cpu memory at the specified address
     *
     * @param sector     sector to write (0-1440)
     * @param cpuMemory  Cpu memory to read from
     * @param ramAddress address of the data to read in CPU memory
     * @return Whether or not the read operation was in the same track as the last r/w
     */
    public boolean writeSector(int sector, Memory cpuMemory, int ramAddress) {

        if (sector <= 1440) {
            memory.write(sector * 512, cpuMemory.getWords(), ramAddress, 512);

            //Calculate seek time
            int deltaTrack = (sector / 80) - rwHeadTrack;

            if (deltaTrack != 0) {
                rwHeadTrack = (sector / 80);
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public Document mongoSerialise() {
        Document dbObject = new Document();

        dbObject.put("rwHeadTrack", rwHeadTrack);
        dbObject.put("memory", memory.mongoSerialise());

        return dbObject;
    }

    public Memory getMemory() {
        return memory;
    }
}
