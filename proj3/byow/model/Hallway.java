package byow.model;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

/**
 * @description: 走廊
 * @author: 杨怀龙
 * @create: 2025-07-08 19:20
 **/
public class Hallway extends RectArea {

    public Hallway(int x, int y) {
        super(3, 3, x, y, Tileset.WALL, Tileset.FLOOR);
    }
}
