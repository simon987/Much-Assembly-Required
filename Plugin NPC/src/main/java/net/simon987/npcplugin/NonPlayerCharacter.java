package net.simon987.npcplugin;

import net.simon987.server.GameServer;
import net.simon987.server.assembly.Util;
import net.simon987.server.game.objects.*;
import net.simon987.server.game.pathfinding.Node;
import net.simon987.server.game.pathfinding.Pathfinder;
import net.simon987.server.logging.LogManager;
import org.bson.Document;

import java.util.ArrayList;

/**
 * Game object that actively interacts with the game world by doing tasks
 */
public abstract class NonPlayerCharacter extends GameObject implements Updatable, Attackable {

    private static final char MAP_INFO = 0x0501;

    /**
     * Maximum distance to travel from its factory, in Worlds
     */
    private static final int MAX_FACTORY_DISTANCE = GameServer.INSTANCE.getConfig().getInt("npc_max_factory_distance");

    /**
     * Number of ticks to live
     */
    public static final int LIFETIME = GameServer.INSTANCE.getConfig().getInt("npc_lifetime");

    // Set these just in case they aren't overridden in the subclass
    public static final int HP_MAX_DEFAULT = 100;
    public static final int HP_REGEN_RATE_DEFAULT = 0;

    /**
     * Current task
     */
    private NPCTask task;

    /**
     * Action at the end of the last tick
     */
    private Action lastAction = Action.IDLE;

    /**
     * Factory that created this NPC
     */
    private Factory factory;

    /**
     * If set to true, the NPC will be destroyed next tick if it is
     * not linked to a Factory
     */
    private boolean selfDestroyNextTick = false;

    /**
     * Age of the npc, in ticks
     */
    private int age = 0;

    /**
     * Current health of the npc
     */
    private int hp = HP_MAX_DEFAULT;

    /**
     * Health regeneration rate of the npc
     */
    private int hpRegenerationRate = HP_REGEN_RATE_DEFAULT;

    /**
     * Maximum health of the npc
     */
    private int maxHp = HP_MAX_DEFAULT;

    public NonPlayerCharacter() {

    }

    public NonPlayerCharacter(Document document) {
        super(document);

        hp = document.getInteger("hp");
        setDirection(Direction.getDirection(document.getInteger("direction")));
    }

    @Override
    public char getMapInfo() {
        return MAP_INFO;
    }

    @Override
    public void update() {

        age++;

        //Destroy NPCs that are not linked with a Factory
        if (factory == null) {
            if (selfDestroyNextTick) {
                setDead(true);
            }

            selfDestroyNextTick = true;
        }

        //Heal the NPC
        heal(hpRegenerationRate);
    }

    /**
     * Attempt to move the NPC to the specified coordinates
     *
     * @param range distance to the desired coordinates, in tiles
     * @return true if the path is passable
     */
    boolean moveTo(int x, int y, int range) {

        ArrayList<Node> path = Pathfinder.findPath(getWorld(), getX(), getY(), x, y, range);

        if (path != null && path.size() > 0) {

            Node nextTile = path.get(1);

            Direction newDirection = Direction.getFacing(getX(), getY(), nextTile.x, nextTile.y);

            if (newDirection != null) {
                setDirection(newDirection);
            } else {
                LogManager.LOGGER.severe("FIXME: moveTo:NonPlayerCharacter, Direction is null");
            }

            if (incrementLocation()) {
                lastAction = Action.WALKING;
                return true;
            }
        }

        lastAction = Action.IDLE;
        return false;
    }

    /**
     * Go to the next World in the specified Direction.
     *
     * @return true if the World in the specified Direction is within the max. distance from the Factory
     */
    boolean gotoWorld(Direction direction) {

        if (direction == Direction.NORTH) {

            if (Util.manhattanDist(factory.getWorld().getX(), factory.getWorld().getY(),
                    getWorld().getX(), getWorld().getY() - 1) <= MAX_FACTORY_DISTANCE) {
                if (!moveTo(8, 0, 0)) {
                    setDirection(Direction.NORTH);
                    incrementLocation();
                }
                return true;
            } else {
                return false;
            }

        } else if (direction == Direction.EAST) {
            if (Util.manhattanDist(factory.getWorld().getX(), factory.getWorld().getY(),
                    getWorld().getX() + 1, getWorld().getY()) <= MAX_FACTORY_DISTANCE) {
                if (!moveTo(15, 7, 0)) {
                    setDirection(Direction.EAST);
                    incrementLocation();
                }
                return true;
            } else {
                return false;
            }
        } else if (direction == Direction.SOUTH) {
            if (Util.manhattanDist(factory.getWorld().getX(), factory.getWorld().getY(),
                    getWorld().getX(), getWorld().getY() + 1) <= MAX_FACTORY_DISTANCE) {
                if (!moveTo(8, 15, 0)) {
                    setDirection(Direction.SOUTH);
                    incrementLocation();
                }
                return true;
            } else {
                return false;
            }
        } else if (direction == Direction.WEST) {
            if (Util.manhattanDist(factory.getWorld().getX(), factory.getWorld().getY(),
                    getWorld().getX() - 1, getWorld().getY()) <= MAX_FACTORY_DISTANCE) {
                if (!moveTo(0, 7, 0)) {
                    setDirection(Direction.WEST);
                    incrementLocation();
                }
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public void setHealRate(int hp) {
        hpRegenerationRate = hp;
    }

    @Override
    public int getHp() {
        return hp;
    }

    @Override
    public void setHp(int hp) {
        this.hp = hp;
    }

    @Override
    public int getMaxHp() {
        return maxHp;
    }

    @Override
    public void setMaxHp(int hp) {
        this.maxHp = hp;
        this.hp = hp;
    }

    @Override
    public void heal(int amount) {
        hp += amount;

        //Can't heal above max
        if (hp > maxHp) {
            hp = maxHp;
        }
    }

    @Override
    public void damage(int amount) {
        hp -= amount;

        //YOU ARE DEAD
        if (hp <= 0) {
            setDead(true);
        }
    }

    public NPCTask getTask() {
        return task;
    }

    public void setTask(NPCTask task) {
        this.task = task;
    }

    public Action getAction() {
        return lastAction;
    }

    public Factory getFactory() {
        return factory;
    }

    public void setFactory(Factory factory) {
        this.factory = factory;
    }

    public int getAge() {
        return age;
    }

}
