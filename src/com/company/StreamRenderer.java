package com.company;

import java.io.*;
import java.time.LocalTime;

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
}
