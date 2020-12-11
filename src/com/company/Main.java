package com.company;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Scanner;

public class Main {
    static Scanner scanner = new Scanner(System.in);
    static GameService game = new GameService();
    static PlayFieldService fieldSvc = new PlayFieldService();

    public static void visualizeFieldSeparator() {
        var field = game.getPlayField();

        System.out.print("+");
        for (int i = 0; i < field.fieldSize; i++) {
            for (int j = 0; j < 3; j++)
                System.out.print("-");
            System.out.print("+");
        }
        System.out.println();
    }

    public static void visualizeRow(int row) {
        var fields = game.getPlayField();

        System.out.print("|");

        for (int i = 0; i < fields.fieldSize; i++) {
            System.out.print(" " + fieldSvc.getFieldTextValue(fields, row, i) + " ");
            System.out.print("|");
        }

        System.out.println();
    }

    public static void visualizePlayfield() {
        var field = game.getPlayField();
        visualizeFieldSeparator();
        for (int row = 0; row < field.fieldSize; row++) {
            visualizeRow(row);
            visualizeFieldSeparator();
        }
    }

    public static void printHelp() {
        System.out.println("Incredible TicTacToe! ");
        System.out.println("start <size (default 3)> - start a game");
        System.out.println("stop - stop a game");
        System.out.println("e, q - exit");
        System.out.println("<row> <column> - make a turn ingame");
    }

    public static void doInputLoop() {
        while (true) {
            try {
                System.out.println();

                if (game.getGameStarted()) {
                    visualizePlayfield();
                    System.out.println("Current player: " + game.getCurrentPlayer());
                } else {
                    LocalTime startTime = game.getStartTime(), endTime = game.getStopTime();
                    if (endTime == null) { // game didn't run
                        printHelp();
                    } else { // show last game info
                        visualizePlayfield();
                        var duration = Duration.between(startTime, endTime);
                        // System.out.println("Started at " + startTime + ", finished at " + endTime);
                        System.out.println("You've been playing for " + duration.toSeconds() + " seconds.");
                        System.out.println("Winner: " + game.getCurrentPlayer());
                    }
                }

                System.out.println();
                System.out.print("Input: ");
                String line = scanner.nextLine();

                if (line.startsWith("start")) {
                    int size = 3;
                    if (line.contains(" ")) {
                         size = Integer.parseInt(line.split(" ")[1]);
                    }
                    System.out.println("Starting game with size: " + size);
                    game.startGame(size);
                } else if (line.startsWith("stop")) {
                    System.out.println("Stopping the game");
                    game.stopGame();
                } else if (line.startsWith("q") || line.startsWith("e")) {
                    break;
                } else {
                    String[] numsRaw = line.split(" ");
                    int row = Integer.parseInt(numsRaw[0]);
                    int column = Integer.parseInt(numsRaw[1]);
                    TurnResult turnResult = game.turn(row, column);

                    if (turnResult == TurnResult.GameFinished)
                        System.out.println("\n !!! FINISHED !!!");
                }
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) throws Exception {
        doInputLoop();
        scanner.close();
    }
}
