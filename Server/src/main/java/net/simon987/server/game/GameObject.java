package net.simon987.server.game;

import com.mongodb.DBObject;
import net.simon987.server.GameServer;
import net.simon987.server.io.GameObjectDeserializer;
import net.simon987.server.io.JSONSerialisable;
import net.simon987.server.io.MongoSerialisable;
import net.simon987.server.plugin.ServerPlugin;
import org.json.simple.JSONObject;

import java.awt.*;

/**
 * An INSTANCE of an object (e.g. a Tree, a character ...) inside the
 * game universe
 */
public abstract class GameObject implements JSONSerialisable, MongoSerialisable {

    private boolean dead;
    /**
     * Object's unique identifier
     */
    private long objectId;

    /**
     * X coordinate of the object in its World
     */
    private int x;

    /**
     * Y coordinate of the object in its World
     */
    private int y;

    /**
     * Direction of the object
     */
    private Direction direction = Direction.NORTH;

    /**
     * Current World of the object
     */
    private World world;


    //--------

    /**
     * Increment the location of the game object by 1 tile
     * Collision checks happen here
     */
    public boolean incrementLocation() {

        int newX = 0, newY = 0;

        if (direction == Direction.NORTH) {
            newX = x;
            newY = (y - 1);

        } else if (direction == Direction.EAST) {
            newX = (x + 1);
            newY = y;

        } else if (direction == Direction.SOUTH) {
            newX = x;
            newY = (y + 1);

        } else if (direction == Direction.WEST) {
            newX = (x - 1);
            newY = y;
        }


        //Check if out of World bounds / collision
        if (newX < 0) {
            //Move object to adjacent World (left)
            World leftWorld;
            if (world.getX() == 0) {
                //Warp around
                leftWorld = GameServer.INSTANCE.getGameUniverse().getWorld(
                        GameServer.INSTANCE.getGameUniverse().getMaxWidth(), world.getY(), true);
            } else {
                leftWorld = GameServer.INSTANCE.getGameUniverse().getWorld(world.getX() - 1, world.getY(), true);
            }

            if (leftWorld != null) {
                world.removeObject(this);
                world.decUpdatable();
                leftWorld.addObject(this);
                leftWorld.incUpdatable();
                setWorld(leftWorld);

                x = World.WORLD_SIZE - 1;
            }
        } else if (newX >= World.WORLD_SIZE) {
            //Move object to adjacent World (right)
            World rightWorld;
            if (world.getX() == GameServer.INSTANCE.getGameUniverse().getMaxWidth()) {
                //Warp around
                rightWorld = GameServer.INSTANCE.getGameUniverse().getWorld(0, world.getY(), true);
            } else {
                rightWorld = GameServer.INSTANCE.getGameUniverse().getWorld(world.getX() + 1, world.getY(), true);
            }

            if (rightWorld != null) {
                world.removeObject(this);
                world.decUpdatable();
                rightWorld.addObject(this);
                rightWorld.incUpdatable();
                setWorld(rightWorld);

                x = 0;
            }
        } else if (newY < 0) {
            //Move object to adjacent World (up)
            World upWorld;
            if (world.getY() == 0) {
                //Warp around
                upWorld = GameServer.INSTANCE.getGameUniverse().getWorld(world.getX(),
                        GameServer.INSTANCE.getGameUniverse().getMaxWidth(), true);
            } else {
                upWorld = GameServer.INSTANCE.getGameUniverse().getWorld(world.getX(), world.getY() - 1, true);
            }

            if (upWorld != null) {
                world.removeObject(this);
                world.decUpdatable();
                upWorld.addObject(this);
                upWorld.incUpdatable();
                setWorld(upWorld);

                y = World.WORLD_SIZE - 1;
            }
        } else if (newY >= World.WORLD_SIZE) {
            //Move object to adjacent World (down)
            World downWorld;
            if (world.getY() == GameServer.INSTANCE.getGameUniverse().getMaxWidth()) {
                //Warp around
                downWorld = GameServer.INSTANCE.getGameUniverse().getWorld(world.getX(), 0, true);
            } else {
                downWorld = GameServer.INSTANCE.getGameUniverse().getWorld(world.getX(), world.getY() + 1, true);
            }


            if (downWorld != null) {
                world.removeObject(this);
                world.decUpdatable();
                downWorld.addObject(this);
                downWorld.incUpdatable();
                setWorld(downWorld);

                y = 0;
            }
        }
        //Check collision
        else if (!world.isTileBlocked(newX, newY)) {
            //Tile is passable
            x = newX;
            y = newY;
        } else {
            return false;
        }

        return true;

    }

    public abstract char getMapInfo();

    public Point getFrontTile() {

        if (direction == Direction.NORTH) {
            return new Point(x, y - 1);
        } else if (direction == Direction.EAST) {
            return new Point(x + 1, y);
        } else if (direction == Direction.SOUTH) {
            return new Point(x, y + 1);
        } else {
            return new Point(x - 1, y);
        }

    }

    public long getObjectId() {
        return objectId;
    }

    public void setObjectId(long objectId) {
        this.objectId = objectId;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    @Override
    public JSONObject serialise() {
        return new JSONObject();
    }

    public static GameObject deserialize(DBObject obj) {
//
        for (ServerPlugin plugin : GameServer.INSTANCE.getPluginManager().getPlugins()) {

            if (plugin instanceof GameObjectDeserializer) {
                GameObject object = ((GameObjectDeserializer) plugin).deserializeObject(obj);

                if (object != null) {
                    return object;
                }
            }
        }

        return null;
    }


    public boolean isAt(int x, int y) {
        return this.x == x && this.y == y;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    /**
     * Called before this GameObject is removed from the world - defaults to doing nothing
     */
    public void onDeadCallback() { }
}