package net.simon987.server.game;

import net.simon987.server.io.JSONSerialisable;
import org.json.simple.JSONObject;

/**
 * Represents a game effect in a World (e.g. Particles made when digging, Error animation, Attack effects etc..)
 * <br>
 * These effects are purely visual and could be changed or ignored by the client
 */
public class GameEffect implements JSONSerialisable {


    /**
     * Type of the effect
     */
    private EffectType type;

    private int x;

    private int y;

    public GameEffect(EffectType type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
    }

    @Override
    public JSONObject serialise() {

        JSONObject json = new JSONObject();

        json.put("x", x);
        json.put("y", y);
        json.put("type", type);

        return json;
    }

    public EffectType getType() {
        return type;
    }

    public void setType(EffectType type) {
        this.type = type;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
