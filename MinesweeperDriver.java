package cs1302.game;

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * A driver program that runs {@code cs1302.game.MinesweeperGame}.
 */
public class MinesweeperDriver {

    /**
     * Reads the seed path on the command-line argument and instantiates a
     * {@link cs1302.game.MinesweeperGame} object with the parameters of
     * a scanner reading system input and a String of the seed path.
     * If the seed file cannot be found or is inputted incorrectly,
     * the program is exitted.
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        Scanner stdIn = new Scanner(System.in);
        String seedPath = args[0];
        if (args.length != 1) {
            System.err.println();
            System.err.println("Usage: MinesweeperDriver SEED_FILE_PATH");
            System.exit(1);
        } // if
        try {
            MinesweeperGame round = new MinesweeperGame(stdIn, seedPath);
            round.play();
        } catch (FileNotFoundException fnfe) {
            System.err.println();
            System.err.println("Seed File Not Found Error: " + fnfe.getMessage());
            System.exit(2);
        } // try
    } // main
} // MinesweeperDriver
