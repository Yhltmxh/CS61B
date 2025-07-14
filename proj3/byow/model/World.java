package byow.model;

import byow.Core.RandomUtils;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;

/**
 * @description: 世界
 * @author: 杨怀龙
 * @create: 2025-07-09 16:19
 **/
public class World implements Serializable {

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

    private final Avatar avatar;

    public World(int width, int height, long seed) {
        this.width = width;
        this.height = height;
        this.seed = seed;
        random = new Random(seed);
        worldMap = new TETile[width][height];
        avatar = new Avatar(0, 0);
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

        // 生成玩家初始位置
        int ax = RandomUtils.uniform(random, ox + 1, ox + originWidth - 1);
        int ay = RandomUtils.uniform(random, oy - originHeight + 2, oy);
        avatar.setCoordinate(ax, ay);
        worldMap[ax][ay] = avatar.getTile();
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
        int tx = sx, ty = sy, tw = getRoomSize(), th = getRoomSize();
        switch(toward) {
            case 0 -> {
                tx = RandomUtils.uniform(random, sx - tw + 2, sx);
                ty = RandomUtils.uniform(random, sy + th + 2, sy + th + 7);
            }
            case 1 -> {
                tx = RandomUtils.uniform(random, sx + tw + 2, sx + tw + 7);
                ty = RandomUtils.uniform(random, sy + 1, sy + th - 1);
            }
            case 2 -> {
                tx = RandomUtils.uniform(random, sx - tw + 2, sx);
                ty = RandomUtils.uniform(random, sy - 6, sy - 1);
            }
            case 3 -> {
                tx = RandomUtils.uniform(random, sx - tw - 6, sx - tw);
                ty = RandomUtils.uniform(random, sy + 1, sy + th - 1);
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
                    tx = RandomUtils.uniform(random, sx + 1, sx + sw - 1);
                    ty = RandomUtils.uniform(random, sy + 3, sy + 6);
                }
                case 1 -> {
                    tx = RandomUtils.uniform(random, sx + sw + 2, sx + sw + 5);
                    ty = RandomUtils.uniform(random, sy - sh + 2, sy);
                }
                case 2 -> {
                    tx = RandomUtils.uniform(random, sx + 1, sx + sw - 1);
                    ty = RandomUtils.uniform(random, sy - sh - 5, sy - sh - 2);
                }
                case 3 -> {
                    tx = RandomUtils.uniform(random, sx - 6, sx - 3);
                    ty = RandomUtils.uniform(random, sy - sh + 2, sy);
                }
            }
        } else if (source instanceof Hallway) {
            switch(toward) {
                case 0 -> ty = RandomUtils.uniform(random, sy + 3, sy + 6);
                case 1 -> tx = RandomUtils.uniform(random, sx + 3, sx + 6);
                case 2 -> ty = RandomUtils.uniform(random, sy - 6, sy - 3);
                case 3 -> tx = RandomUtils.uniform(random, sx - 6, sx - 3);
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
        source.getNearArea()[toward] = target;
        drawPath(getPathArgs(source, target, toward));
    }


    /**
     * 检查两区域间的道路是否通畅
     * @param source 源区域
     * @param target 目标区域
     * @param toward 朝向
     * @return true：无障碍，false：有障碍
     */
    private boolean checkPath(RectArea source, RectArea target, int toward) {
        int[] args = getPathArgs(source, target, toward);
        int t1 = args[0], t2 = args[1], k = args[2], newToward = args[3];
        for (int i = t1 + 1; i < t2; i ++) {
            for (int j = -1; j < 2; j ++) {
                int a = k + j, b = i;
                if (newToward == 1 || newToward == 3) {
                    a = i; b = k + j;
                }
                if (!worldMap[a][b].equals(Tileset.NOTHING)) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * 绘制路径
     * @param args 参数数组
     */
    private void drawPath(int[] args) {
        int t1 = args[0], t2 = args[1], k = args[2], toward = args[3];
        for (int i = t1; i <= t2; i ++) {
            if (toward == 0 || toward == 2) {
                worldMap[k][i] = Tileset.FLOOR;
                worldMap[k - 1][i] = Tileset.WALL;
                worldMap[k + 1][i] = Tileset.WALL;
            } else {
                worldMap[i][k] = Tileset.FLOOR;
                worldMap[i][k + 1] = Tileset.WALL;
                worldMap[i][k - 1] = Tileset.WALL;
            }
        }
    }


    /**
     * 获取道路参数
     * @param source 源区域
     * @param target 目标区域
     * @param toward 朝向
     * @return 参数数组
     */
    private int[] getPathArgs(RectArea source, RectArea target, int toward) {
        if (!(source instanceof Hallway) && !(target instanceof Hallway)) {
            throw new RuntimeException("given area have not Hallway");
        }
        RectArea s = source, t = target;
        // 若source不为Hallway，则将两对象调换，并将朝向反转
        if (!(source instanceof Hallway)) {
            s = target; t = source;
            toward = reverseToward(toward);
        }
        int x = s.getX(), y = s.getY(), tx = t.getX(), ty = t.getY();
        int tw = t.getWidth(), th = t.getHeight();
        if (t instanceof Hallway) {
            tx -= 1; ty += 1;
        }
        return switch(toward) {
            case 0 -> new int[] {y + 1, ty - th + 1, x, toward};
            case 1 -> new int[] {x + 1, tx, y, toward};
            case 2 -> new int[] {ty, y - 1, x, toward};
            case 3 -> new int[] {tx + tw - 1, x - 1, y, toward};
            default -> null;
        };
    }


    /**
     * 反转朝向
     * @param toward 给定朝向
     * @return 反转后的朝向
     */
    private int reverseToward(int toward) {
        return switch (toward) {
            case 0, 2 -> 2 - toward;
            case 1, 3 -> 4 - toward;
            default -> throw new IllegalArgumentException("toward error");
        };
    }


    private int getRoomSize() {
        return RandomUtils.uniform(random, 4, 10);
    }


    /**
     * 玩家移动
     * @param op 移动操作
     */
    public void moveAvatar(char op) {
        int ax = avatar.getX(), ay = avatar.getY();
        int tx = ax, ty = ay;
        switch (op) {
            case 'w' -> ay += 1;
            case 's' -> ay -= 1;
            case 'a' -> ax -= 1;
            case 'd' -> ax += 1;
        }
        if (!worldMap[ax][ay].equals(Tileset.WALL)) {
            worldMap[tx][ty] = Tileset.FLOOR;
            worldMap[ax][ay] = avatar.getTile();
            avatar.setCoordinate(ax, ay);
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

    public Avatar getAvatar() {
        return avatar;
    }
}
