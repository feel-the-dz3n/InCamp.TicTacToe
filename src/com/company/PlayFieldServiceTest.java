package com.company;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PlayFieldServiceTest {
    // todo cases:
    // - win for each player (X, O, BOTH, nobody)
    // - each type of win combination with different playfield sizes

    @Test
    void bothWinTest() throws Exception {
        var charMap = new char[][]{
                {'x', 'o', 'x'},
                {'o', 'x', 'o'},
                {'o', 'x', 'o'},
        };

        var svc = new PlayFieldService();
        var field = svc.playfieldFromChars(charMap);

        assertEquals(Player.BOTH, svc.getPlayerWon(field));
    }

    @Test
    void bothWinTest2() throws Exception {
        var charMap = new char[][]{
                {'o', 'x', 'x'},
                {'x', 'x', 'o'},
                {'o', 'o', 'x'},
        };

        var svc = new PlayFieldService();
        var field = svc.playfieldFromChars(charMap);

        assertEquals(Player.BOTH, svc.getPlayerWon(field));
    }

    @Test
    public void fromCharTest() throws Exception {
        var charMap = new char[][]{
                {'x', 'x', 'x'},
                {'o', 'o', 'o'},
                {' ', ' ', ' '},
        };

        var svc = new PlayFieldService();
        var playField = svc.playfieldFromChars(charMap);

        assertEquals(Player.X, svc.getFieldValue(playField, 0, 0));
        assertEquals(Player.X, svc.getFieldValue(playField, 0, 1));
        assertEquals(Player.X, svc.getFieldValue(playField, 0, 2));

        assertEquals(Player.O, svc.getFieldValue(playField, 1, 0));
        assertEquals(Player.O, svc.getFieldValue(playField, 1, 1));
        assertEquals(Player.O, svc.getFieldValue(playField, 1, 2));

        assertNull(svc.getFieldValue(playField, 2, 0));
        assertNull(svc.getFieldValue(playField, 2, 1));
        assertNull(svc.getFieldValue(playField, 2, 2));
    }

    @Test
    public void columnWinTest() throws Exception {
        PlayFieldService svc = new PlayFieldService();
        PlayField f = svc.playfieldFromChars(new char[][]{
                {'x', ' ', ' '},
                {'x', ' ', ' '},
                {'x', ' ', ' '}
        });

        assertEquals(Player.X, svc.getPlayerWon(f));
    }

    @Test
    public void rowWinTest() throws Exception {
        PlayFieldService svc = new PlayFieldService();
        PlayField f = svc.playfieldFromChars(new char[][]{
                {'x', 'x', 'x'},
                {' ', ' ', ' '},
                {' ', ' ', ' '}
        });

        assertEquals(Player.X, svc.getPlayerWon(f));
    }

    @Test
    public void diagWinTest1() throws Exception {
        PlayFieldService svc = new PlayFieldService();
        PlayField f = svc.playfieldFromChars(new char[][]{
                {'x', 'o', ' '},
                {' ', 'x', ' '},
                {'o', ' ', 'x'}
        });

        assertEquals(Player.X, svc.getPlayerWon(f));
    }

    @Test
    public void diagWinTest2() throws Exception {
        PlayFieldService svc = new PlayFieldService();
        PlayField f = svc.playfieldFromChars(new char[][]{
                {'o', 'o', 'x'},
                {' ', 'x', ' '},
                {'x', ' ', ' '}
        });

        assertEquals(Player.X, svc.getPlayerWon(f));
    }

    @Test
    public void setFieldValueTest() throws Exception {
        PlayField f = new PlayField(3);
        PlayFieldService svc = new PlayFieldService();

        // Force remove occupation
        svc.setFieldValue(f, 0, 0, null);
        assertNull(svc.getFieldValue(f, 0, 0));

        // Force X player
        svc.setFieldValue(f, 0, 0, Player.X);
        assertEquals(Player.X, svc.getFieldValue(f, 0, 0));

        // Force Y player
        svc.setFieldValue(f, 0, 0, Player.X);
        assertEquals(Player.X, svc.getFieldValue(f, 0, 0));

        // Force BOTH player, exception is excepted
        assertThrows(Exception.class, () -> svc.setFieldValue(f, 0, 0, Player.BOTH));
        svc.setFieldValue(f, 0, 0, Player.X);
        assertNotEquals(Player.BOTH, svc.getFieldValue(f, 0, 0));
    }

    @ParameterizedTest
    @CsvSource({"3", "5", "150", "1000"})
    public void fieldOccupationTest(int fieldSize) throws Exception {
        PlayField f = new PlayField(fieldSize);
        PlayFieldService svc = new PlayFieldService();

        // All must be non-occupied (null) now
        for (int row = 0; row < fieldSize; row++)
            for (int column = 0; column < fieldSize; column++)
                assertFalse(svc.isFieldOccupied(f, row, column));

        // All fields are owned by X
        for (int row = 0; row < fieldSize; row++)
            for (int column = 0; column < fieldSize; column++) {
                svc.setFieldValue(f, row, column, Player.X);
                assertEquals(Player.X, svc.getFieldValue(f, row, column));
                assertTrue(svc.isFieldOccupied(f, row, column));
            }

        // All fields are owned by O
        for (int row = 0; row < fieldSize; row++)
            for (int column = 0; column < fieldSize; column++) {
                svc.setFieldValue(f, row, column, Player.O);
                assertEquals(Player.O, svc.getFieldValue(f, row, column));
                assertTrue(svc.isFieldOccupied(f, row, column));
            }
    }
}
