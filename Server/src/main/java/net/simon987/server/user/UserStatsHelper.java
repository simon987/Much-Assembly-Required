package net.simon987.server.user;

import com.mongodb.client.MongoCollection;
import net.simon987.server.GameServer;
import org.bson.Document;

import java.util.*;

/**
 * Retrieve user stats in a structured fashion
 */
public class UserStatsHelper {

    /**
     * Database collection of users
     */
    private MongoCollection<Document> users;

    /**
     * @param users Database collection of users
     */
    public UserStatsHelper(MongoCollection<Document> users) {
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

        Document orderBy = new Document("$stats." + statName, -1);

        for (Document dbUser : users.find().sort(orderBy).limit(n)) {
            User user = GameServer.INSTANCE.getGameUniverse().getUser((String) dbUser.get("username"));
            int val = 0;
            if (user.getStats().getInt(statName) > 0) {
                val = user.getStats().getInt(statName);
            }
            rows.add(new AbstractMap.SimpleEntry<>(user, val));
        }

        return rows;
    }

    /**
     * Get top n players along with all their stat values, in descending order of completed vaults
     *
     * @param n Maximum number of players
     * @return Top n players, in User,value format, in descending order
     */
    public ArrayList<Map.Entry<User, Map<String, Integer>>> getLeaderboardStats(int n) {

        ArrayList<Map.Entry<User, Map<String, Integer>>> rows = new ArrayList<>(n);

        List<User> users = getTopNSetSize("completedVaults", n);

        for (User user : users) {
            Map<String, Integer> allStats = new HashMap<>();
            allStats.put("completedVaults", user.getStats().getSet("completedVaults").size());
            allStats.put("death", user.getStats().getInt("death"));
            allStats.put("executionTime", user.getStats().getInt("executionTime"));
            allStats.put("walkDistance", user.getStats().getInt("walkDistance"));
            rows.add(new AbstractMap.SimpleEntry<>(user, allStats));
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
    private List<User> getTopNSetSize(String statName, int n) {

        ArrayList<User> rows = new ArrayList<>();

        List<Object> ifNullList = new ArrayList<>(2);
        ifNullList.add("$stats." + statName);
        ifNullList.add(new ArrayList());

        Document project = new Document();
        project.put("setLength", new Document("$size", new Document("$ifNull", ifNullList)));
        project.put("username", 1);

        for (Document document : users.aggregate(Arrays.asList(
                new Document("$project", project),
                new Document("$sort", new Document("setLength", -1)),
                new Document("$limit", n))
        )) {
            User user = GameServer.INSTANCE.getGameUniverse().getUser((String) document.get("username"));
            rows.add(user);
        }

        return rows;
    }
}
