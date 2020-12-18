package com.company;

import java.util.HashSet;

public class GameRoom {
    private HashSet<RemotePlayer> players = new HashSet<>();
    private GameService gameService = new GameService();

    public GameRoom() {
        gameService.setRoom(this);
    }

    public boolean isRoomEmpty() {
        return players.size() == 0;
    }

    public boolean isRoomFull() {
        return players.size() >= 2;
    }

    public boolean canJoinRoom() {
        return players.size() < 2;
    }

    public void joinRoom(RemotePlayer player) {
        players.add(player);
    }

    public void leaveRoom(RemotePlayer player) {
        players.remove(player);

        if (gameService.getGameStarted()) {
            try {
                gameService.stopGame();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void closeRoom() {
        if (gameService.getGameStarted()) {
            try {
                gameService.stopGame();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        players.forEach((x) -> leaveRoom(x));
    }
}
