package net.simon987.server.user;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import net.simon987.server.io.MongoSerialisable;
import net.simon987.server.logging.LogManager;

public class UserStats implements MongoSerialisable {

    private BasicDBObject stats;

    UserStats() {
        this.stats = new BasicDBObject();
    }

    UserStats(BasicDBObject stats) {
        if (stats != null) {
            this.stats = stats;
        } else {
            this.stats = new BasicDBObject();
        }
    }

    @Override
    public BasicDBObject mongoSerialise() {

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
        stats.put(name, stats.getInt(name) + count);
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

        return stats.getInt(name, 0);
    }

    /**
     * Add an string item to a set
     *
     * @param name  Name of the stat
     * @param value Value to add to the set
     */
    public void addToStringSet(String name, String value) {

        stats.putIfAbsent(name, new BasicDBList());

        try {
            ((BasicDBList) stats.get(name)).add(value);
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

        if (stats.putIfAbsent(name, new BasicDBList()) != null) {
            return ((BasicDBList) stats.get(name)).remove(value);
        }

        return false;
    }

    public BasicDBList getSet(String name) {
        stats.putIfAbsent(name, new BasicDBList());

        return (BasicDBList) stats.get(name);
    }
}