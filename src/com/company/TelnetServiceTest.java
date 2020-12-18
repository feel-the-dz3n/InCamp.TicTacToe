package com.company;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TelnetServiceTest {

    @Test
    void getFreeeRoom__updateClients__tests() {
        var svc = new TelnetService();

        // Creating fake players
        RemotePlayer
                player1 = new RemotePlayer(null),
                player2 = new RemotePlayer(null),
                player3 = new RemotePlayer(null),
                player4 = new RemotePlayer(null);

        svc.clients.add(player1);
        svc.clients.add(player2);
        svc.clients.add(player3);
        svc.clients.add(player4);

        // Testing room search
        // Both rooms are supposed to be the same for player1 and player2
        GameRoom room1 = svc.getFreeRoom(player1);
        GameRoom room2 = svc.getFreeRoom(player2);

        assertEquals(room1, room2);

        // Now another room is supposed to be created for two next players
        // Both rooms are supposed to be the same for player3 and player4
        GameRoom room3 = svc.getFreeRoom(player3);
        GameRoom room4 = svc.getFreeRoom(player4);

        assertEquals(room3, room4);
        assertNotEquals(room1, room3);

        // Let's test getRemotePlayerRoom, why not?
        assertEquals(room1, svc.getRemotePlayerRoom(player1));
        assertEquals(room1, svc.getRemotePlayerRoom(player2));
        assertEquals(room3, svc.getRemotePlayerRoom(player3));
        assertEquals(room3, svc.getRemotePlayerRoom(player4));

        // Now let's imagine that player 2 which was in room1/2 left the game
        svc.clients.remove(player2);
        svc.updateClients();

        // Player 1 is supposed to be removed from the 'room1/2'
        // 'room1/2' is supposed to be removed from TelnetService at all
        // Two other players are supposed to innocently keep
        // playing in the 'room3/4' like nothing happened...
        assertEquals(1, svc.getRoomsCount()); // only one room left

        assertNull(svc.getRemotePlayerRoom(player1)); // player 1 is not any room

        assertEquals(room3, svc.getRemotePlayerRoom(player3)); // player 3 is in room 3/4
        assertEquals(room4, svc.getRemotePlayerRoom(player4)); // player 4 is in room 3/4

        // OK, now let's imagine that player 4 lost the connection.
        svc.clients.remove(player4);
        svc.updateClients();

        // So, there are no more rooms on the server
        assertEquals(0, svc.getRoomsCount());

        // And player 3 is not in a room
        assertNull(svc.getRemotePlayerRoom(player3));

        // And now players 1 and 3 are decides to start a new online session
        room1 = svc.getFreeRoom(player1);
        room2 = svc.getFreeRoom(player3);

        // And test if everything is correct
        assertEquals(room1, room2);
        assertEquals(room1, svc.getRemotePlayerRoom(player1));
        assertEquals(room1, svc.getRemotePlayerRoom(player3));
    }
}