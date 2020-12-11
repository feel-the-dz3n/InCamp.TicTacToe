package com.company;

public class PlayFieldService {
    // Checks if player won on current playfield
    public boolean checkPlayerWon(PlayField playField, Player player) throws Exception {
        return getPlayerWon(playField) == player;
    }

    // Checks if any play won on current playfield
    public boolean isAnyPlayerWon(PlayField playField) throws Exception {
        return getPlayerWon(playField) != null;
    }

    private Player checkRowLine(PlayField playField, int row) {
        var prevPlayer = getFieldValue(playField, row, 0);
        for (int column = 0; column < playField.fieldSize; column++) {
            var owned = getFieldValue(playField, row, column);
            if (owned != prevPlayer) {
                return null;
            } else {
                prevPlayer = owned;
            }
        }

        return prevPlayer;
    }

    private Player checkColumnLine(PlayField playField, int column) {
        var prevPlayer = getFieldValue(playField, 0, column);
        for (int row = 0; row < playField.fieldSize; row++) {
            var owned = getFieldValue(playField, row, column);
            if (owned != prevPlayer) {
                return null;
            } else {
                prevPlayer = owned;
            }
        }

        return prevPlayer;
    }

    private Player checkDiagonalLine(PlayField playField, boolean inverse) {
        int startRow = inverse ? playField.fieldSize - 1 : 0;
        int startColumn = 0;
        int endRow = inverse ? 0 - 1 : playField.fieldSize - 1 + 1;
        int endColumn = playField.fieldSize - 1;
        var prevPlayer = getFieldValue(playField, startRow, startColumn);
        int row = startRow, column = startColumn;

        while (row != endRow && column != endColumn + 1) {
            var owned = getFieldValue(playField, row, column);
            if (owned != prevPlayer) {
                return null;
            } else {
                prevPlayer = owned;
            }

            if (!inverse) row++;
            else row--;

            column++;
        }

        return prevPlayer;
    }

    public PlayField playfieldFromChars(char[][] chars) throws Exception {
        if (chars[0].length != chars.length) throw new Exception("Invalid array size");

        int size = chars[0].length;
        PlayField playField = new PlayField(size);

        for (int row = 0; row < size; row++)
            for (int column = 0; column < size; column++)
                if (chars[row][column] == 'x')
                    setFieldValue(playField, row, column, Player.X);
                else if (chars[row][column] == 'o')
                    setFieldValue(playField, row, column, Player.O);
                else
                    setFieldValue(playField, row, column, null);

        return playField;
    }

    public boolean isTurnAvailableOnPlayfield(PlayField playField) {
        for (int row = 0; row < playField.fieldSize; row++)
            for (int column = 0; column < playField.fieldSize; column++)
                if (!isFieldOccupied(playField, row, column))
                    return true;
        return false;
    }

    // Returns a player that won a current playfield or null if no one
    public Player getPlayerWon(PlayField playField) throws Exception {
        // There are 8 win combinations
        Player winner = null;
        boolean skipCombination = false;

        // 1. Check row lines
        for (int row = 0; row < playField.fieldSize; row++) {
            winner = checkRowLine(playField, row);
            if (winner != null) return winner;
        }

        // 2. Check column lines
        for (int column = 0; column < playField.fieldSize; column++) {
            winner = checkColumnLine(playField, column);
            if (winner != null) return winner;
        }

        // 3. Check diagonal
        winner = checkDiagonalLine(playField, false);
        if (winner != null) return winner;

        // 4. Check inverted diagonal
        winner = checkDiagonalLine(playField, true);
        if (winner != null) return winner;

        // No winner at this pont. Let's check if playfield is
        // fully occupied by players. In this case, there's
        // no way to do a turn, and both players won
        if (!isTurnAvailableOnPlayfield(playField))
            return Player.BOTH;

        return null;
    }

    // Returns field owner and null if no one occupied that field
    public Player getFieldValue(PlayField playField, int row, int column) {
        return playField.fields[row][column];
    }

    public String getFieldTextValue(PlayField playField, int row, int column) {
        var val = getFieldValue(playField, row, column);
        if (val == null) return " ";
        else if (val == Player.X) return "x";
        else if (val == Player.O) return "o";
        else return "?";
    }

    // Returns true if field is occupied by any player and false if it's free
    public boolean isFieldOccupied(PlayField playField, int row, int column) {
        return getFieldValue(playField, row, column) != null;
    }

    // Force sets field occupation player
    public void setFieldValue(PlayField playField, int row, int column, Player player) throws Exception {
        if (player != null && player != Player.X && player != Player.O)
            throw new Exception("Invalid player");

        playField.fields[row][column] = player;
    }
}
