package net.simon987.pluginradioactivecloud;

import net.simon987.server.GameServer;
import net.simon987.server.assembly.Status;
import net.simon987.server.game.objects.ControllableUnit;
import org.bson.Document;

//WORK IN PROGRESS
public class RadiationDetector extends HardwareModule {

    //Find last HWID used (0x000F) + 1
    public static final char DEFAULT_ADDRESS = 0x0010;
    static final char HWID = 0x0010;

    private static final int RADIATION_POLL_ALPHA = 1;
    private static final int RADIATION_POLL_BETA = 2;
    private static final int RADIATION_POLL_GAMMA = 3;

    //Potentially add cost??

    public RadiationDetector() {

    }

    public RadiationDetector(Document document, ControllableUnit cubot) {
        super(document, cubot);
    }

    @Override
    public char getId() {
        return HWID;
    }

	@Override
	public void handleInterrupt(Status status) {
        int a = getCpu().getRegisterSet().getRegister("A").getValue();
        
        switch(a){
            case RADIATION_POLL_ALPHA: //Set register B to alpha level
                
                break;

            case RADIATION_POLL_BETA: //Set register B to beta level
                
                break;

            case RADIATION_POLL_GAMMA: //Set register B to gamma level
                
                break;

            default:
                break;
		}

    }
}