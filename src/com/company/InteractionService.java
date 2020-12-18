package com.company;

public class InteractionService {
    private Renderer render;
    public GameService gameService;

    public InteractionService(Renderer renderer) {
        this.render = renderer;
    }

    public void runLocalGame() {
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
                gameService.turn(field.getRow(), field.getColumn());
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
            var menuEntry = render.renderMenu(gameService != null && gameService.isOnline());

            if (menuEntry == MenuEntry.LOCAL_GAME) {
                runLocalGame();
            } else if (menuEntry == MenuEntry.START_SERVER) {

            } else if (menuEntry == MenuEntry.ROOM_GAME) {

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
}
