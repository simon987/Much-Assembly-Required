package net.simon987.server.web;

import net.simon987.server.ServerConfiguration;
import net.simon987.server.logging.LogManager;
import net.simon987.server.websocket.SocketServer;
import org.apache.velocity.app.VelocityEngine;
import spark.Spark;
import spark.template.velocity.VelocityTemplateEngine;

import java.util.Properties;

public class WebServer {

    private SocketServer socketServer;
    private GuestPolicy guestPolicy;

    public WebServer(ServerConfiguration config) {

        //Velocity config
        Properties properties = new Properties();
        properties.setProperty("file.resource.loader.path", "templates/");
        VelocityTemplateEngine templateEngine = new VelocityTemplateEngine(new VelocityEngine(properties));
        Spark.staticFiles.externalLocation("static");

        //Spark config
        if (config.getInt("use_ssl") != 0) {

            /*
             * Generate keystore from Let's Encrypt with command:
             * openssl pkcs12 -export -in fullchain.pem -inkey privkey.pem -out keystore.jks -name muchassemblyrequired -CAfile chain.pem -caname root -password file:password.txt
             *
             * Certificates generated from Let's Encrypt are usually in /etc/letsencrypt/live/www.site.com
             */
            Spark.secure(
                    config.getString("keyStore_path"),
                    config.getString("keyStore_password"), null, null);
            LogManager.LOGGER.info("(Web) Enabled ssl");
        }

        initGuestPolicy(config);

        socketServer = new SocketServer(guestPolicy);

        Spark.webSocket("/socket", socketServer);

        Spark.get("/", new HomePage(), templateEngine);
        Spark.get("/leaderboard", new LeaderBoardPage(), templateEngine);
        Spark.get("/play", new PlayPage(), templateEngine);
        Spark.get("/account", new AccountPage(), templateEngine);

        Spark.post("/register", new RegisterRoute());
        Spark.post("/login", new LoginRoute());
        Spark.get("/logout", new LogoutRoute());
        Spark.post("/change_password", new ChangePasswordRoute());
        Spark.get("/server_info", new ServerInfoRoute());
        Spark.post("/floppy_upload", new FloppyUploadRoute());
        Spark.get("/floppy_download", new FloppyDownloadRoute());

        Spark.after((request, response) -> response.header("Content-Encoding", "gzip"));
    }

    /**
     * Load guest policy from config.
     * If no valid policy is specified in the config, the default <code>GuestPolicy.ALLOW</code> is used
     */
    private void initGuestPolicy(ServerConfiguration config) {
        String guestPolicyStr = config.getString("guest_policy");
        try {
            this.guestPolicy = GuestPolicy.valueOf(guestPolicyStr);
        } catch (IllegalArgumentException | NullPointerException e) {
            System.err.println("Invalid argument for guest policy: " + guestPolicyStr);
            this.guestPolicy = GuestPolicy.ALLOW;
        }
    }

    public SocketServer getSocketServer() {
        return socketServer;
    }

}
