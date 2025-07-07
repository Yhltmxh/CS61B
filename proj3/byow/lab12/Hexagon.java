package byow.lab12;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.HashMap;
import java.util.Map;


/**
 * @description: 六边形区域
 * @author: 杨怀龙
 * @create: 2025-07-07 16:35
 **/
public class Hexagon {

    /**
     * 瓦片类型
     */
    private TETile teTile = Tileset.WALL;

    /**
     * 临近的六边形区域
     */
    private final Hexagon[] nearHexagon = new Hexagon[6];

    /**
     * 六边形区域x坐标（左上角的位置）
     */
    private int x;

    /**
     * 六边形区域y坐标（左上角的位置）
     */
    private int y;

    public Hexagon(int x, int y) {
        this.x = x;
        this.y = y;

    }

    public Hexagon(TETile teTile, int x, int y) {
        this.teTile = teTile;
        this.x = x;
        this.y = y;
    }


    public TETile getTeTile() {
        return teTile;
    }

    public void setTeTile(TETile teTile) {
        this.teTile = teTile;
    }

    public Hexagon getNearHexagon(int index) {
        return nearHexagon[index];
    }

    public void setNearHexagon(int index, Hexagon hexagon) {
        this.nearHexagon[index] = hexagon;
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
