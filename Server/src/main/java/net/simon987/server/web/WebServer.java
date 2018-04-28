package net.simon987.server.web;

import net.simon987.server.ServerConfiguration;
import net.simon987.server.logging.LogManager;
import net.simon987.server.websocket.SocketServer;
import org.apache.velocity.app.VelocityEngine;
import spark.Spark;
import spark.template.velocity.VelocityTemplateEngine;

import java.util.Properties;

public class WebServer {

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
             * openssl pkcs12 -export -in fullchain.pem -inkey privkey.pem -out keystore.kjs -name muchassemblyrequired -CAfile chain.pem -caname root -password file:password.txt
             *
             * Certificates generated from Let's Encrypt are usually in /etc/letsencrypt/live/www.site.com
             */

            Spark.secure(
                    config.getString("keyStore_path"),
                    config.getString("keyStore_password"), null, null);
            LogManager.LOGGER.info("(Web) Enabled ssl");
        }

        Spark.webSocket("/socket", SocketServer.class);

        Spark.get("/", new HomePage(), templateEngine);
        Spark.get("/leaderboard", new LeaderBoardPage(), templateEngine);
        Spark.get("/play", new PlayPage(), templateEngine);
        Spark.get("/account", new AccountPage(), templateEngine);

        Spark.post("/register", new RegisterRoute());
        Spark.post("/login", new LoginRoute());
        Spark.get("/logout", new LogoutRoute());
        Spark.post("/change_password", new ChangePasswordRoute());
        Spark.get("/server_info", new ServerInfoRoute());

        Spark.after((request, response) -> response.header("Content-Encoding", "gzip"));
    }

}
