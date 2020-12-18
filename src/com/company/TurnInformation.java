package com.company;

public class TurnInformation {
    private int row;
    private int column;
    private boolean leaveGame;

    public TurnInformation(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public TurnInformation(boolean leaveGame) {
        this.leaveGame = leaveGame;
    }

    public boolean isLeavingGame() {
        return this.leaveGame;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}
