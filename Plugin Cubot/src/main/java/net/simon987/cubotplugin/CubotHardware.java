package net.simon987.cubotplugin;

import net.simon987.server.assembly.CpuHardware;
import net.simon987.server.game.objects.ControllableUnit;
import org.bson.Document;

public abstract class CubotHardware extends CpuHardware {

    protected Cubot cubot;

    public CubotHardware(Document document, ControllableUnit cubot) {
        this.cubot = (Cubot) cubot;
    }

    public CubotHardware(Cubot cubot) {
        this.cubot = cubot;
    }

    @Override
    public Document mongoSerialise() {
        Document document = new Document();

        document.put("type", getClass().getCanonicalName());
        return document;
    }
}
