package com.company;

import java.io.*;
import java.time.Duration;
import java.time.LocalTime;
import java.time.Period;
import java.util.Locale;

public class StreamRenderer implements Renderer {
    PrintStream out;
    BufferedReader in;
    InputStreamReader inputStreamReader;
    InputStream inputStream;
    PlayFieldService fieldSvc = new PlayFieldService();
    TextRenderService textSvc = new TextRenderService();

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
                out.print(textSvc.getMainMenu(isOnline));
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

    @Override
    public void renderPlayField(PlayField playField) {
        out.println("\r\n" + textSvc.getPlayField(playField));
    }

    @Override
    public TurnInformation askGameTurn(Player currentPlayer) {
        drawMessage(textSvc.getTurnText(currentPlayer));

        String line = null;
        try {
            line = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
