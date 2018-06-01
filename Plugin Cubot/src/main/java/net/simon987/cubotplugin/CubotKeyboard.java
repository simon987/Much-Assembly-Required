package net.simon987.cubotplugin;

import net.simon987.server.assembly.Status;
import net.simon987.server.game.objects.ControllableUnit;
import org.bson.Document;

public class CubotKeyboard extends CubotHardwareModule {

    public static final int DEFAULT_ADDRESS = 4;

    private static final int KEYBOARD_CLEAR_BUFFER = 0;
    private static final int KEYBOARD_FETCH_KEY = 1;

    /**
     * Hardware ID (Should be unique)
     */
    public static final char HWID = 0x0004;

    public CubotKeyboard(Cubot cubot) {
        super(cubot);
    }

    public CubotKeyboard(Document document, ControllableUnit cubot) {
        super(document, cubot);
    }

    @Override
    public char getId() {
        return HWID;
    }

    @Override
    public void handleInterrupt(Status status) {

        int a = getCpu().getRegisterSet().getRegister("A").getValue();

        if (a == KEYBOARD_CLEAR_BUFFER) {

            cubot.clearKeyboardBuffer();

        } else if (a == KEYBOARD_FETCH_KEY) {
            //pop
            int key = 0;
            if (cubot.getKeyboardBuffer().size() > 0) {
                key = cubot.getKeyboardBuffer().get(0);
                cubot.getKeyboardBuffer().remove(0);
            }

            getCpu().getRegisterSet().getRegister("B").setValue(key);

        }

    }
}
