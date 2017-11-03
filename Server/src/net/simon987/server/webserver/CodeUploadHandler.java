package net.simon987.server.webserver;

import net.simon987.server.GameServer;
import net.simon987.server.assembly.Assembler;
import net.simon987.server.assembly.AssemblyResult;
import net.simon987.server.logging.LogManager;
import org.json.simple.JSONObject;

public class CodeUploadHandler implements MessageHandler {

    @Override
    public void handle(OnlineUser user, JSONObject json) {
        if(json.get("t").equals("uploadCode")){

            LogManager.LOGGER.info("(WS) Code upload from " + user.getUser().getUsername());

            //TODO Should we wait at the end of the tick to modify the CPU ?
            user.getUser().setUserCode((String)json.get("code"));

            AssemblyResult ar = new Assembler(user.getUser().getCpu().getInstructionSet(),
                    user.getUser().getCpu().getRegisterSet(),
                    GameServer.INSTANCE.getConfig()).parse(user.getUser().getUserCode());

            user.getUser().getCpu().getMemory().clear();

            //Write assembled code to mem
            user.getUser().getCpu().getMemory().write((char) ar.origin, ar.bytes, ar.bytes.length);
            user.getUser().getCpu().setCodeSegmentOffset(ar.origin);

        }
    }
}
