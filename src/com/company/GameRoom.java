package com.company;

import java.util.HashSet;

public class GameRoom {
    private HashSet<RemotePlayer> players = new HashSet<>();
    private GameService gameService = new GameService();
    private ServerThread serverThread;

    public GameRoom(ServerThread serverThread) {
        gameService.setRoom(this);
        this.serverThread = serverThread;
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

    public GameService getGameService() {
        return gameService;
    }

    public void joinRoom(RemotePlayer player) {
        players.add(player);

        // Currently, the 1st player joined
        // the room is X and the 2nd is O
        if (players.size() == 1) player.setMark(Player.X);
        else player.setMark(Player.O);

        // Check if all joined
        if (isRoomFull()) {
            // all players joined, start the game
            try {
                gameService.startGame();
            } catch (Exception e) {
                // Game failed to start, destroy room
                closeRoom();
                e.printStackTrace();
            }
        }
    }

    public boolean isPlayerJoined(RemotePlayer player) {
        return players.contains(player);
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

    public RemotePlayer getPlayerByMark(Player mark) {
        for (var p : players)
            if (p.getMark() == mark)
                return p;
        return null;
    }
}
