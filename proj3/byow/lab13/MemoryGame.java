package byow.lab13;

import byow.Core.RandomUtils;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class MemoryGame {
    /** The width of the window of this game. */
    private int width;
    /** The height of the window of this game. */
    private int height;
    /** The current round the user is on. */
    private int round;
    /** The Random object used to randomly generate Strings. */
    private Random rand;
    /** Whether or not the game is over. */
    private boolean gameOver;
    /** Whether or not it is the player's turn. Used in the last section of the
     * spec, 'Helpful UI'. */
    private boolean playerTurn;
    /** The characters we generate random Strings from. */
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    /** Encouraging phrases. Used in the last section of the spec, 'Helpful UI'. */
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
                                                   "You got this!", "You're a star!", "Go Bears!",
                                                   "Too easy for you!", "Wow, so impressive!"};

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        long seed = Long.parseLong(args[0]);
        MemoryGame game = new MemoryGame(40, 40, seed);
        game.startGame();
    }

    public MemoryGame(int width, int height, long seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        rand = new Random(seed);
    }

    public String generateRandomString(int n) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < n; i ++) {
            res.append(CHARACTERS[RandomUtils.uniform(rand, 26)]);
        }
        return res.toString();
    }

    public void drawFrame(String s, String action, int round) {
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.line(0, height - 3, width, height - 3);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 30));
        StdDraw.text(width / 2.0, height / 2.0, s);

        StdDraw.setFont(new Font("Monaco", Font.BOLD, 20));
        StdDraw.textLeft(1, height - 1.5, String.format("Round: %d", round));
        StdDraw.text(width / 2.0, height - 1.5, action);
        int len = ENCOURAGEMENT.length;
        StdDraw.textRight(width - 1, height - 1.5, ENCOURAGEMENT[RandomUtils.uniform(rand, len)]);
        StdDraw.show();
    }

    public void flashSequence(String letters) {
        for (int i = 0; i < letters.length(); i ++) {
            char c = letters.charAt(i);
            drawFrame(Character.toString(c), "Watch!", letters.length());
            StdDraw.pause(1000);
            drawFrame("", "Watch!", letters.length());
            StdDraw.pause(500);
        }
        drawFrame("", "Type!", letters.length());
    }

    public String solicitNCharsInput(int n) {
        StringBuilder res = new StringBuilder();
        int cnt = 0;
        while (cnt < n){
            if (StdDraw.hasNextKeyTyped()) {
                res.append(StdDraw.nextKeyTyped());
                drawFrame(res.toString(), "Type!", n);
                cnt += 1;
            }
            StdDraw.pause(100);
        }
        return res.toString();
    }

    public void startGame() {
        int round = 1;
        while (true) {
            drawFrame(String.format("Round: %d", round), "Watch!", round);
            StdDraw.pause(1000);
            String cur = generateRandomString(round);
            flashSequence(cur);
            String input = solicitNCharsInput(round);
            if (input.equals(cur)) {
                round += 1;
                StdDraw.pause(1000);
            } else {
                drawFrame(String.format("Game Over! You made it to round: %d", round), "", round);
                break;
            }
        }
    }

}
