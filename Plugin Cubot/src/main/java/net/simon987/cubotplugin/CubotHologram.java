package net.simon987.cubotplugin;

import net.simon987.server.assembly.Status;
import net.simon987.server.game.objects.ControllableUnit;
import org.bson.Document;
import org.json.simple.JSONObject;

public class CubotHologram extends CubotHardwareModule {

    /**
     * Hardware ID (Should be unique)
     */
    static final char HWID = 0x0009;
    public static final int DEFAULT_ADDRESS = 9;

    private static final int HOLO_CLEAR = 0;
    private static final int HOLO_DISPLAY_HEX = 1;
    private static final int HOLO_DISPLAY_STRING = 2;
    private static final int HOLO_DISPLAY_DEC = 3;
    private static final int HOLO_DISPLAY_COLOR = 4;

    private static final int STR_MAX_LEN = 8;

    private int displayValue = 0;
    private String displayString = "";
    private HologramMode mode = HologramMode.CLEARED;
    private HologramMode lastMode = HologramMode.CLEARED;

    /**
     * Hologram color code. Format is handled by the client
     */
    private int displayColor = 0;

    public CubotHologram(Cubot cubot) {
        super(cubot);
    }

    public CubotHologram(Document document, ControllableUnit cubot) {
        super(document, cubot);

        displayValue = document.getInteger("value");
        displayColor = document.getInteger("color");
        displayString = document.getString("string");
        mode = HologramMode.values()[document.getInteger("mode")];
    }

    @Override
    public void handleInterrupt(Status status) {

        char a = getCpu().getRegisterSet().getRegister("A").getValue();

        if (a == HOLO_CLEAR) {
            mode = HologramMode.CLEARED;
        } else if (a == HOLO_DISPLAY_HEX) {
            displayValue = getCpu().getRegisterSet().getRegister("B").getValue();
            mode = HologramMode.HEX;
        } else if (a == HOLO_DISPLAY_STRING) {
            char x = getCpu().getRegisterSet().getRegister("X").getValue();
            //Display zero-terminated string starting at X (max 8 chars)

            StringBuilder holoString = new StringBuilder();

            for (int i = 0; i < STR_MAX_LEN; i++) {

                char nextChar = (char) getCpu().getMemory().get(x + i);

                if (nextChar != 0) {
                    holoString.append((char) getCpu().getMemory().get(x + i));
                } else {
                    break;
                }
            }

            displayString = holoString.toString();
            mode = HologramMode.STRING;
        } else if (a == HOLO_DISPLAY_DEC) {
            //Display decimal number
            displayValue = getCpu().getRegisterSet().getRegister("B").getValue();
            mode = HologramMode.DEC;

        } else if (a == HOLO_DISPLAY_COLOR) {

            if (cubot.spendEnergy(4)) {
                int b = getCpu().getRegisterSet().getRegister("B").getValue();
                int c = getCpu().getRegisterSet().getRegister("C").getValue();

                displayColor = (c | (b << 16)); //B:C
            }
        }

    }

    @Override
    public char getId() {
        return HWID;
    }

    @Override
    public Document mongoSerialise() {
        Document document = super.mongoSerialise();

        document.put("color", displayColor);
        document.put("value", displayValue);
        document.put("string", displayString);
        document.put("mode", lastMode.ordinal());

        return document;
    }

    @Override
    public JSONObject debugJsonSerialise() {
        JSONObject json = jsonSerialise();

        json.put("lastmode", mode);

        return json;
    }

    @Override
    public JSONObject jsonSerialise() {
        JSONObject json = new JSONObject();

        json.put("color", displayColor);
        json.put("value", displayValue);
        json.put("string", displayString);
        json.put("mode", lastMode.ordinal());

        return json;
    }

    private enum HologramMode {
        /**
         * Display nothing
         */
        CLEARED,
        /**
         * Display value as hexadecimal in format 0x0000
         */
        HEX,
        /**
         * Display string
         */
        STRING,
        /**
         * Display value as decimal
         */
        DEC
    }

    @Override
    public void reset() {
        displayValue = 0;
        displayColor = 0;
        displayString = "";
        mode = HologramMode.CLEARED;
    }

    @Override
    public void update() {
        lastMode = mode;
        mode = HologramMode.CLEARED;
    }
}
