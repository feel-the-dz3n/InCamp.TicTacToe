package com.company;

import java.time.LocalTime;

public interface Renderer {
    void initialize();
    void shutdown();

    MenuEntry renderMenu(boolean isOnline);
    void renderPlayField(PlayField playField);
    TurnInformation askGameTurn(Player currentPlayer);
    void drawMessage(String text);
    int askPlayFieldSize();
    void drawGameFinished(PlayField playField, LocalTime startTime, LocalTime endTime, Player winner);
    ServerMenuEntry renderServerMenu(int roomsCount, int clientsCount, int port, LocalTime startTime);
    String askNickname();
    boolean drawRoomWaiting(int clientsCount);
}
