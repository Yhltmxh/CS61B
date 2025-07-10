package byow.model;

import byow.Core.RandomUtils;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;

/**
 * @description: 世界
 * @author: 杨怀龙
 * @create: 2025-07-09 16:19
 **/
public class World {

    /**
     * 世界宽度
     */
    private final int width;

    /**
     * 世界高度
     */
    private final int height;

    /**
     * 世界生成种子
     */
    private final long seed;

    /**
     * 世界随机数生成器
     */
    private final Random random;

    /**
     * 瓦片世界数组
     */
    private final TETile[][] worldMap;

    public World(int width, int height, long seed) {
        this.width = width;
        this.height = height;
        this.seed = seed;
        random = new Random(seed);
        worldMap = new TETile[width][height];
        initWorldMap();
        generateWorld();
    }

    /**
     * 初始化世界数组
     */
    private void initWorldMap() {
        for (int i = 0; i < worldMap.length; i ++) {
            for (int j = 0; j < worldMap[0].length; j ++) {
                worldMap[i][j] = Tileset.NOTHING;
            }
        }
    }

    /**
     * 世界生成
     */
    private void generateWorld() {
        // 随机生成一个房间作为源点
        int originWidth = RandomUtils.uniform(random, 4, 8);
        int originHeight = RandomUtils.uniform(random, 4, 8);
        int ox = RandomUtils.uniform(random, width - originWidth);
        int oy = RandomUtils.uniform(random, originHeight, height);
        Room origin = new Room(originWidth, originHeight, ox, oy);
        drawRectArea(origin);
        // 由源点bfs生成其他区域
        Queue<RectArea> q = new ArrayDeque<>();
        q.offer(origin);
        while (!q.isEmpty()) {
            RectArea t = q.poll();
            RectArea[] nearArea = t.getNearArea();
            for (int i = 0; i < nearArea.length; i ++) {
                if (nearArea[i] == null) {
                    RectArea newArea = extendRectArea(t, i);
                    if (checkArea(newArea) && checkPath(t, newArea, i)) {
                        drawRectArea(newArea);
                        connectRectArea(t, newArea, i);
                        q.offer(newArea);
                    }
                }
            }
        }
    }


    /**
     * 根据给定区域在给定朝向构建临近新区域
     * @param source 给定区域
     * @param toward 给定朝向
     * @return 区域对象
     */
    private RectArea extendRectArea(RectArea source, int toward) {
        RectArea target = null;
        if (source instanceof Room) {
            target = buildHallway(source, toward);
        } else if (source instanceof Hallway) {
            if (RandomUtils.uniform(random, 2) == 0) {
                target = buildRoom(source, toward);
            } else {
                target = buildHallway(source, toward);
            }
        }
        return target;
    }


    /**
     * 根据给定区域构建房间
     * @param source 给定区域
     * @param toward 指定朝向
     * @return 房间对象
     */
    private Room buildRoom(RectArea source, int toward) {
        // 与房间相邻的必为走廊，故source必须为Hallway
        if (!(source instanceof Hallway)) {
            return null;
        }
        int sx = source.getX(), sy = source.getY();
        int tx = sx, ty = sy, tw = 0, th = 0;
        switch(toward) {
            case 0 -> {
                tx = RandomUtils.uniform(random, sx - 5, sx);
                ty = RandomUtils.uniform(random, sy + 5, sy + 10);
                tw = RandomUtils.uniform(random, Math.min(sx + 5, width) - tx);
                th = RandomUtils.uniform(random, Math.max(3, ty - sy));
            }
            case 1 -> {
                tx = RandomUtils.uniform(random, sx + 1, Math.min(sx + 10, width));
                ty = RandomUtils.uniform(random, sy, Math.min(sy + 5, height));
                tw = RandomUtils.uniform(random, Math.min(tx + 10, width) - tx);
                th = RandomUtils.uniform(random, Math.max(3, ty));
            }
            case 2 -> {
                tx = RandomUtils.uniform(random, sx - 5, sx);
                ty = RandomUtils.uniform(random, sy - 10, sy - 5);
                tw = RandomUtils.uniform(random, Math.min(sx + 5, width) - tx);
                th = RandomUtils.uniform(random, Math.max(3, ty));
            }
            case 3 -> {
                tx = RandomUtils.uniform(random, sx - 10, sx - 1);
                ty = RandomUtils.uniform(random, sy, Math.min(sy + 5, height));
                tw = RandomUtils.uniform(random, sx - tx);
                th = RandomUtils.uniform(random, Math.max(3, ty));
            }
        }
        return new Room(tw, th, tx, ty);
    }


    /**
     * 根据给定区域构建走廊
     * @param source 给定区域
     * @param toward 给定朝向
     * @return 走廊对象
     */
    private Hallway buildHallway(RectArea source, int toward) {
        int sx = source.getX(), sy = source.getY();
        int sw = source.getWidth(), sh = source.getHeight();
        int tx = sx, ty = sy;
        if (source instanceof Room) {
            switch(toward) {
                case 0 -> {
                    tx = RandomUtils.uniform(random, sx + 1, sx + sw);
                    ty = RandomUtils.uniform(random, sy + 5, sy + 10);
                }
                case 1 -> {
                    tx = RandomUtils.uniform(random, sx + sw + 4, sx + sw + 9);
                    ty = RandomUtils.uniform(random, sy - sh + 2, sy);
                }
                case 2 -> {
                    tx = RandomUtils.uniform(random, sx + 1, sx + sw);
                    ty = RandomUtils.uniform(random, sy - sh - 9, sy - sh - 4);
                }
                case 3 -> {
                    tx = RandomUtils.uniform(random, sx - 10, sx - 5);
                    ty = RandomUtils.uniform(random, sy - sh + 2, sy);
                }
            }
        } else if (source instanceof Hallway) {
            switch(toward) {
                case 0 -> ty = RandomUtils.uniform(random, sy + 5, sy + 10);
                case 1 -> tx = RandomUtils.uniform(random, sx + 5, sx + 10);
                case 2 -> ty = RandomUtils.uniform(random, sy - 10, sy - 5);
                case 3 -> tx = RandomUtils.uniform(random, sx - 10, sx - 5);
            }
        }
        return new Hallway(tx, ty);
    }


    /**
     * 检测给定区域是否合法
     * @param rectArea 给定区域
     * @return true：合法，false：不合法
     */
    private boolean checkArea(RectArea rectArea) {
        int x = rectArea.getX(), y = rectArea.getY();
        int w = rectArea.getWidth(), h = rectArea.getHeight();
        if (rectArea instanceof Room && (w < 3 || h < 3)) {
            return false;
        }
        if (rectArea instanceof Hallway) {
            x -= 1; y += 1;
        }
        int dx = x + w - 1, dy = y - h + 1;
        if (x < 0 || x >= width || dx >= width || dy < 0 || dy >= height || y >= height) {
            return false;
        }
        for (int i = x; i <= dx; i ++) {
            for (int j = dy; j <= y; j ++) {
                if (!worldMap[i][j].equals(Tileset.NOTHING)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkPath(RectArea source, RectArea target, int toward) {
        // todo: 优化结构
        int x = source.getX(), y = source.getY(), tx = target.getX(), ty = target.getY();
        switch(toward) {
            case 0 -> {
                if (source instanceof Hallway && target instanceof Room) {
                    y += 1; ty = ty - target.getHeight() + 1;
                } else if (source instanceof Room) {
                    x = tx; ty -= 1;
                } else {
                    y += 1; ty -= 1;
                }
                for (int i = y + 1; i < ty; i ++) {
                    if (!worldMap[x][i].equals(Tileset.NOTHING) || !worldMap[x - 1][i].equals(Tileset.NOTHING)
                            || !worldMap[x + 1][i].equals(Tileset.NOTHING)) {
                        return false;
                    }
                }
            }
            case 1 -> {
                if (source instanceof Hallway && target instanceof Room) {
                    x += 1;
                } else if (source instanceof Room) {
                    y = ty; x = x + source.getWidth() - 1; tx -= 1;
                } else {
                    x += 1; tx -= 1;
                }
                for (int i = x + 1; i < tx; i ++) {
                    if (!worldMap[i][y].equals(Tileset.NOTHING) || !worldMap[i][y + 1].equals(Tileset.NOTHING)
                            || !worldMap[i][y - 1].equals(Tileset.NOTHING)) {
                        return false;
                    }
                }
            }
            case 2 -> {
                if (source instanceof Hallway && target instanceof Room) {
                    y -= 1;
                } else if (source instanceof Room) {
                    x = tx; y = y - source.getHeight() + 1; ty += 1;
                } else {
                    y -= 1; ty += 1;
                }
                for (int i = y - 1; i > ty; i --) {
                    if (!worldMap[x][i].equals(Tileset.NOTHING) || !worldMap[x - 1][i].equals(Tileset.NOTHING)
                            || !worldMap[x + 1][i].equals(Tileset.NOTHING)) {
                        return false;
                    }
                }
            }
            case 3 -> {
                if (source instanceof Hallway && target instanceof Room) {
                    x -= 1; tx = tx + target.getWidth() - 1;
                } else if (source instanceof Room) {
                    y = ty; tx += 1;
                } else {
                    x -= 1; tx += 1;
                }
                for (int i = x - 1; i > tx; i --) {
                    if (!worldMap[i][y].equals(Tileset.NOTHING) || !worldMap[i][y + 1].equals(Tileset.NOTHING)
                            || !worldMap[i][y - 1].equals(Tileset.NOTHING)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 绘制给定区域
     * @param rectArea 给定处于
     */
    private void drawRectArea(RectArea rectArea) {
        int x = rectArea.getX(), y = rectArea.getY();
        int w = rectArea.getWidth(), h = rectArea.getHeight();
        if (rectArea instanceof Hallway) {
            x -= 1; y += 1;
        }
        int dx = x + w - 1, dy = y - h + 1;
        for (int i = x; i <= dx; i ++) {
            for (int j = dy; j <= y; j ++) {
                if (i == x || i == dx || j == y || j == dy) {
                    worldMap[i][j] = rectArea.getWall();
                } else {
                    worldMap[i][j] = rectArea.getFloor();
                }
            }
        }
    }


    /**
     * 连接两个区域
     * @param source 源区域
     * @param target 目标区域
     * @param toward 源区域相对目标区域的朝向
     */
    private void connectRectArea(RectArea source, RectArea target, int toward) {
        if (!(source instanceof Hallway) && !(target instanceof Hallway)) {
            throw new RuntimeException("given area have not Hallway");
        }
        source.getNearArea()[toward] = target;
//        int targetToward = switch (toward) {
//            case 0, 2 -> 2 - toward;
//            case 1, 3 -> 4 - toward;
//            default -> -1;
//        };
//        target.getNearArea()[targetToward] = source;
        if (toward == 0 || toward == 2) {
            int x = source.getX(), t ;
            if (target instanceof Hallway) {
                x = target.getX();
            }
        }
        int x = source.getX(), y = source.getY(), tx = target.getX(), ty = target.getY();
        switch(toward) {
            case 0 -> {
                if (source instanceof Hallway && target instanceof Room) {
                    y += 1; ty = ty - target.getHeight() + 1;
                } else if (source instanceof Room) {
                    x = tx; ty -= 1;
                } else {
                    y += 1; ty -= 1;
                }
                for (int i = y; i <= ty; i ++) {
                    worldMap[x][i] = Tileset.FLOOR;
                    worldMap[x - 1][i] = Tileset.WALL;
                    worldMap[x + 1][i] = Tileset.WALL;
                }
            }
            case 1 -> {
                if (source instanceof Hallway && target instanceof Room) {
                    x += 1;
                } else if (source instanceof Room) {
                    y = ty; x = x + source.getWidth() - 1; tx -= 1;
                } else {
                    x += 1; tx -= 1;
                }
                for (int i = x; i <= tx; i ++) {
                    worldMap[i][y] = Tileset.FLOOR;
                    worldMap[i][y + 1] = Tileset.WALL;
                    worldMap[i][y - 1] = Tileset.WALL;
                }
            }
            case 2 -> {
                if (source instanceof Hallway && target instanceof Room) {
                    y -= 1;
                } else if (source instanceof Room) {
                    x = tx; y = y - source.getHeight() + 1; ty += 1;
                } else {
                    y -= 1; ty += 1;
                }
                for (int i = y; i >= ty; i --) {
                    worldMap[x][i] = Tileset.FLOOR;
                    worldMap[x - 1][i] = Tileset.WALL;
                    worldMap[x + 1][i] = Tileset.WALL;
                }
            }
            case 3 -> {
                if (source instanceof Hallway && target instanceof Room) {
                    x -= 1; tx = tx + target.getWidth() - 1;
                } else if (source instanceof Room) {
                    y = ty; tx += 1;
                } else {
                    x -= 1; tx += 1;
                }
                for (int i = x; i >= tx; i --) {
                    worldMap[i][y] = Tileset.FLOOR;
                    worldMap[i][y + 1] = Tileset.WALL;
                    worldMap[i][y - 1] = Tileset.WALL;
                }
            }
        }
    }


    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public long getSeed() {
        return seed;
    }

    public TETile[][] getWorldMap() {
        return worldMap;
    }
}
