package byow.model;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.io.Serializable;

/**
 * @description: 玩家化身
 * @author: 杨怀龙
 * @create: 2025-07-14 16:34
 **/
public class Avatar implements Serializable {

    private final TETile tile = Tileset.AVATAR;

    private int x;

    private int y;

    public Avatar(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setCoordinate(int a, int b) {
        this.x = a;
        this.y = b;
    }

    public TETile getTile() {
        return tile;
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
