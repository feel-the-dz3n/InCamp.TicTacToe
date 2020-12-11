package com.company;

import java.time.LocalTime;

public class GameService {
    private boolean isGameStarted;
    private LocalTime startTime, stopTime;
    private Player currentTurn;
    private PlayField playField;
    private PlayFieldService fieldService;

    public GameService() {
        fieldService = new PlayFieldService();
    }

    // Returns current instance of PlayField
    public PlayField getPlayField() {
        return playField;
    }

    public boolean getGameStarted() {
        return isGameStarted;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getStopTime() {
        return stopTime;
    }

    public Player getCurrentPlayer() {
        return currentTurn;
    }

    // Starts a new game or restarts a current one with playfield size 3
    public void startGame() throws Exception {
        startGame(3);
    }

    // Starts a new game or restarts a current one
    public void startGame(int playFieldSize) throws Exception {
        startGame(new PlayField(playFieldSize));
    }

    // Starts a new game or restarts a current one
    public void startGame(PlayField playField) throws Exception {
        if (isGameStarted) stopGame();

        // Initializing variables
        isGameStarted = true;
        startTime = LocalTime.now();
        stopTime = null;
        currentTurn = Player.X;
        this.playField = playField;
    }

    // Force-stops current game, so no turns can be done
    public void stopGame() throws Exception {
        if (!isGameStarted) return;

        // Initializing variables
        isGameStarted = false;
        stopTime = LocalTime.now();
        currentTurn = null;
    }

    // Switches between players. Use after turn.
    private void switchPlayers() throws Exception {
        if (!isGameStarted) throw new Exception("The game is not running.");

        if (currentTurn == Player.X) currentTurn = Player.O;
        else currentTurn = Player.X;
    }

    // Do a turn as current player.
    // In the case if any other turn can be done,
    // returns TurnResult.WaitingNextTurn and switches
    // between players. Otherwise, stops current game,
    // returns TurnResult.GameFinished. The winner is current player.
    public TurnResult turn(int row, int column) throws Exception {
        if (!isGameStarted) throw new Exception("The game is not running.");

        if (fieldService.isFieldOccupied(playField, row, column))
            throw new Exception("The field is already taken by one of players");

        fieldService.setFieldValue(playField, row, column, currentTurn);

        var winner = fieldService.getPlayerWon(playField);

        if (winner != null) {
            stopGame();
            currentTurn = winner;
            return TurnResult.GameFinished;
        } else {
            switchPlayers();
            return TurnResult.WaitingNextTurn;
        }
    }
}
