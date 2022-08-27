package cs1302.game;

import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;

/**
 * The class respresents a game of minesweeper.
 */
public class MinesweeperGame {
    int row;
    int col;
    int rounds;
    int numMines;
    double score;
    private final Scanner stdIn;
    ArrayList<ArrayList<Integer>> board = new ArrayList<ArrayList<Integer>>();
    ArrayList<ArrayList<String>> stats = new ArrayList<ArrayList<String>>();
    ArrayList<Integer> mines = new ArrayList<Integer>();

    /**
     * Constructs a {@code MinesweeperGame} object with specificed
     * rows, columns, number of mines, and a score. The rows, columns,
     * and number of mines are determined by the seed path while the
     * score is calculated by rows times lengths divided by the round
     * multiplied by 100.
     * @param stdIn Scanner for system input
     * @param seedPath values for rows, columns, number of mines,
     * and their squares
     * @throws FileNotFoundException seed path is invalid
     */
    public MinesweeperGame(Scanner stdIn, String seedPath) throws FileNotFoundException {
        this.stdIn = stdIn;
        File configFile = new File(seedPath);
        Scanner seedScan = new Scanner(configFile);
        row = seedScan.nextInt();
        col = seedScan.nextInt();
        numMines = seedScan.nextInt();
        // checks if the game can be set up with the seed file input
        if (row < 5 || row > 10 || col < 5 || col > 10) {
            System.err.println();
            System.err.println("Seed File Malformed Error: "
                + "Cannot create a mine field with that many rows and/or columns!");
            System.exit(3);
        } // if
        if (numMines < 1 || numMines > (row * col) - 1) {
            System.err.println();
            System.err.println("Seed File Malformed Error: "
                + "Cannot create a mine field with that many rows and/or columns!");
            System.exit(3);
        } // if
        // fills board and stat ArrayLists with their default values.
        for (int i = 0; i < row; i++) {
            board.add(new ArrayList<Integer>(row));
            stats.add(new ArrayList<String>(col));
            for (int j = 0; j < col; j++) {
                board.get(i).add(0);
                stats.get(i).add("   ");
            } // for
        } // for
        // places mines in their designated coordinates from the seed file.
        for (int i = 0; i < numMines; i++) {
            int x = seedScan.nextInt();
            int y = seedScan.nextInt();
            try {
                board.get(x).set(y, 1);
            } catch (IndexOutOfBoundsException ioobe) {
                System.err.println();
                System.err.println("Seed File Malformed Error: "
                    + "Mine Coordinates Out Of Bounds");
                System.exit(3);
            } // try
        } // for
        printWelcome();
        printMineField();
    } // MinesweeperGame


    /**
     * Prints the welcome banner when a game is started.
     * Credit to Dr. Barnes for the code.
     */
    public void printWelcome() {
        try {
            File configFile = new File("resources/welcome.txt");
            Scanner welcome = new Scanner(configFile);
            while (welcome.hasNextLine()) {
                String line = welcome.nextLine();
                System.out.println(line);
            } // while
        } catch (FileNotFoundException fnfe) {
            System.err.println("Welcome banner not found");
        } // try
    } // printWelcome


    /**
     * Print the game prompt and interpret user input.
     */
    public void promptUser() {
        System.out.println();
        System.out.print("minesweeper-alpha: ");
        // Scans in input and creates a scanner from the line to be parsed through
        String fullCommand = stdIn.nextLine().trim();
        Scanner commandScan = new Scanner(fullCommand);
        String command = " ";
        try {
            command = commandScan.next().trim();
        } catch (NoSuchElementException nsee) {
            System.err.println();
            System.err.println("Invalid Command: " + nsee.getMessage());
            printMineField();
        } // try
        // checks first word of the input for a keyword and performs its role, error otherwise
        if (command.equals("help") || command.equals("h")) {
            help();
        } else if (command.equals("quit") || command.equals("q")) {
            quit();
        } else if (command.equals("nofog")) {
            noFog();
        } else if (command.equals("reveal") || command.equals("r")) {
            reveal(commandScan);
        } else if (command.equals("mark") || command.equals("m")) {
            // Displays an "F" on the inputted square
            try {
                int rowIn = commandScan.nextInt();
                int colIn = commandScan.nextInt();
                stats.get(rowIn).set(colIn, " F ");
                rounds++;
                printMineField();
            } catch (IndexOutOfBoundsException ioobe) {
                inputError(ioobe);
            } catch (NoSuchElementException nsee) {
                System.err.println();
                System.err.println("Invalid Command: " + nsee.getMessage());
                printMineField();
            } // try
        } else if (command.equals("guess") || command.equals("g")) {
            // Displays a "?" on the inputted square
            try {
                int rowIn = commandScan.nextInt();
                int colIn = commandScan.nextInt();
                stats.get(rowIn).set(colIn, " ? ");
                rounds++;
                printMineField();
            } catch (IndexOutOfBoundsException ioobe) {
                inputError(ioobe);
            } catch (NoSuchElementException nsee) {
                System.err.println();
                System.err.println("Invalid Command: " + nsee.getMessage());
                printMineField();
            } // try
        } else {
            // Displays error if a correct keyword is not used
            System.err.println();
            System.err.println("Invalid Command: Command not recognized!");
            printMineField();
        } // else
    } //promptUser


    /**
     * Displays an error to user that the input for the round was invalid.
     * @param ioobe Index Out Of Bounds Exception from invalid inputs to play the game
     */
    public void inputError(IndexOutOfBoundsException ioobe) {
        System.err.println();
        System.err.println("Invalid Command: " + ioobe.getMessage());
        printMineField();
    } // inputError


    /**
     * Checks if a mine is at the spot, exits if so and displays adjecent mines if not.
     * @param commandScan Scanner of the input from the user for the turn
     */
    public void reveal(Scanner commandScan) {
        try {
            int rowIn = commandScan.nextInt();
            int colIn = commandScan.nextInt();
            if (board.get(rowIn).get(colIn) == 0) {
                stats.get(rowIn).set(colIn, getNumAdjMines(rowIn, colIn));
                rounds++;
                printMineField();
            } else {
                printLoss();
                System.exit(0);
            } // else
        } catch (IndexOutOfBoundsException ioobe) {
            System.err.println();
            System.err.println("Invalid Command: " + ioobe.getMessage());
            printMineField();
        } catch (NoSuchElementException nsee) {
            System.err.println();
            System.err.println("Invalid Command: " + nsee.getMessage());
            printMineField();
        } // try
    } // reveal


    /**
     * Prints the help menu to the player.
     */
    public void help() {
        System.out.println();
        System.out.println("Commands Available...");
        System.out.println("- Reveal: r/reveal row col");
        System.out.println("-   Mark: m/mark   row col");
        System.out.println("-  Guess: g/guess  row col");
        System.out.println("-   Help: h/help");
        System.out.println("-   Quit: q/quit");
        rounds++;
        printMineField();
    } // help


    /**
     * Gracefully exits from the game.
     */
    public void quit() {
        System.out.println();
        System.out.println("Quitting the game...");
        System.out.println("Bye!");
        System.exit(0);
    } // if quit


    /**
     * Reveals the position of all mines to the player.
     */
    public void noFog() {
        // Changes the display of each coordinate with a mine to have "< >" around it
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (board.get(i).get(j) == 1) {
                    if (stats.get(i).get(j).equals(" F ")) {
                        stats.get(i).set(j, "<F>");
                    } else if (stats.get(i).get(j).equals(" ? ")) {
                        stats.get(i).set(j, "<?>");
                    } else {
                        stats.get(i).set(j, "< >");
                    } // else
                } // if
            } // for
        } // for
        printMineField();
        // Resets the fog after it is printed with "< >" once
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (board.get(i).get(j) == 1) {
                    if (stats.get(i).get(j).equals("<F>")) {
                        stats.get(i).set(j, " F ");
                    } else if (stats.get(i).get(j).equals("<?>")) {
                        stats.get(i).set(j, " ? ");
                    } else {
                        stats.get(i).set(j, "   ");
                    } // else
                } // if
            } // for
        } // for
    } // noFog


    /**
     * Decides whether the game is won.
     * @return true if game is won, false otherwise
     */
    public boolean isWon() {
        boolean condition1 = false;
        boolean condition2 = false;
        // checks if all squares containing a mine are marked as containing a mine
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (board.get(i).get(j) == 1) {
                    if (stats.get(i).get(j).equals(" F ")) {
                        condition1 = true;
                    } else {
                        condition1 = false;
                    } // else
                } // if
            }  // for
        } // for
        // checks if all squares not containing a mine are revealed
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (stats.get(i).get(j).equals(" F ") || stats.get(i).get(j).equals(" 1 ") ||
                    stats.get(i).get(j).equals(" 2 ") || stats.get(i).get(j).equals(" 3 ") ||
                    stats.get(i).get(j).equals(" 4 ") || stats.get(i).get(j).equals(" 5 ") ||
                    stats.get(i).get(j).equals(" 6 ") || stats.get(i).get(j).equals(" 7 ") ||
                    stats.get(i).get(j).equals(" 8 ") || stats.get(i).get(j).equals(" 0 ")) {
                    condition2 = true;
                } else {
                    condition2 = false;
                    break;
                } // else
            } // for
        } // for
        // declares the game won if both conditions are met
        if (condition1 && condition2) {
            return true;
        } else {
            return false;
        } // else
    } // isWon


    /**
     * Prints the board array to appear like the Minesweeper board.
     */
    public void printMineField() {
        System.out.println();
        System.out.println(" Rounds Completed: " + rounds);
        System.out.println();
        for (int i = 0; i < row; i++) {
            System.out.print(" " + i + " ");
            for (int j = 0; j < col; j++) {
                System.out.print("|" + stats.get(i).get(j));
            } // for
            System.out.print("|");
            System.out.println();
        } // for
        System.out.print("  ");
        for (int k = 0; k < row; k++) {
            System.out.print("   " + k);
        } // for
        System.out.println();
    } // printMineField


    /**
     * Tells whether or not a square index is part of the board.
     * @param row the row index of the board
     * @param col the column index of the board
     * @return true if index is on board, false otherwise
     */
    private boolean isInBounds(int row, int col) {
        try {
            if (board.get(row).get(col) == 0 || board.get(row).get(col) ==  1) {
                return true;
            } else {
                return false;
            } // else
        } catch (IndexOutOfBoundsException aioobe) {
            return false;
        } // try
    } // isInBounds


    /**
     * Returns the number of mines adjacent to the square
     * inputted by the user.
     * @param row the row index of the board
     * @param col the column index of the board
     * @return the number of adjacent mines
     */
    private String getNumAdjMines(int row, int col) {
        int count = 0;
        // checks each square around the inputted coordinate
        if (isInBounds((row - 1), (col - 1)) && board.get(row - 1).get(col - 1) == 1) {
            count++;
        } // if
        if (isInBounds((row), (col - 1)) && board.get(row).get(col - 1) == 1) {
            count++;
        } // if
        if (isInBounds((row + 1), (col - 1)) && board.get(row + 1).get(col - 1) == 1) {
            count++;
        } // if
        if (isInBounds((row - 1), (col)) && board.get(row - 1).get(col) == 1) {
            count++;
        } // if
        if (isInBounds((row + 1), (col)) && board.get(row + 1).get(col) == 1) {
            count++;
        } // if
        if (isInBounds((row - 1), (col + 1)) && board.get(row - 1).get(col + 1) == 1) {
            count++;
        } // if
        if (isInBounds((row), (col + 1)) && board.get(row).get(col + 1) == 1) {
            count++;
        } // if
        if (isInBounds((row + 1), (col + 1)) && board.get(row + 1).get(col + 1) == 1) {
            count++;
        } // if
        return (" " + count + " ");
    } // getNumAdjMines


    /**
     * Prints the win message to standard output.
     */
    public void printWin() {
        try {
            File configFile = new File("resources/gamewon.txt");
            Scanner welcome = new Scanner(configFile);
            while (welcome.hasNextLine()) {
                System.out.println();
                String line = welcome.nextLine();
                System.out.print(line);
            } // while
        } catch (FileNotFoundException fnfe) {
            System.err.println("Welcome banner not found");
        } // try
        System.out.println(" " + score);
        System.out.println();
    } // printWin


    /**
     * Prints the game over message to standard output.
     */
    public void printLoss() {
        try {
            System.out.println();
            File configFile = new File("resources/gameover.txt");
            Scanner welcome = new Scanner(configFile);
            while (welcome.hasNextLine()) {
                String line = welcome.nextLine();
                System.out.println(line);
            } // while
        } catch (FileNotFoundException fnfe) {
            System.err.println("Welcome banner not found");
        } // try
        System.out.println();
    } // printLoss


    /**
     * Provides the main game loop.
     */
    public void play() {
        while (!isWon()) {
            promptUser();
        } // while
        score = (double) Math.round((100.0 * row * col * 100) / rounds) / 100;
        printWin();
        System.exit(0);
    } // play

} //MinesweeperGame
