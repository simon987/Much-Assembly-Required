package net.simon987.server.webserver;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import net.simon987.server.GameServer;
import net.simon987.server.game.ControllableUnit;
import net.simon987.server.game.GameObject;
import net.simon987.server.game.Updatable;
import net.simon987.server.game.World;
import net.simon987.server.logging.LogManager;
import net.simon987.server.user.User;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class DebugHandler implements MessageHandler {
    @Override
    public void handle(OnlineUser user, JSONObject json) {

        if (json.get("t").equals("debug") && user.isModerator()) {

            LogManager.LOGGER.fine("(WS) Debug command from " + user.getUser().getUsername());

            String command = (String) json.get("command");

            if (json.containsKey("command")) {

                JSONObject response = new JSONObject();
                response.put("t", "debug");

                switch (command) {

                    case "setTileAt":

                        response.put("message", setTileAt(
                                (int) (long) json.get("x"),
                                (int) (long) json.get("y"),
                                (int) (long) json.get("newTile"),
                                (int) (long) json.get("worldX"),
                                (int) (long) json.get("worldY"),
                                (String) json.get("dimension")));

                        break;

                    case "createWorld":
                        response.put("message", createWorld(
                                (int) (long) json.get("worldX"),
                                (int) (long) json.get("worldY"),
                                (String) json.get("dimension")));
                        break;


                    case "killAll":
                        response.put("message", killAll(
                                (int) (long) json.get("x"),
                                (int) (long) json.get("y"),
                                (int) (long) json.get("worldX"),
                                (int) (long) json.get("worldY"),
                                (String) json.get("dimension")));
                        break;

                    case "objInfo":
                        response.put("message", objInfo(
                                (int) (long) json.get("x"),
                                (int) (long) json.get("y"),
                                (int) (long) json.get("worldX"),
                                (int) (long) json.get("worldY"),
                                (String) json.get("dimension")));

                        break;

                    case "userInfo":
                        response.put("message", userInfo((String) json.get("username")));
                        break;

                    case "moveObj":
                        response.put("message", moveObj(
                                (long) json.get("objectId"),
                                (int) (long) json.get("x"),
                                (int) (long) json.get("y")));
                        break;

                    case "spawnObj":
                        response.put("message", spawnObj(
                                (int) (long) json.get("worldX"),
                                (int) (long) json.get("worldY"),
                                (String) json.get("data"),
                                (String) json.get("dimension")));
                        break;
                    case "tpObj":
                        response.put("message", moveObj(
                                (long) json.get("objectId"),
                                (int) (long) json.get("x"),
                                (int) (long) json.get("y"),
                                (int) (long) json.get("worldX"),
                                (int) (long) json.get("worldY"),
                                (String) json.get("dimension")));
                        break;

                    default:
                        LogManager.LOGGER.severe("Unknown command: " + command);
                        response.put("message", "Unknown command " + command);
                }

                user.getWebSocket().send(response.toJSONString());

            }

        }
    }

    /**
     * Create a world at coordinates
     */
    private String createWorld(int worldX, int worldY, String dimension) {

        World world = GameServer.INSTANCE.getGameUniverse().getWorld(worldX, worldY, true, dimension);

        if (world != null) {

            return "Success";

        } else {
            return "Couldn't create world";
        }

    }

    /**
     * Change the tile at coordinate
     */
    private String setTileAt(int x, int y, int newTile, int worldX, int worldY, String dimension) {
        World world = GameServer.INSTANCE.getGameUniverse().getWorld(worldX, worldY, false, dimension);

        if (world != null) {

            world.getTileMap().setTileAt(newTile, x, y);
            return "Success";

        } else {
            return "Error: World is uncharted";
        }
    }

    private String spawnObj(int worldX, int worldY, String data, String dimension) {

        World world = GameServer.INSTANCE.getGameUniverse().getWorld(worldX, worldY, false, dimension);

        try {
            DBObject dbObj = (DBObject) JSON.parse(data);
            dbObj.put("i", GameServer.INSTANCE.getGameUniverse().getNextObjectId());

            GameObject object = GameObject.deserialize(dbObj);

            if (object != null) {
                world.addObject(object);
                object.setWorld(world);

                object.initialize();

                return "Created object " + object.getObjectId();

            } else {
                return "Couldn't deserialise the object";
            }

        } catch (Exception e) {

            String message = e.getMessage();
            message += "\n " + Arrays.toString(e.getStackTrace()).replaceAll(", ", "\n");

            return message;
        }


    }

    private String killAll(int x, int y, int worldX, int worldY, String dimension) {

        World world = GameServer.INSTANCE.getGameUniverse().getWorld(worldX, worldY, false, dimension);

        try {

            ArrayList<GameObject> objs = world.getGameObjectsAt(x, y);

            for (GameObject o : objs) {
                o.setDead(true);
            }

            return "Killed " + objs.size() + " objects";

        } catch (Exception e) {

            return Arrays.toString(e.getStackTrace()).replaceAll(", ", "\n");
        }
    }

    private String objInfo(int x, int y, int worldX, int worldY, String dimension) {

        World world = GameServer.INSTANCE.getGameUniverse().getWorld(worldX, worldY, false, dimension);
        try {

            Collection<GameObject> objs = world.getGameObjects();
            String str = objs.size() + "\n";

            for (GameObject obj : objs) {

                if (obj.isAt(x, y) || (obj.getX() == x && obj.getY() == y)) {
                    str += "Mongo:" + obj.mongoSerialise() + "\n";
                    str += "JSON :" + obj.serialise().toJSONString() + "\n\n";
                }
            }

            return str;

        } catch (Exception e) {

            String message = e.getMessage();
            message += "\n " + Arrays.toString(e.getStackTrace()).replaceAll(", ", "\n");

            return message;
        }
    }

    private String moveObj(long objectId, int x, int y) {

        GameObject object = GameServer.INSTANCE.getGameUniverse().getObject(objectId);

        if (object != null) {

            object.setX(x);
            object.setY(y);

            return "Success";
        } else {
            return "Object not found: " + objectId;
        }
    }

    private String moveObj(long objectId, int x, int y, int worldX, int worldY, String dimension) {

        GameObject object = GameServer.INSTANCE.getGameUniverse().getObject(objectId);
        World world = GameServer.INSTANCE.getGameUniverse().getWorld(worldX, worldY, false, dimension);

        if (object != null) {

            if (world != null) {

                if (object instanceof Updatable) {
                    object.getWorld().decUpdatable();
                    world.incUpdatable();

                }

                object.getWorld().removeObject(object);
                world.addObject(object);
                object.setWorld(world);

                object.setX(x);
                object.setY(y);

                return "Success";

            } else {
                return "World not found: " + World.idFromCoordinates(worldX, worldY, dimension);
            }
        } else {
            return "Object not found: " + objectId;
        }


    }

    private String userInfo(String username) {

        User user = GameServer.INSTANCE.getGameUniverse().getUser(username);

        if (user != null) {

            String str = "Showing information for user " + username + "\n";

            str += "isGuest: " + user.isGuest() + "\n";

            ControllableUnit unit = user.getControlledUnit();
            str += "ControlledUnit: " + unit.getObjectId() + " at (" + unit.getX() + ", " + unit.getY() + ")\n";

            str += "CPU:" + user.getCpu() + "\n";
            str += "Code: " + user.getUserCode();

            return str;


        } else {
            return "User not found";
        }
    }


}
