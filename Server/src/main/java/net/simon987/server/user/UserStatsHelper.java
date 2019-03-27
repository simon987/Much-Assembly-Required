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
            rows.add(new AbstractMap.SimpleEntry<>(user, user.getStats().getInt(statName)));
        }

        return rows;
    }

    /**
     * Get top n players along with all their stat values, in descending order
     *
     * @param n        Maximum number of players
     * @return Top n players, in User,value format, in descending order
     */
    public ArrayList<Map.Entry<User, Map<String, Integer>>> getTopNAll(int n) {

        ArrayList<Map.Entry<User, Map<String, Integer>>> rows = new ArrayList<>();

        ArrayList<Map.Entry<User, ArrayList>> vaults = new ArrayList<>(this.getTopNSetLength("completedVaults", n));
        ArrayList<Map.Entry<User, Integer>>  deaths = new ArrayList<>(this.getTopN("deathCount", n));
        ArrayList<Map.Entry<User, Integer>>  time = new ArrayList<>(this.getTopN("executionTime", n));
        ArrayList<Map.Entry<User, Integer>>  distance = new ArrayList<>(this.getTopN("walkDistance", n));

        for (int i = 0; i < n ; i++) {
            User user = vaults.get(i).getKey();
            Map<String, Integer> allStats = new HashMap();
            allStats.put("completedVaults", vaults.get(i).getValue().size());
            allStats.put("deathCount", deaths.get(i).getValue());
            allStats.put("executionTime", time.get(i).getValue());
            allStats.put("walkDistance", distance.get(i).getValue());
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
    public ArrayList<Map.Entry<User, ArrayList>> getTopNSetLength(String statName, int n) {

        ArrayList<Map.Entry<User, ArrayList>> rows = new ArrayList<>();

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
            rows.add(new AbstractMap.SimpleEntry<>(user, user.getStats().getSet(statName)));
        }

        return rows;
    }
}
