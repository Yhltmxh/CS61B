package byow.model;

import byow.TileEngine.TETile;

/**
 * @description: 矩形区域
 * @author: 杨怀龙
 * @create: 2025-07-09 15:59
 **/
public class RectArea {

    /**
     * 区域宽度
     */
    private int width;

    /**
     * 区域高度
     */
    private int height;

    /**
     * 区域x坐标（房间为左上角的位置，走廊为中间位置）
     */
    private int x;

    /**
     * 区域y坐标（房间为左上角的位置，走廊为中间位置）
     */
    private int y;

    /**
     * 围墙的瓦片种类
     */
    private TETile wall;

    /**
     * 地板的瓦片种类
     */
    private TETile floor;

    /**
     * 四面相互连接的区域
     */
    private RectArea[] nearArea = new RectArea[4];

    public RectArea(int width, int height, int x, int y, TETile wall, TETile floor) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.wall = wall;
        this.floor = floor;
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

    public TETile getWall() {
        return wall;
    }

    public void setWall(TETile wall) {
        this.wall = wall;
    }

    public TETile getFloor() {
        return floor;
    }

    public void setFloor(TETile floor) {
        this.floor = floor;
    }

    public RectArea[] getNearArea() {
        return nearArea;
    }
}
