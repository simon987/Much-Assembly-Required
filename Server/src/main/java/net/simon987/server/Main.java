package net.simon987.server;

import net.simon987.server.logging.LogManager;
import net.simon987.server.webserver.SocketServer;
import org.apache.velocity.app.VelocityEngine;
import spark.ModelAndView;
import spark.Spark;
import spark.template.velocity.VelocityTemplateEngine;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class Main {
    public static void main(String[] args) {

        ServerConfiguration config = new ServerConfiguration("config.properties");
        LogManager.initialize(config);

        //Load
        GameServer.INSTANCE.load();

        SocketServer socketServer = new SocketServer(new InetSocketAddress(config.getString("webSocket_host"),
                config.getInt("webSocket_port")), config);

        GameServer.INSTANCE.setSocketServer(socketServer);

        (new Thread(socketServer)).start();
        (new Thread(GameServer.INSTANCE)).start();

        //TEST ---------------------------
        Properties properties = new Properties();
        properties.setProperty("file.resource.loader.path", "templates/");
        VelocityTemplateEngine templateEngine = new VelocityTemplateEngine(new VelocityEngine(properties));
        //--

        Spark.staticFiles.externalLocation("static");

        Spark.get("/", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            System.out.println((String) request.session().attribute("user"));

            return new ModelAndView(model, "home.vm");
        }, templateEngine);

        Spark.get("/leaderboard", (request, response) -> {
            return new ModelAndView(new HashMap<>(), "leaderboard.vm");
        }, templateEngine);

        Spark.get("/play", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            return new ModelAndView(model, "play.vm");
        }, templateEngine);

        Spark.get("/account", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            return new ModelAndView(model, "account.vm");
        }, templateEngine);

        Spark.after((request, response) -> response.header("Content-Encoding", "gzip"));
    }
}
