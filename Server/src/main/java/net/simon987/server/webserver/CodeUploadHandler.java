package net.simon987.server.webserver;

import net.simon987.server.GameServer;
import net.simon987.server.assembly.Assembler;
import net.simon987.server.assembly.AssemblyResult;
import net.simon987.server.logging.LogManager;
import org.json.simple.JSONObject;

public class CodeUploadHandler implements MessageHandler {

    @Override
    public void handle(OnlineUser user, JSONObject json) {
        if (json.get("t").equals("uploadCode")) {

            LogManager.LOGGER.fine("(WS) Code upload from " + user.getUser().getUsername());

            if (user.isGuest()) {
                //Ignore

            } else {
                //TODO Should we wait at the end of the tick to modify the CPU ?
                user.getUser().setUserCode((String) json.get("code"));

                if (user.getUser().getUserCode() != null) {
                    AssemblyResult ar = new Assembler(user.getUser().getCpu().getInstructionSet(),
                            user.getUser().getCpu().getRegisterSet(),
                            GameServer.INSTANCE.getConfig()).parse(user.getUser().getUserCode());

                    user.getUser().getCpu().getMemory().clear();

                    //Write assembled code to mem
                    char[] assembledCode = ar.getWords();

                    user.getUser().getCpu().getMemory().write((char) ar.origin, assembledCode, 0, assembledCode.length);
                    user.getUser().getCpu().setCodeSectionOffset(ar.getCodeSectionOffset());

                    //Clear keyboard buffer
                    if (user.getUser().getControlledUnit() != null &&
                            user.getUser().getControlledUnit().getKeyboardBuffer() != null) {
                        user.getUser().getControlledUnit().getKeyboardBuffer().clear();
                    }

                    //Clear registers
                    user.getUser().getCpu().getRegisterSet().clear();

                    JSONObject response = new JSONObject();
                    response.put("t", "codeResponse");
                    response.put("bytes", ar.bytes.length);
                    response.put("exceptions", ar.exceptions.size());

                    user.getWebSocket().send(response.toJSONString());
                }
            }


        }
    }
}
