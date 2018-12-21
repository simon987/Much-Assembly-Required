package net.simon987.server.web;

import net.simon987.server.GameServer;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.TemplateViewRoute;

import java.util.HashMap;
import java.util.Map;

public class PlayPage implements TemplateViewRoute {

    @Override
    public ModelAndView handle(Request request, Response response) {

        String autoLogin = GameServer.INSTANCE.getConfig().getString("autologin");
        if (autoLogin != null && !autoLogin.equals("")) {
            AlertMessage[] messages = {new AlertMessage("Logged in as " + autoLogin, AlertType.SUCCESS)};
            request.session().attribute("messages", messages);
            request.session().attribute("username", autoLogin);
        }

        Map<String, Object> model = new HashMap<>(1);
        model.put("session", request.session());
        model.put("gamePageTitle", GameServer.INSTANCE.getConfig().getString("server_name"));

        return new ModelAndView(model, "play.vm");
    }
}
