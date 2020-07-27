package net.simon987.mar.cubot;

import net.simon987.mar.server.assembly.HardwareModule;
import net.simon987.mar.server.game.objects.ControllableUnit;
import org.bson.Document;

public abstract class CubotHardwareModule extends HardwareModule {

    protected Cubot cubot;

    public CubotHardwareModule(Document document, ControllableUnit cubot) {
        this.cubot = (Cubot) cubot;
    }

    public CubotHardwareModule(Cubot cubot) {
        this.cubot = cubot;
    }

}
