package net.simon987.npcplugin;

import net.simon987.server.game.Action;
import net.simon987.server.game.Direction;
import net.simon987.server.game.GameObject;
import net.simon987.server.game.Updatable;
import net.simon987.server.game.pathfinding.Node;
import net.simon987.server.game.pathfinding.Pathfinder;
import net.simon987.server.logging.LogManager;

import java.util.ArrayList;

public abstract class NonPlayerCharacter extends GameObject implements Updatable {

    private static final int MAP_INFO = 0x0040;

    protected int hp;

    protected int energy;
    protected int maxEnergy;

    private NPCTask task;

    private Action lastAction = Action.IDLE;

    @Override
    public char getMapInfo() {
        return MAP_INFO;
    }


    public boolean moveTo(int x, int y, int range) {

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

    public boolean gotoWorld(Direction direction) {

        System.out.println("going " + direction);

        if (direction == Direction.NORTH) {
            if (!moveTo(8, 0, 0)) {
                setDirection(Direction.NORTH);
                return incrementLocation();
            }
        } else if (direction == Direction.EAST) {
            if (!moveTo(15, 8, 0)) {
                setDirection(Direction.EAST);
                return incrementLocation();
            }
        } else if (direction == Direction.SOUTH) {
            if (!moveTo(7, 15, 0)) {
                setDirection(Direction.SOUTH);
                return incrementLocation();
            }
        } else if (direction == Direction.WEST) {
            if (!moveTo(0, 7, 0)) {
                setDirection(Direction.WEST);
                return incrementLocation();
            }
        } else {
            return false;
        }

        return true;
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
}
