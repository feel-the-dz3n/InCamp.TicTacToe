package com.company;

public class InteractionService {
    private RemotePlayer remotePlayer;
    private Renderer render;
    public GameService gameService;

    // Local player renderer
    public InteractionService(Renderer renderer) {
        this.render = renderer;
    }

    // Remote player renderer
    public InteractionService(Renderer renderer, RemotePlayer player) {
        this(renderer);
        this.remotePlayer = player;
    }

    private void runLocalGame() {
        int playfieldSize = render.askPlayFieldSize();
        gameService = new GameService();
        try {
            gameService.startGame(playfieldSize);
        } catch (Exception ex) {
            render.drawMessage("Failed to start the game: " + ex.getMessage());
        }

        while (gameService.getGameStarted()) {
            render.renderPlayField(gameService.getPlayField());

            try {
                TurnInformation field = render.askGameTurn(gameService.getCurrentPlayer());
                if (!field.isLeavingGame()) {
                    gameService.turn(field.getRow(), field.getColumn());
                } else {
                    gameService.stopGame();
                }
            } catch (Exception ex) {
                render.drawMessage("Unable to do a turn: " + ex.getMessage());
            }
        }

        render.drawGameFinished(
                gameService.getPlayField(),
                gameService.getStartTime(),
                gameService.getStopTime(),
                gameService.getCurrentPlayer());
    }

    public void run() {
        render.initialize();

        while (true) {
            var menuEntry = render.renderMenu(isOnline());

            if (menuEntry == MenuEntry.LOCAL_GAME) {
                runLocalGame();
            } else if (menuEntry == MenuEntry.START_SERVER) {
                runServer();
            } else if (menuEntry == MenuEntry.ROOM_GAME) {
                runRoomGame();
            } else if (menuEntry == MenuEntry.QUIT) {
                break;
            }
        }

        if (gameService != null) {
            try {
                gameService.stopGame();
            } catch (Exception ex) {
                render.drawMessage("Unable to stop game: " + ex.getMessage());
            }
        }

        render.shutdown();
    }

    public boolean isOnline() {
        return remotePlayer != null;
    }

    private void runRoomGame() {
        try {
            var telnetService = new TelnetService();
            var nick = render.askNickname();
            remotePlayer.setNickname(nick);

            var room = telnetService.getFreeRoom(remotePlayer);

            while (!room.isRoomFull()) {
                if (!render.drawRoomWaiting(telnetService.getClientsCount())) {
                    // Player decides to leave the room
                    room.leaveRoom(remotePlayer);
                    telnetService.update();
                    break;
                }

                Thread.sleep(1000);
            }

            if (!room.isRoomFull()) return; // somebody left
            else {
                // Game began
                this.gameService = room.getGameService();

                while (gameService.getGameStarted()) {
                    render.renderPlayField(gameService.getPlayField());

                    if (gameService.getCurrentPlayer() == remotePlayer.getMark()) {
                        // it's our turn
                        var turn = render.askGameTurn(remotePlayer.getMark());
                        try {
                            gameService.turn(turn.getRow(), turn.getColumn());
                        } catch (Exception ex) {
                            render.drawMessage("Unable to do a turn: " + ex.getMessage());
                        }
                    } else {
                        // waiting for opponent
                        render.drawMessage(String.format(
                                "Waiting for oponent %s '%s'",
                                gameService.getCurrentPlayer(),
                                room.getPlayerByMark(gameService.getCurrentPlayer()).getNickname()
                        ));
                    }
                }

                render.drawGameFinished(
                        gameService.getPlayField(),
                        gameService.getStartTime(),
                        gameService.getStopTime(),
                        gameService.getCurrentPlayer());
            }

        } catch (Exception ex) {
            render.drawMessage("Room game failed: " + ex.getMessage());
        }
    }

    private void runServer() {
        try {
            var telnetSvc = new TelnetService(4000);
            telnetSvc.start();

            Thread.sleep(500); // wait a lil' for starting a thread

            while (telnetSvc.isAlive()) {
                var menuEntry = render.renderServerMenu(
                        telnetSvc.getRoomsCount(),
                        telnetSvc.getClientsCount(),
                        telnetSvc.getPort(),
                        telnetSvc.getStartTime()
                );

                if (menuEntry == ServerMenuEntry.STOP_SERVER) {
                    telnetSvc.stopServer();
                    render.drawMessage("Please wait, stopping the server...");
                    telnetSvc.join();
                }
            }
        } catch (Exception e) {
            render.drawMessage("Server Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
