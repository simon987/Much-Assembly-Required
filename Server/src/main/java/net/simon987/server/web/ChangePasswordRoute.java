package net.simon987.server.web;

import net.simon987.server.GameServer;
import net.simon987.server.user.RegistrationException;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;

public class ChangePasswordRoute implements Route {

    @Override
    public ModelAndView handle(Request request, Response response) {
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
    }
}
