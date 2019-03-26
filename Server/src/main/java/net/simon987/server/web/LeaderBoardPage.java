package net.simon987.server.web;

import net.simon987.server.GameServer;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.TemplateViewRoute;

import java.util.HashMap;
import java.util.Map;

public class LeaderBoardPage implements TemplateViewRoute {

    @Override
    public ModelAndView handle(Request request, Response response) {
        Map<String, Object> model = new HashMap<>(5);
        model.put("session", request.session());
        model.put("stats", GameServer.INSTANCE.getUserStatsHelper().getTopNSetLength("completedVaults", 25));
        model.put("stats", GameServer.INSTANCE.getUserStatsHelper().getTopNSetLength("deathCount", 25));
        model.put("stats", GameServer.INSTANCE.getUserStatsHelper().getTopNSetLength("totalExecutionTime", 25));
        model.put("stats", GameServer.INSTANCE.getUserStatsHelper().getTopNSetLength("walkDistance", 25));
        return new ModelAndView(model, "leaderboard.vm");
    }
}