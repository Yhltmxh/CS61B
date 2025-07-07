package byow.lab12;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;

/**
 * @description: 六边形世界示例
 * @author: 杨怀龙
 * @create: 2025-07-07 19:29
 **/
public class HexWorldDemo {
    public static void main(String[] args) {
        HexWorld hexWorld = new HexWorld(50, 60, 4, 194346);
        TERenderer ter = new TERenderer();
        ter.initialize(hexWorld.getWidth(), hexWorld.getHeight());
        ter.renderFrame(hexWorld.getWorld());
    }
}
