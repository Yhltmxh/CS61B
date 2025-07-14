package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.model.World;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.io.File;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 40;

    public static final File CWD = new File(System.getProperty("user.dir"));

    public static final File WORLD_FILE = FileUtils.join(CWD, "worldFile.txt");

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        drawMainMenu();
        switch (listenMainMenu()) {
            case 'n' -> doNewGame();
            case 'l' -> doLoadGame();
            case 'q' -> doQuitGame();
        }
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        input = input.toLowerCase();
        char op = input.charAt(0);
        input = input.substring(1);
        if (op == 'l') {
            return doLoadGame(input).getWorldMap();
        }
        return doNewGame(input).getWorldMap();
    }


    /**
     * 绘制主菜单
     */
    private void drawMainMenu() {
        StdDraw.setCanvasSize(WIDTH * 16, HEIGHT * 16);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 50));
        StdDraw.text(WIDTH / 2.0, HEIGHT / 10.0 * 8, "CS61B: MY WORLD");
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 30));
        StdDraw.text(WIDTH / 2.0, HEIGHT / 10.0 * 5, "New Game (N)");
        StdDraw.text(WIDTH / 2.0, HEIGHT / 10.0 * 4, "Load Game (L)");
        StdDraw.text(WIDTH / 2.0, HEIGHT / 10.0 * 3, "Quit (Q)");
        StdDraw.show();
    }


    /**
     * 监听主菜单的玩家指令
     * @return 玩家指令
     */
    private char listenMainMenu() {
        String opSet = "nlq";
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char op = Character.toLowerCase(StdDraw.nextKeyTyped());
                if (opSet.indexOf(op) != -1) {
                    return op;
                }
            }
            StdDraw.pause(15);
        }
    }


    /**
     * 键盘开始新游戏
     */
    private void doNewGame() {
        String seed = inputSeed();
        ter.initialize(WIDTH, HEIGHT, 3, 3);
        World world = new World(WIDTH - 6, HEIGHT - 6, Long.parseLong(seed));
        runGame(world);
    }


    /**
     * 字符串开始新游戏
     * @param input 输入字符串
     */
    private World doNewGame(String input) {
        int index = input.indexOf("s");
        String seed = input.substring(0, index);
        String op = "";
        if (input.length() - 1 > index) {
            op = input.substring(index + 1);
        }
        World world = new World(WIDTH - 6, HEIGHT - 6, Long.parseLong(seed));
        runGame(world, op);
        return world;
    }


    /**
     * 加载游戏
     */
    private void doLoadGame() {
        ter.initialize(WIDTH, HEIGHT, 3, 3);
        World world = FileUtils.readObject(WORLD_FILE, World.class);
        runGame(world);
    }


    /**
     * 字符串加载游戏
     * @param input 输入字符串
     */
    private World doLoadGame(String input) {
        World world = FileUtils.readObject(WORLD_FILE, World.class);
        runGame(world, input);
        return world;
    }


    /**
     * 游戏退出
     */
    private void doQuitGame() {
        StdDraw.pause(1000);
        System.exit(0);
    }


    /**
     * 游戏存档
     * @param world 世界对象
     */
    private void doSaveGame(World world) {
        if (!WORLD_FILE.exists()) {
            FileUtils.createFile(WORLD_FILE);
        }
        FileUtils.writeObject(WORLD_FILE, world);
    }


    /**
     * 种子输入
     * @return 种子
     */
    private String inputSeed() {
        StringBuilder seed = new StringBuilder();
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if (Character.toLowerCase(c) == 's') {
                    return seed.toString();
                }
                seed.append(c);
            }
            StdDraw.clear(Color.BLACK);
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.setFont(new Font("Monaco", Font.BOLD, 30));
            StdDraw.text(WIDTH / 2.0, HEIGHT / 10.0 * 6, "Please input seed and end with 's': ");
            StdDraw.text(WIDTH / 2.0, HEIGHT / 10.0 * 5, seed.toString());
            StdDraw.show();
            StdDraw.pause(100);
        }
    }


    /**
     * 键盘运行游戏
     * @param world 世界对象
     */
    private void runGame(World world) {
        boolean isCommand = false;
        StringBuilder command = new StringBuilder();
        while (true) {
            // 绘制世界
            TETile[][] worldMap = world.getWorldMap();
            ter.renderFrame(worldMap);
            StdDraw.setPenColor(StdDraw.WHITE);
            // 监听玩家指令
            if (StdDraw.hasNextKeyTyped()) {
                char op = Character.toLowerCase(StdDraw.nextKeyTyped());
                if (op == ':') {
                    isCommand = true;
                    command.append(op);
                } else if (isCommand) {
                    if (op == '\n') {
                        executeCommand(command.substring(1), world);
                        command.setLength(0);
                        isCommand = false;
                    } else {
                        command.append(op);
                    }
                } else {
                    world.moveAvatar(op);
                }
            }

            // 绘制HUD信息
            TETile mousePoint = ter.getTitle(StdDraw.mouseX(), StdDraw.mouseY(), worldMap);
            if (!mousePoint.equals(Tileset.NOTHING)) {
                StdDraw.text(3, HEIGHT - 1.5, mousePoint.description());
            }
            StdDraw.text(3, 1.5, command.toString());
            StdDraw.show();
            StdDraw.pause(100);
        }
    }


    /**
     * 输入运行游戏
     * @param world 世界对象
     */
    private void runGame(World world, String input) {
        boolean isCommand = false;
        for (int i = 0; i < input.length(); i ++) {
            // 监听玩家指令
            char op = input.charAt(i);
            if (op == ':') {
                isCommand = true;
            } else if (isCommand && op == 'q') {
                executeCommand("s", world);
                isCommand = false;
            } else {
                world.moveAvatar(op);
            }
        }
    }

    /**
     * 执行玩家指令
     * @param op 指令
     * @param world 世界对象
     */
    private void executeCommand(String op, World world) {
        switch (op) {
            case "q" -> {
                doSaveGame(world);
                doQuitGame();
            }
            case "s" -> {
                doSaveGame(world);
            }
        }
    }

}
