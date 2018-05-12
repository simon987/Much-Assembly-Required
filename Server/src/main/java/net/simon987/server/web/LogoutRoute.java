package net.simon987.server.web;

import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;

public class LogoutRoute implements Route {
    @Override
    public ModelAndView handle(Request request, Response response) {
        AlertMessage[] messages = {new AlertMessage("Logged out", AlertType.INFO)};
        request.session().attribute("messages", messages);
        request.session().removeAttribute("username");

        response.redirect("/account");
        return null;
    }
}
