package net.simon987.server.game.pathfinding;

import net.simon987.server.assembly.Util;
import net.simon987.server.game.world.World;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Class to compute paths in the game universe. It supports
 * paths within the same World.
 */
public class Pathfinder {


    /**
     * Create a pathfinder
     */
    public Pathfinder() {

    }

    /**
     * Find the shortest path between 2 set of coordinates within a single World
     * <p>
     * based on https://en.wikipedia.org/wiki/A*_search_algorithm
     *
     * @param world World to search the path in
     * @param sX    X coordinate of the start
     * @param sY    Y coordinate of the start
     * @param gX    X coordinate of the goal
     * @param gY    Y coordinate of the goal
     * @return The shortest path to the goal from the start
     */
    public static ArrayList<Node> findPath(World world, int sX, int sY, int gX, int gY, int range) {

        if (sX == gX && sY == gY) {
            return null;
        }

        if (gX < 0 || gX > 15 || gY < 0 || gY > 15) {
            return null;
        }


        ArrayList<Node> closed = new ArrayList<>(64);
        SortedArrayList open = new SortedArrayList();

        //Initialize node map
        Node[][] nodes = new Node[world.getWorldSize()][world.getWorldSize()];

        for (int x = 0; x < world.getWorldSize(); x++) {
            for (int y = 0; y < world.getWorldSize(); y++) {

                nodes[x][y] = new Node(x, y);

            }
        }

        Node start = nodes[sX][sY];

        //The cost of going from start to start is 0
        start.gScore = 0;
        start.fScore = (Math.abs(sX - gX) + Math.abs(sY - gY));

        open.add(start);


        while (open.size() > 0) {
            Node current = open.first();

            if (Util.manhattanDist(current.x, current.y, gX, gY) <= range) {
                //goal is reached
                //Reconstruct the path
                ArrayList<Node> reconstructedPath = new ArrayList<>(48);


                while (current != null) {
                    reconstructedPath.add(current);
                    current = current.parent; // crawl back up to the start
                }

                //Reverse in the start -> goal order
                Collections.reverse(reconstructedPath);

                return reconstructedPath;

            }

            open.remove(current);
            closed.add(current);

            ArrayList<Node> neighbors = getNeighbors(world, nodes, current);
            for (Node neighbor : neighbors) {

                if (closed.contains(neighbor)) {
                    continue;
                }

                int tentativeGScore = current.gScore + 1;

                if (!open.contains(neighbor)) {
                    open.add(neighbor);
                } else if (tentativeGScore >= neighbor.gScore) {
                    continue;
                }

                neighbor.parent = current;
                neighbor.gScore = tentativeGScore;
                neighbor.fScore = neighbor.gScore + (Math.abs(neighbor.x - gX) + Math.abs(neighbor.y - gY));
            }

        }

        //Incomplete path
        // LogManager.LOGGER.fine("Incomplete path! " + counter);
        return null;

    }

    /**
     * Get the valid neighbors of a node within a single World
     *
     * @param world World to check the validity of a position
     * @param nodes Map of nodes to get the neighbors from
     * @param node  Node to get the neighbors of
     * @return a list of valid neighbors of the specified node
     */
    private static ArrayList<Node> getNeighbors(World world, Node[][] nodes, Node node) {

        ArrayList<Node> neighbors = new ArrayList<>(4);

        //Check if left neighbor is within the World boundaries and isn't blocked
        if (node.x != 0 && !world.isTileBlocked(node.x - 1, node.y)) {
            neighbors.add(nodes[node.x - 1][node.y]);
        }

        //Check if the right neighbor is within the World boundaries and isn't blocked
        if (node.x != (world.getWorldSize() - 1) && !world.isTileBlocked(node.x + 1, node.y)) {
            neighbors.add(nodes[node.x + 1][node.y]);
        }

        //Check if the top neighbor is within the World boundaries and isn't blocked
        if (node.y != 0 && !world.isTileBlocked(node.x, node.y - 1)) {
            neighbors.add(nodes[node.x][node.y - 1]);
        }

        //Check if the bottom neighbor is within the World boundaries and isn't blocked
        if (node.y != (world.getWorldSize() - 1) && !world.isTileBlocked(node.x, node.y + 1)) {
            neighbors.add(nodes[node.x][node.y + 1]);
        }

        return neighbors;
    }

}
