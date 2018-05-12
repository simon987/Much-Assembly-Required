package net.simon987.server.web;

import net.simon987.server.GameServer;
import net.simon987.server.logging.LogManager;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoginRoute implements Route {
    @Override
    public ModelAndView handle(Request request, Response response) {
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
    }
}
