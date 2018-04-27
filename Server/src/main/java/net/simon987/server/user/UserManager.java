package net.simon987.server.user;

import com.mongodb.*;
import net.simon987.server.assembly.exception.CancelledException;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.ArrayList;

public class UserManager {

    private MongoClient mongo;
    private DBCollection userCollection;

    public UserManager(MongoClient mongo) {

        this.mongo = mongo;
        DB db = mongo.getDB("mar");
        userCollection = db.getCollection("user");
    }

    public ArrayList<User> getUsers() {

        ArrayList<User> userList = new ArrayList<>();

        DBCursor cursor = userCollection.find();
        while (cursor.hasNext()) {
            try {
                userList.add(User.deserialize(cursor.next()));
            } catch (CancelledException e) {
                e.printStackTrace();
            }
        }

        return userList;
    }

    public void registerUser(User user, String password) {

        DBObject dbUser = user.mongoSerialise();

        String salt = BCrypt.gensalt(12);
        String hashedPassword = BCrypt.hashpw(password, salt);

        dbUser.put("password", hashedPassword);

        userCollection.save(dbUser);
    }

    public boolean validateUser(String username, String password) {

        DBObject where = new BasicDBObject();
        where.put("_id", username);

        DBObject user = userCollection.findOne(where);

        return user != null && BCrypt.checkpw(password, (String) user.get("password"));
    }
}
