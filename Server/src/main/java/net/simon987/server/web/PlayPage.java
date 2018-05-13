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
        Map<String, Object> model = new HashMap<>(1);
        model.put("session", request.session());
        model.put("gamePageTitle", GameServer.INSTANCE.getConfig().getString("server_name"));

        return new ModelAndView(model, "play.vm");
    }
}
