package net.simon987.server.web;

import net.simon987.server.GameServer;
import net.simon987.server.logging.LogManager;
import net.simon987.server.user.RegistrationException;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;

public class RegisterRoute implements Route {

    @Override
    public ModelAndView handle(Request request, Response response) {
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
    }
}
