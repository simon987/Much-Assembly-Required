package net.simon987.server.user;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import net.simon987.server.GameServer;
import net.simon987.server.assembly.exception.CancelledException;
import net.simon987.server.crypto.RandomStringGenerator;
import net.simon987.server.logging.LogManager;
import org.bson.Document;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.ArrayList;

public class UserManager {

    private MongoCollection<Document> userCollection;

    public UserManager(MongoCollection<Document> userCollection) {

        this.userCollection = userCollection;
    }

    /**
     * Get and deserialise all users from mongo
     *
     * @return list of de-serialized users
     */
    public ArrayList<User> getUsers() {

        ArrayList<User> userList = new ArrayList<>();

        MongoCursor<Document> cursor = userCollection.find().iterator();
        while (cursor.hasNext()) {
            try {
                userList.add(User.deserialize(cursor.next()));
            } catch (CancelledException e) {
                e.printStackTrace();
            }
        }

        return userList;
    }

    /**
     * Register an user and initialises its controlled unit
     * @param username username
     * @param password plain password
     * @throws RegistrationException is username/password length is invalid
     */
    public void registerUser(String username, String password) throws RegistrationException {

        if (username.length() < 5 || username.length() > 20) {
            throw new RegistrationException("Username must be 5-20 characters");
        }
        if (password.length() < 8 || password.length() > 96) {
            throw new RegistrationException("Password must be 8-96 characters");
        }

        //Check if exists
        Document where = new Document();
        where.put("_id", username);

        if (userCollection.find(where).first() != null) {
            throw new RegistrationException("Username is already in use");
        }

        try {
            User user = GameServer.INSTANCE.getGameUniverse().getOrCreateUser(username, true);
            user.setUsername(username);

            String salt = BCrypt.gensalt();
            String hashedPassword = BCrypt.hashpw(password, salt);
            user.setPassword(hashedPassword);

            Document dbUser = user.mongoSerialise();

            userCollection.insertOne(dbUser);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RegistrationException("An exception occurred while trying to create user: " + e.getMessage());
        }
    }

    /**
     * Validate a username/password combo
     * @param username username
     * @param password plain password
     * @return true if combo is valid
     */
    public boolean validateUser(String username, String password) {

        Document where = new Document();
        where.put("_id", username);

        Document user = userCollection.find(where).first();
        return user != null && BCrypt.checkpw(password, (String) user.get("password"));
    }

    /**
     * Change the password of an user and immediately save it
     * @param username Username
     * @param newPassword New plain password
     * @throws RegistrationException When password length is invalid
     */
    public void changePassword(String username, String newPassword) throws RegistrationException {

        if (newPassword.length() < 8 || newPassword.length() > 96) {
            throw new RegistrationException("Password must be 8-96 characters");
        }

        User user = GameServer.INSTANCE.getGameUniverse().getUser(username);

        String salt = BCrypt.gensalt();
        String hashedPassword = BCrypt.hashpw(newPassword, salt);
        user.setPassword(hashedPassword);

        userCollection.replaceOne(new Document("_id", username), user.mongoSerialise()); //Save new password immediately
    }

    /**
     * Generate and save a access token for websocket auth
     *
     * @param username Username
     * @return The generated token
     */
    public String generateAndGetToken(String username) {

        User user = GameServer.INSTANCE.getGameUniverse().getUser(username);

        if (user == null) {
            return null;
        }

        RandomStringGenerator generator = new RandomStringGenerator(128);
        String token = generator.nextString();

        user.setAccessToken(token); //Token is not saved in DB, and is erased when used

        LogManager.LOGGER.fine("(Web) Generated access token for " + username);

        return token;
    }

    /**
     * Validate an access token sent by the client
     * @param token 128-char accesss token
     * @return username of the corresponding user, null if not found
     */
    public User validateAuthToken(String token) {

        for (User user : GameServer.INSTANCE.getGameUniverse().getUsers()) {

            if (user.getAccessToken() != null && user.getAccessToken().equals(token)) {
                user.setAccessToken(""); //Token is erased when used

                return user;
            }
        }

        return null;
    }
}
