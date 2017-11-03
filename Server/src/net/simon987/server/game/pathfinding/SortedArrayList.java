package net.simon987.server.game.pathfinding;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Wrapper of ArrayList to make it sorted
 * <p>
 * inspired by http://www.cokeandcode.com/main/tutorials/path-finding/
 */
public class SortedArrayList extends ArrayList<Node> {

    /**
     * Get the first element from the list
     *
     * @return element at index 0
     */
    Node first() {
        return get(0);
    }


    /**
     * Add an node to the list and sort it
     *
     * @param node node to add
     * @return always return true
     */
    @Override
    public boolean add(Node node) {
        super.add(node);
        Collections.sort(this);

        return true; //Return value ignored
    }


}
