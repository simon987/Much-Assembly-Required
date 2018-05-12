package net.simon987.cubotplugin;

import net.simon987.server.GameServer;
import net.simon987.server.assembly.CpuHardware;
import org.bson.Document;

public abstract class CubotHardware extends CpuHardware {

    protected Cubot cubot;

    public CubotHardware(Document document) {
        this.cubot = (Cubot) GameServer.INSTANCE.getGameUniverse().getObject((long) document.get("cubot"));
    }

    public CubotHardware(Cubot cubot) {
        this.cubot = cubot;
    }

    @Override
    public Document mongoSerialise() {
        Document document = new Document();

        document.put("type", getClass().getCanonicalName());
        document.put("cubot", cubot.getObjectId());
        return document;
    }
}
