package com.company;

import java.io.*;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Locale;

public class StreamRenderer implements Renderer {
    PrintStream out;
    BufferedReader in;
    InputStreamReader inputStreamReader;
    InputStream inputStream;
    PlayFieldService fieldSvc = new PlayFieldService();

    public StreamRenderer(InputStream in, PrintStream out) {
        this.inputStream = in;
        this.out = out;
    }

    @Override
    public void initialize() {
        inputStreamReader = new InputStreamReader(inputStream);
        in = new BufferedReader(inputStreamReader);
    }

    @Override
    public void shutdown() {
        try {
            in.close();
            inputStreamReader.close();
        } catch (Exception ex) {
        }
    }

    @Override
    public MenuEntry renderMenu(boolean isOnline) {
        while (true) {
            try {
                out.print("==== TicTacToe ");
                if (isOnline) out.print("(online)");
                else out.print("(host)");

                out.print("\r\n\r\n");

                out.print("Main Menu\r\n");
                out.print("1) Two Players, One Screen\r\n");
                out.print("2) ");

                if (isOnline) out.print("Join/Create a Room");
                else out.print("Start Server");

                out.print("\r\n3) Quit\r\n\r\nYour choice: ");

                var line = in.readLine();
                int value = Integer.parseInt(line);

                switch (value) {
                    case 1:
                        return MenuEntry.LOCAL_GAME;
                    case 2:
                        return isOnline ? MenuEntry.ROOM_GAME : MenuEntry.START_SERVER;
                    case 3:
                        return MenuEntry.QUIT;
                    default:
                        throw new Exception("Unknown menu entry");
                }
            } catch (Exception ex) {
                drawMessage("Error: " + ex.getMessage());
            }
        }
    }

    private void visualizeRow(PlayField fields, int row) {
        out.print("|");

        for (int i = 0; i < fields.fieldSize; i++) {
            out.print(" " + fieldSvc.getFieldTextValue(fields, row, i) + " ");
            out.print("|");
        }

        out.println();
    }

    public void visualizeFieldSeparator(PlayField field) {
        out.print("+");
        for (int i = 0; i < field.fieldSize; i++) {
            for (int j = 0; j < 3; j++)
                out.print("-");
            out.print("+");
        }
        out.println();
    }

    @Override
    public void renderPlayField(PlayField playField) {
        visualizeFieldSeparator(playField);
        for (int row = 0; row < playField.fieldSize; row++) {
            visualizeRow(playField, row);
            visualizeFieldSeparator(playField);
        }

        out.println("\r\n");
    }

    @Override
    public TurnInformation askGameTurn(Player currentPlayer) {
        drawMessage("Current Player: " + currentPlayer);
        drawMessage("Write 'ROW COLUMN' to make a turn or 'q' to leave");

        String line = null;
        try {
            line = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (line.toLowerCase(Locale.ROOT).startsWith("q"))
            return new TurnInformation(true);

        String[] numsRaw = line.split(" ");
        int row = Integer.parseInt(numsRaw[0]);
        int column = Integer.parseInt(numsRaw[1]);

        return new TurnInformation(row, column);
    }

    @Override
    public void drawMessage(String text) {
        out.println(text);
    }

    @Override
    public int askPlayFieldSize() {
        // FIXME: use TextRenderService
        drawMessage("\r\nEnter playfield size (or leave empty for 3): ");

        String line = null;
        try {
            line = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (line != null && !line.isEmpty())
            return Integer.parseInt(line);
        else
            return 3;
    }

    @Override
    public void drawGameFinished(PlayField playField, LocalTime startTime, LocalTime endTime, Player winner) {
        // TODO: show more information
        renderPlayField(playField);
        drawMessage("\r\nGame finished, winner: " + winner);
    }

    @Override
    public ServerMenuEntry renderServerMenu(int roomsCount, int clientsCount, int port, LocalTime startTime) {
        // FIXME: use TextRenderService
        drawMessage("\r\n\r\nThe server is running.");
        drawMessage("Rooms Count: " + roomsCount);
        drawMessage("Clients Count: " + clientsCount);
        drawMessage("Port: " + port);

        if (startTime != null) {
            var duration = Duration.between(startTime, LocalTime.now());
            var s = duration.getSeconds();
            drawMessage(String.format("Uptime: %d:%02d:%02d", s / 3600, (s % 3600) / 60, (s % 60)));
        }

        drawMessage("\r\nPut 'stop' into buffer to stop the server.");

        String line = null;
        try {
            line = in.readLine();
            if (line.toLowerCase(Locale.ROOT).startsWith("stop")) return ServerMenuEntry.STOP_SERVER;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String askNickname() {
        drawMessage("\r\nEnter your nickname (or leave empty):");

        String line = null;
        try {
            line = in.readLine();
            if (line.isEmpty()) return "(no name)";
            else return line;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "(no name)";
    }
}
