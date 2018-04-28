package net.simon987.server.user;

import com.mongodb.*;
import net.simon987.server.GameServer;
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

    public void registerUser(String username, String password) throws RegistrationException {

        if (username.length() < 5 || username.length() > 20) {
            throw new RegistrationException("Username must be 5-20 characters");
        }
        if (password.length() < 8 || password.length() > 96) {
            throw new RegistrationException("Password must be 8-96 characters");
        }

        //Check if exists
        DBObject where = new BasicDBObject();
        where.put("_id", username);

        if (userCollection.findOne(where) != null) {
            throw new RegistrationException("Username is already in use");
        }

        try {
            User user = GameServer.INSTANCE.getGameUniverse().getOrCreateUser(username, true);
            user.setUsername(username);

            String salt = BCrypt.gensalt();
            String hashedPassword = BCrypt.hashpw(password, salt);
            user.setPassword(hashedPassword);

            DBObject dbUser = user.mongoSerialise();

            userCollection.save(dbUser);
        } catch (Exception e) {
            throw new RegistrationException("An exception occurred while trying to create user: " + e.getMessage());
        }
    }

    public boolean validateUser(String username, String password) {

        DBObject where = new BasicDBObject();
        where.put("_id", username);

        DBObject user = userCollection.findOne(where);
        return user != null && BCrypt.checkpw(password, (String) user.get("password"));
    }

    public void changePassword(String username, String newPassword) throws RegistrationException {

        if (newPassword.length() < 8 || newPassword.length() > 96) {
            throw new RegistrationException("Password must be 8-96 characters");
        }

        User user = GameServer.INSTANCE.getGameUniverse().getUser(username);

        String salt = BCrypt.gensalt();
        String hashedPassword = BCrypt.hashpw(newPassword, salt);
        user.setPassword(hashedPassword);

        userCollection.save(user.mongoSerialise()); //Save new password immediately
    }
}
