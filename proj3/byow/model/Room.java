package byow.model;

import byow.TileEngine.Tileset;

/**
 * @description: 房间
 * @author: 杨怀龙
 * @create: 2025-07-08 19:20
 **/
public class Room extends RectArea {

    public Room(int width, int height, int x, int y) {
        super(width, height, x, y, Tileset.WALL, Tileset.FLOOR);
    }
}
