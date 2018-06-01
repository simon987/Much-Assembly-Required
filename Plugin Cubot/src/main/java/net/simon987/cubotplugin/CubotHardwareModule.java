package net.simon987.cubotplugin;

import net.simon987.server.assembly.HardwareModule;
import net.simon987.server.game.objects.ControllableUnit;
import org.bson.Document;

public abstract class CubotHardwareModule extends HardwareModule {

    protected Cubot cubot;

    public CubotHardwareModule(Document document, ControllableUnit cubot) {
        this.cubot = (Cubot) cubot;
    }

    public CubotHardwareModule(Cubot cubot) {
        this.cubot = cubot;
    }

    @Override
    public Document mongoSerialise() {
        Document document = new Document();

        document.put("type", getClass().getCanonicalName());
        return document;
    }
}
