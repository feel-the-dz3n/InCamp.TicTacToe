package com.company;

public class TextRenderService {
    PlayFieldService fieldSvc = new PlayFieldService();

    public TextRenderService() {
    }

    private String getFieldSeparator(PlayField field) {
        StringBuilder b = new StringBuilder();

        b.append("+");
        for (int i = 0; i < field.fieldSize; i++) {
            for (int j = 0; j < 3; j++)
                b.append("-");
            b.append("+");
        }

        b.append("\r\n");

        return b.toString();
    }

    private String getRow(PlayField fields, int row) {
        StringBuilder b = new StringBuilder();
        b.append("|");

        for (int i = 0; i < fields.fieldSize; i++) {
            b.append(" " + fieldSvc.getFieldTextValue(fields, row, i) + " ");
            b.append("|");
        }

        b.append("\r\n");
        return b.toString();
    }

    public String getPlayField(PlayField playField) {
        StringBuilder b = new StringBuilder();

        b.append(getFieldSeparator(playField));
        for (int row = 0; row < playField.fieldSize; row++) {
            b.append(getRow(playField, row));
            b.append(getFieldSeparator(playField));
        }

        return b.toString();
    }

    public String getMainMenu(boolean isOnlineClient) {
        StringBuilder b = new StringBuilder();

        b.append("==== TicTacToe ");
        if (isOnlineClient) b.append("(online)");
        else b.append("(host)");

        b.append("\r\n\r\n");

        b.append("Main Menu\r\n");
        b.append("1) Two Players, One Screen\r\n");
        b.append("2) ");

        if (isOnlineClient) b.append("Join/Create a Room");
        else b.append("Start Server");

        b.append("\r\n3) Quit\r\n\r\nYour choice: ");

        return b.toString();
    }

    public String getTurnText(Player currentPlayer) {
        return "Player " + currentPlayer + ", type ROW COLUMN to make a turn: ";
    }
}
