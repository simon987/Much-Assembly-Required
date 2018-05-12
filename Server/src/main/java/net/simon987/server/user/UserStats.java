package net.simon987.server.user;

import net.simon987.server.io.MongoSerializable;
import net.simon987.server.logging.LogManager;
import org.bson.Document;

import java.util.ArrayList;

public class UserStats implements MongoSerializable {

    private Document stats;

    UserStats() {
        this.stats = new Document();
    }

    UserStats(Document stats) {
        if (stats != null) {
            this.stats = stats;
        } else {
            this.stats = new Document();
        }
    }

    @Override
    public Document mongoSerialise() {

        return stats;
    }

    /**
     * Increment a stat n times
     *
     * @param name  Name of the stat to increment
     * @param count Number of time to increment
     */
    public void incrementStat(String name, int count) {

        stats.putIfAbsent(name, 0);
        stats.put(name, stats.getInteger(name) + count);
    }

    /**
     * Set the value of a stat
     *
     * @param name  Name of the stat
     * @param value new value
     */
    public void setInt(String name, int value) {

        stats.put(name, value);
    }

    /**
     * Get the value of at stat
     *
     * @param name Name of the value
     * @return The value of the stat. Returns 0 if not found
     */
    public int getInt(String name) {
        return stats.getInteger(name, 0);
    }

    /**
     * Add an string item to a set
     *
     * @param name  Name of the stat
     * @param value Value to add to the set
     */
    public void addToStringSet(String name, String value) {

        stats.putIfAbsent(name, new ArrayList<>());

        try {
            ((ArrayList<String>) stats.get(name)).add(value);
        } catch (ClassCastException e) {
            LogManager.LOGGER.severe("UserStats: cannot add to list because stat already exists and is not a list");
        }
    }

    /**
     * Remove an item from a set
     *
     * @param name  Name of the stat
     * @param value Value to remove from the set
     * @return true if the list contained the item
     */
    public boolean removeFromSet(String name, String value) {

        if (stats.putIfAbsent(name, new ArrayList()) != null) {
            return ((ArrayList) stats.get(name)).remove(value);
        }

        return false;
    }

    public ArrayList getSet(String name) {
        stats.putIfAbsent(name, new ArrayList());

        return (ArrayList) stats.get(name);
    }
}