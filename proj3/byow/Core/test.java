package byow.Core;

import byow.TileEngine.TERenderer;
import byow.lab12.HexWorld;

/**
 * @description: 第一阶段测试
 * @author: 杨怀龙
 * @create: 2025-07-08 17:04
 **/
public class test {
    public static void main(String[] args) {
        Engine engine = new Engine();
        TERenderer ter = new TERenderer();
        ter.initialize(Engine.WIDTH, Engine.HEIGHT);
        ter.renderFrame(engine.interactWithInputString("N20250711S"));
    }
}
