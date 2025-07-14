package byow.Core;

import byow.TileEngine.TETile;

/**
 * @description: 第一阶段测试
 * @author: 杨怀龙
 * @create: 2025-07-08 17:04
 **/
public class Test {
    public static void main(String[] args) {
        Engine engine = new Engine();
        TETile[][] world = engine.interactWithInputString("LD:Q");
        int xl = world.length, yl = world[0].length;
        for (int i = yl - 1; i >= 0; i--) {
            for (int j = 0; j < xl; j++) {
                System.out.print(world[j][i].character());
                System.out.print(" ");
            }
            System.out.println();
        }
    }
}
