package net.simon987.server.game.pathfinding;

/**
 * A single node in the search graph
 * <p>
 * Inspired by http://www.cokeandcode.com/main/tutorials/path-finding/
 */
public class Node implements Comparable {

    /**
     * x coordinate of the node
     */
    public int x;

    /**
     * y coordinate of the node
     */
    public int y;

    /**
     * Cost of getting from the start node to this node
     */
    public int gScore;

    /**
     * Total cost of getting from the start node to the goal
     */
    public int fScore;

    /**
     * Parent of the node
     */
    public Node parent;


    /**
     * Create a new Node
     *
     * @param x X coordinate of the node
     * @param y Y coordinate of the node
     */
    public Node(int x, int y) {
        this.x = x;
        this.y = y;

        gScore = Integer.MAX_VALUE;
        fScore = Integer.MAX_VALUE;
    }

    /**
     * Compare two Nodes using their fScore
     */
    @Override
    public int compareTo(Object o) {
        Node other = (Node) o;

        return Integer.compare(fScore, other.fScore);
    }

    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }
}
