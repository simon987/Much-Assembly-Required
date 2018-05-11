package net.simon987.server.user;

import com.mongodb.*;
import net.simon987.server.GameServer;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;

/**
 * Retrieve user stats in a structured fashion
 */
public class UserStatsHelper {

    /**
     * Database collection of users
     */
    private DBCollection users;

    /**
     * @param users Database collection of users
     */
    public UserStatsHelper(DBCollection users) {
        this.users = users;
    }

    /**
     * Get top n players along with their stat value, in descending order
     *
     * @param statName Name of the stat
     * @param n        Maximum number of players
     * @return Top n players, in User,value format, in descending order
     */
    public ArrayList<Map.Entry<User, Integer>> getTopN(String statName, int n) {

        ArrayList<Map.Entry<User, Integer>> rows = new ArrayList<>();

        BasicDBObject orderBy = new BasicDBObject("$stats." + statName, -1);
        DBCursor cursor = users.find().sort(orderBy).limit(n);

        while (cursor.hasNext()) {
            DBObject dbUser = cursor.next();
            User user = GameServer.INSTANCE.getGameUniverse().getUser((String) dbUser.get("username"));
            rows.add(new AbstractMap.SimpleEntry<>(user, user.getStats().getInt(statName)));
        }

        return rows;
    }

    /**
     * Get top n players along with the stat set, in descending order
     *
     * @param statName Name of the stat
     * @param n        Maximum number of players
     * @return Top n players, in User,set format, in descending order
     */
    public ArrayList<Map.Entry<User, BasicDBList>> getTopNSetLength(String statName, int n) {

        ArrayList<Map.Entry<User, BasicDBList>> rows = new ArrayList<>();

        BasicDBList ifNullList = new BasicDBList();
        ifNullList.add("$stats." + statName);
        ifNullList.add(new BasicDBList());

        BasicDBObject project = new BasicDBObject();
        project.put("setLength", new BasicDBObject("$size", new BasicDBObject("$ifNull", ifNullList)));
        project.put("username", 1);

        Iterable<DBObject> results = users.aggregate(
                new BasicDBObject("$project", project),
                new BasicDBObject("$sort", new BasicDBObject("setLength", -1)),
                new BasicDBObject("$limit", n)
        ).results();

        for (DBObject dbUser : results) {
            User user = GameServer.INSTANCE.getGameUniverse().getUser((String) dbUser.get("username"));
            rows.add(new AbstractMap.SimpleEntry<>(user, user.getStats().getSet(statName)));
        }

        return rows;
    }
}
