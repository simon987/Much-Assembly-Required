package net.simon987.cubotplugin;

import net.simon987.server.GameServer;
import net.simon987.server.assembly.CpuHardware;
import net.simon987.server.assembly.Status;
import net.simon987.server.game.World;
import net.simon987.server.game.pathfinding.Node;
import net.simon987.server.game.pathfinding.Pathfinder;
import net.simon987.server.io.JSONSerialisable;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class CubotRadar extends CpuHardware implements JSONSerialisable {

    /**
     * Hardware ID (Should be unique)
     */
    public static final int HWID = 0x0003;

    public static final int DEFAULT_ADDRESS = 3;

    private Cubot cubot;

    private static final int GET_POS = 1;
    private static final int GET_PATH = 2;
    private static final int GET_MAP = 3;

    public CubotRadar(Cubot cubot) {
        this.cubot = cubot;
    }

    @Override
    public void handleInterrupt(Status status) {

        int a = getCpu().getRegisterSet().getRegister("A").getValue();

        switch (a){
            case GET_POS:
                getCpu().getRegisterSet().getRegister("X").setValue(cubot.getX());
                getCpu().getRegisterSet().getRegister("Y").setValue(cubot.getY());
                break;
            case GET_PATH:
                int b = getCpu().getRegisterSet().getRegister("B").getValue();
                int destX = getCpu().getRegisterSet().getRegister("X").getValue();
                int destY = getCpu().getRegisterSet().getRegister("Y").getValue();

                //Get path
                ArrayList<Node> nodes = Pathfinder.findPath(cubot.getWorld(), cubot.getX(), cubot.getY(),
                        destX, destY, b);

//                System.out.println(nodes.size() + " nodes");

                //Write to memory
                byte[] mem = getCpu().getMemory().getBytes();

                int counter = 0; //todo get memory address from config/constant

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
                            mem[counter++] = 0;
                            mem[counter++] = 3;
                        } else if (n.x > lastNode.x) {
                            //East
                            mem[counter++] = 0;
                            mem[counter++] = 1;
                        } else if (n.y < lastNode.y) {
                            //North
                            mem[counter++] = 0;
                            mem[counter++] = 0;
                        } else if (n.y > lastNode.y) {
                            //South
                            mem[counter++] = 0;
                            mem[counter++] = 2;
                        }

                        lastNode = n;
                    }

                    //Indicate end of path with 0xAAAA
                    mem[counter++] = -86;
                    mem[counter] = -86;
                } else {
                    //Indicate invalid path 0xFFFF
                    mem[counter++] = -1;
                    mem[counter] = -1;
                }

                System.out.println("DEBUG: path to" + destX + "," + destY);
                break;

            case GET_MAP:
                char[][] mapInfo = cubot.getWorld().getMapInfo();

                int i = 0;
                for (int y = 0; y < World.WORLD_SIZE; y++) {
                    for (int x = 0; x < World.WORLD_SIZE; x++) {
                        getCpu().getMemory().set(i++, mapInfo[x][y]);
                    }
                }
        }


    }

    @Override
    public JSONObject serialise() {

        JSONObject json = new JSONObject();
        json.put("hwid", HWID);
        json.put("cubot", cubot.getObjectId());

        return json;
    }

    public static CubotRadar deserialize(JSONObject hwJSON){
        return new CubotRadar((Cubot) GameServer.INSTANCE.getGameUniverse().getObject((int)(long)hwJSON.get("cubot")));
    }
}
