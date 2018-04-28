package net.simon987.server.web;

import net.simon987.server.GameServer;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.TemplateViewRoute;

import java.util.HashMap;
import java.util.Map;

public class AccountPage implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request request, Response response) {
        Map<String, Object> model = new HashMap<>();
        model.put("session", request.session());


        if (request.session().attribute("username") != null) {
            model.put("user", GameServer.INSTANCE.getGameUniverse().getUser(request.session().attribute("username")));
        }

        return new ModelAndView(model, "account.vm");
    }
}
