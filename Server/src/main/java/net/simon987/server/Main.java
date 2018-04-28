package net.simon987.server;

import net.simon987.server.logging.LogManager;
import net.simon987.server.user.RegistrationException;
import net.simon987.server.web.AlertMessage;
import net.simon987.server.web.AlertType;
import net.simon987.server.websocket.SocketServer;
import org.apache.velocity.app.VelocityEngine;
import spark.ModelAndView;
import spark.Spark;
import spark.template.velocity.VelocityTemplateEngine;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class Main {
    public static void main(String[] args) {

        ServerConfiguration config = new ServerConfiguration("config.properties");
        LogManager.initialize(config);

        //Load
        GameServer.INSTANCE.load();

        (new Thread(GameServer.INSTANCE)).start();

        //TEST ---------------------------
        Properties properties = new Properties();
        properties.setProperty("file.resource.loader.path", "templates/");
        VelocityTemplateEngine templateEngine = new VelocityTemplateEngine(new VelocityEngine(properties));
        //--

        //Websocket
        Spark.webSocket("/socket", SocketServer.class);

        Spark.staticFiles.externalLocation("static");

        Spark.get("/", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("session", request.session());

            return new ModelAndView(model, "home.vm");
        }, templateEngine);

        Spark.get("/leaderboard", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("session", request.session());

            return new ModelAndView(new HashMap<>(), "leaderboard.vm");
        }, templateEngine);

        Spark.get("/play", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("session", request.session());

            return new ModelAndView(model, "play.vm");
        }, templateEngine);

        Spark.get("/account", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("session", request.session());


            if (request.session().attribute("username") != null) {
                model.put("user", GameServer.INSTANCE.getGameUniverse().getUser(request.session().attribute("username")));
            }

            return new ModelAndView(model, "account.vm");
        }, templateEngine);

        Spark.post("/register", (request, response) -> {
            String username = request.queryParams("username");
            String password = request.queryParams("password");

            if (username != null && password != null) {
                try {
                    GameServer.INSTANCE.getUserManager().registerUser(username, password);

                    AlertMessage[] messages = {new AlertMessage("Successfully registered", AlertType.SUCCESS)};
                    request.session().attribute("messages", messages);
                    request.session().attribute("username", username);

                    LogManager.LOGGER.fine("(Web) " + username + " registered " + request.ip());
                } catch (RegistrationException e) {
                    AlertMessage[] messages = {new AlertMessage(e.getMessage(), AlertType.DANGER)};
                    request.session().attribute("messages", messages);
                }
            }

            response.redirect("/account");
            return null;
        });

        Spark.post("/login", (request, response) -> {
            String username = request.queryParams("username");
            String password = request.queryParams("password");

            if (username != null && password != null) {
                if (GameServer.INSTANCE.getUserManager().validateUser(username, password)) {
                    AlertMessage[] messages = {new AlertMessage("Logged in as " + username, AlertType.SUCCESS)};
                    request.session().attribute("messages", messages);
                    request.session().attribute("username", username);

                    LogManager.LOGGER.fine("(Web) " + username + " logged in");
                } else {
                    AlertMessage[] messages = {new AlertMessage("Invalid username or password", AlertType.DANGER)};
                    request.session().attribute("messages", messages);
                }
            }

            response.redirect("/account");
            return null;
        });

        Spark.get("logout", (request, response) -> {

            AlertMessage[] messages = {new AlertMessage("Logged out", AlertType.INFO)};
            request.session().attribute("messages", messages);
            request.session().removeAttribute("username");

            response.redirect("/account");
            return null;
        });

        Spark.post("change_password", (request, response) -> {

            String username = request.session().attribute("username");
            String currentPassword = request.queryParams("password");
            String newPassword = request.queryParams("new_password");
            String newPasswordRepeat = request.queryParams("new_password_repeat");

            if (newPassword.equals(newPasswordRepeat)) {

                if (username != null && GameServer.INSTANCE.getUserManager().validateUser(username, currentPassword)) {

                    try {
                        GameServer.INSTANCE.getUserManager().changePassword(username, newPassword);
                        AlertMessage[] messages = {new AlertMessage("Changed password", AlertType.SUCCESS)};
                        request.session().attribute("messages", messages);
                    } catch (RegistrationException e) {
                        AlertMessage[] messages = {new AlertMessage(e.getMessage(), AlertType.DANGER)};
                        request.session().attribute("messages", messages);
                    }

                } else {
                    AlertMessage[] messages = {new AlertMessage("Invalid password", AlertType.DANGER)};
                    request.session().attribute("messages", messages);
                }
            } else {
                AlertMessage[] messages = {new AlertMessage("Passwords did not match", AlertType.DANGER)};
                request.session().attribute("messages", messages);
            }


            response.redirect("/account");
            return null;
        });

        Spark.after((request, response) -> response.header("Content-Encoding", "gzip"));
    }


}
