package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {

    private int width = 50;

    private int height = 50;

    private TETile[][] world;

    private int hexSize;

    private final long seed;
    private final Random random;

    public HexWorld(int width, int height, int hexSize, long seed) {
        this.width = width;
        this.height = height;
        this.hexSize = hexSize;
        this.seed = seed;
        random = new Random(seed);
        initWorld();
        addHexagon();
    }

    private void initWorld() {
        world = new TETile[width][height];
        for (int i = 0; i < world.length; i ++) {
            for (int j = 0; j < world[0].length; j ++) {
                world[i][j] = Tileset.NOTHING;
            }
        }
    }

    private void addHexagon() {
        // 计算中心区域的六边形坐标
        int x = width / 2 - hexSize / 2;
        int y = height / 2 - hexSize;
        Hexagon center = new Hexagon(getRandomTiles(), x, y);
        drawHexagon(center);
        createNearHexagon(center);
        for (int i = 0; i < 6; i ++) {
            createNearHexagon(center.getNearHexagon(i));
        }
    }


    private void createNearHexagon(Hexagon current) {
        int x = current.getX();
        int y = current.getY();
        TETile t = getRandomTiles();
        for (int i = 0; i < 6; i ++) {
            if (current.getNearHexagon(i) != null) {
                continue;
            }
            Hexagon h = switch (i) {
                case 0 -> new Hexagon(t, x, y + hexSize * 2);
                case 1 -> new Hexagon(t, x + 2 * hexSize - 1, y + hexSize);
                case 2 -> new Hexagon(t, x + 2 * hexSize - 1, y - hexSize);
                case 3 -> new Hexagon(t, x, y - hexSize * 2);
                case 4 -> new Hexagon(t, x - 2 * hexSize + 1, y - hexSize);
                case 5 -> new Hexagon(t, x - 2 * hexSize + 1, y + hexSize);
                default -> null;
            };
            if (checkHexagon(h)) {
                current.setNearHexagon(i, h);
                drawHexagon(h);
            }
        }
    }


    private boolean checkHexagon(Hexagon h) {
        if (h == null) {
            return false;
        }
        int x1 = h.getX() - hexSize + 1;
        int x2 = h.getX() + 2 * hexSize - 2;
        int y1 = h.getY() - hexSize * 2 + 1;
        int y2 = h.getY();
        return x1 >= 0 && x2 < width && y1 >= 0 && y2 < height;
    }

    private TETile getRandomTiles() {
        int tileNum = random.nextInt(7);
        return switch (tileNum) {
            case 0 -> Tileset.WALL;
            case 1 -> Tileset.GRASS;
            case 2 -> Tileset.WATER;
            case 3 -> Tileset.FLOWER;
            case 4 -> Tileset.SAND;
            case 5 -> Tileset.MOUNTAIN;
            case 6 -> Tileset.TREE;
            default -> Tileset.NOTHING;
        };
    }

    private void drawHexagon(Hexagon h) {
        int x = h.getX();
        int y = h.getY();
        TETile t = h.getTeTile();
        for (int i = 0; i < hexSize; i ++, y --) {
            for (int j = x - i; j < x - i + hexSize + i * 2; j ++) {
                world[j][y] = t;
            }
        }
        for (int i = hexSize - 1; i >= 0; i --, y --) {
            for (int j = x - i; j < x - i + hexSize + i * 2; j ++) {
                world[j][y] = t;
            }
        }
    }

    private void drawHexagonByRandomColor(Hexagon h) {
        int x = h.getX();
        int y = h.getY();
        TETile t = h.getTeTile();
        for (int i = 0; i < hexSize; i ++, y --) {
            for (int j = x - i; j < x - i + hexSize + i * 2; j ++) {
                world[j][y] = TETile.colorVariant(t, 255, 255, 255, random);
            }
        }
        for (int i = hexSize - 1; i >= 0; i --, y --) {
            for (int j = x - i; j < x - i + hexSize + i * 2; j ++) {
                world[j][y] = TETile.colorVariant(t, 255, 255, 255, random);
            }
        }
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public TETile[][] getWorld() {
        return world;
    }

    public void setWorld(TETile[][] world) {
        this.world = world;
    }

    public int getHexSize() {
        return hexSize;
    }

    public void setHexSize(int hexSize) {
        this.hexSize = hexSize;
    }

    public long getSeed() {
        return seed;
    }

}
