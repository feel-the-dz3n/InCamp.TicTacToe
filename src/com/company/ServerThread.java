package com.company;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.util.HashSet;

public class ServerThread extends Thread {
    private final Logger log;
    private ServerSocket serverSocket;
    private boolean stopping;
    final HashSet<RemotePlayer> clients = new HashSet<>();
    final HashSet<GameRoom> rooms = new HashSet<>();
    private LocalTime startTime;

    public ServerThread() {
        log = System.getLogger("Server");
    }

    public ServerThread(int port) throws IOException {
        this();
        serverSocket = new ServerSocket(port);
    }

    public void run() {
        startTime = LocalTime.now();

        log.log(Level.TRACE, "Starting server on port " + serverSocket.getLocalPort());

        while (!stopping) {
            processClients();
        }

        shutdownServer();
    }

    private void shutdownServer() {
        log.log(Level.TRACE, "Shutting down the server");

        // Close all rooms
        rooms.forEach(GameRoom::closeRoom);

        // Close all connections
        clients.forEach((r) -> {
            try {
                r.getSocket().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        update();

        log.log(Level.TRACE, "Shutdown completed");
    }

    private void processClients() {
        try {
            // Wait for a new connection
            Socket client = serverSocket.accept();

            log.log(Level.INFO, "Got a new connection from " + client.getLocalAddress());

            // We got one, now let's get in/out buffers
            var inputStream = client.getInputStream();
            var outStream = client.getOutputStream();

            // Current thread will keep waiting for new connections
            // Creating a new thread for interaction with client
            Thread interactionThread = new Thread(() -> {

                // Creating a new RemotePlayer instance
                var remotePlayer = new RemotePlayer(client);

                clients.add(remotePlayer);

                // Creating InteractionService
                var interaction = new InteractionService(
                        new StreamRenderer(inputStream, new PrintStream(outStream)),
                        remotePlayer,
                        this);

                // Start interaction!
                interaction.run();

                // Interaction finished, now cleanup
                log.log(Level.INFO, "Connection finished");
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                update();
            });

            interactionThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopServer() {
        this.stopping = true;

        // Force close server to avoid waiting for socket.accept()...
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getRoomsCount() {
        return rooms.size();
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    // Returns GameRoom if player joined any or
    // returns null if player didn't join any room
    public GameRoom getRemotePlayerRoom(RemotePlayer player) {
        synchronized (rooms) {
            for (var room : rooms)
                if (room.isPlayerJoined(player))
                    return room;
        }

        return null;
    }

    // 1. Creates or finds a room where player is able to join
    // 2. Joins room
    public GameRoom getFreeRoom(RemotePlayer player) {
        synchronized (rooms) {
            GameRoom playerRoom;

            for (var room : rooms) {
                if (room.canJoinRoom()) {
                    log.log(Level.INFO, "Found a room for player " + player.getNickname());

                    room.joinRoom(player);
                    return room;
                }
            }

            // We have not found room, so create a new one
            log.log(Level.INFO, "Creating a room for player " + player.getNickname());

            playerRoom = new GameRoom(this);
            rooms.add(playerRoom);

            playerRoom.joinRoom(player);

            return playerRoom;
        }
    }

    public int getClientsCount() {
        return clients.size();
    }

    // Checks if all players are still 'alive', otherwise
    // removes them from rooms and cleans up rooms
    // remark: call this when update is expedient
    public void update() {
        updateClients();
        updateRooms();
    }

    private void updateClients() {
        synchronized (clients) {
            try {
                clients.forEach(this::updateClient);
            } catch (Exception e) {
            }
        }
    }


    private void updateRooms() {
        synchronized (rooms) {
            try {
                rooms.forEach(this::updateRoom);
            } catch (Exception e) {
            }
        }
    }

    private void updateClient(RemotePlayer player) {
        if (!player.isAlive()) {
            log.log(Level.INFO, "Got a dead client, cleaning up");

            var room = getRemotePlayerRoom(player);
            if (room != null) {
                log.log(Level.INFO, "Previous dead client was in a room");
                room.leaveRoom(player);
            }

            clients.remove(player);
        }
    }

    private void updateRoom(GameRoom room) {
        var gameService = room.getGameService();

        if (gameService.getGameStarted() && !room.isRoomFull()) {
            log.log(Level.INFO, "Got an incomplete room, cleaning up");

            room.closeRoom();
            rooms.remove(room);
        }
    }

    public boolean isRoomAvailable(GameRoom room) {
        return rooms.contains(room);
    }
}
