package com.company;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {
    @Test
    // Starts a game, checks if current player does a turn,
    // Stops a game, does a faily-turn, checks if player is still correct,
    // Then starts a game again, does a good turn and check a player
    public void stopStartTest() throws Exception {
        GameService g = new GameService();
        g.startGame(3);
        assertEquals(Player.X, g.getCurrentPlayer());

        g.stopGame();
        assertThrows(Exception.class, () -> g.turn(0,0));

        g.startGame(3);
        assertEquals(Player.X, g.getCurrentPlayer());

        g.turn(1,1);
        assertEquals(Player.O, g.getCurrentPlayer());
    }

    @Test
    public void winTest() throws Exception {
        GameService g = new GameService();
        g.startGame(3);

        // - - -
        // - x -
        // - - -
        g.turn(1, 1); // X

        // - o -
        // - x -
        // - - -
        g.turn(1, 0); // O

        // x o -
        // - x -
        // - - -
        g.turn(0, 0); // X

        // x o -
        // - x -
        // - o -
        g.turn(2, 1); // O

        // x o -
        // - x -
        // - o x
        TurnResult result = g.turn(2, 2); // X

        assertEquals(TurnResult.GameFinished, result);
        assertEquals(Player.X, g.getCurrentPlayer());
    }
}