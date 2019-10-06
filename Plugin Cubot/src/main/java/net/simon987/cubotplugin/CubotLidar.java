package net.simon987.cubotplugin;

import net.simon987.server.assembly.HardwareModule;
import net.simon987.server.assembly.Memory;
import net.simon987.server.assembly.Status;
import net.simon987.server.game.objects.ControllableUnit;
import net.simon987.server.game.pathfinding.Node;
import net.simon987.server.game.pathfinding.Pathfinder;
import org.bson.Document;

import java.util.ArrayList;

public class CubotLidar extends HardwareModule {

    /**
     * Hardware ID (Should be unique)
     */
    public static final char HWID = 0x0003;

    public static final int DEFAULT_ADDRESS = 3;

    private static final int LIDAR_GET_POS = 1;
    private static final int LIDAR_GET_PATH = 2;
    private static final int LIDAR_GET_MAP = 3;
    private static final int LIDAR_GET_WORLD_POS = 4;
    private static final int LIDAR_GET_WORLD_SIZE = 5;

    public CubotLidar(ControllableUnit unit) {
        super(null, unit);
    }

    public CubotLidar(Document document, ControllableUnit unit) {
        super(document, unit);
    }

    @Override
    public char getId() {
        return HWID;
    }

    @Override
    public void handleInterrupt(Status status) {

        int a = getCpu().getRegisterSet().getRegister("A").getValue();

        switch (a) {
            case LIDAR_GET_POS:
                getCpu().getRegisterSet().getRegister("X").setValue(unit.getX());
                getCpu().getRegisterSet().getRegister("Y").setValue(unit.getY());
                break;
            case LIDAR_GET_PATH:
                if (unit.spendEnergy(50)) {
                    int c = getCpu().getRegisterSet().getRegister("C").getValue();
                    int b = getCpu().getRegisterSet().getRegister("B").getValue();
                    int destX = getCpu().getRegisterSet().getRegister("X").getValue();
                    int destY = getCpu().getRegisterSet().getRegister("Y").getValue();

                    //Get path
                    ArrayList<Node> nodes = Pathfinder.findPath(unit.getWorld(), unit.getX(), unit.getY(),
                            destX, destY, b);

                    //Write to memory
                    Memory mem = getCpu().getMemory();

                    int counter = c;

                    if (nodes != null) {

                        Node lastNode = null;

                        for (Node n : nodes) {
                            //Store the path as a sequence of directions
                            if (lastNode == null) {
                                lastNode = n;
                                continue;
                            }

                            if (n.x < lastNode.x) {
                                //West
                                mem.set(counter++, 3);
                            } else if (n.x > lastNode.x) {
                                //East
                                mem.set(counter++, 1);
                            } else if (n.y < lastNode.y) {
                                //North
                                mem.set(counter++, 0);
                            } else if (n.y > lastNode.y) {
                                //South
                                mem.set(counter++, 2);
                            }

                            lastNode = n;
                        }

                        //Indicate end of path with 0xAAAA
                        mem.set(counter, 0xAAAA);
                    } else {
                        //Indicate invalid path 0xFFFF
                        mem.set(counter, 0xFFFF);
                    }
                }

                break;

            case LIDAR_GET_MAP:
                if (unit.spendEnergy(10)) {
                    char[][] mapInfo = unit.getWorld().getMapInfo();

                    //Write map data to the location specified by register X
                    int i = getCpu().getRegisterSet().getRegister("X").getValue();
                    for (int y = 0; y < unit.getWorld().getWorldSize(); y++) {
                        for (int x = 0; x < unit.getWorld().getWorldSize(); x++) {
                            getCpu().getMemory().set(i++, mapInfo[x][y]);
                        }
                    }
                }
                break;

            case LIDAR_GET_WORLD_SIZE:
                getCpu().getRegisterSet().getRegister("X").setValue(unit.getWorld().getWorldSize());
                getCpu().getRegisterSet().getRegister("Y").setValue(unit.getWorld().getWorldSize());
                break;

            case LIDAR_GET_WORLD_POS:
                getCpu().getRegisterSet().getRegister("X").setValue(unit.getWorld().getX());
                getCpu().getRegisterSet().getRegister("Y").setValue(unit.getWorld().getY());
                break;
                
            default:
                	break;

        }
    }
}
