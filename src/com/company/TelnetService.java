package com.company;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.util.HashSet;

// TODO:
// - remove 'dead' players
// - correctly process rooms where somebody left the game
// - implement getFreeRoom

public class TelnetService extends Thread {
    private ServerSocket serverSocket;
    private boolean stopping;
    HashSet<RemotePlayer> clients = new HashSet<>();
    HashSet<GameRoom> rooms = new HashSet<>();
    private LocalTime startTime;

    public TelnetService() {

    }

    public TelnetService(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    public void run() {
        startTime = LocalTime.now();

        while (!stopping) {
            processClients();
        }

        shutdownServer();
    }

    private void shutdownServer() {
        // Close and remove each room
        rooms.forEach((room) -> {
            room.closeRoom();
            rooms.remove(room);
        });

        // Close all connections and remove all players
        clients.forEach((r) -> {
            try {
                r.getSocket().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            clients.remove(r);
        });
    }

    private void processClients() {
        try {
            Socket client = serverSocket.accept();
            var remotePlayer = new RemotePlayer(client);
            clients.add(remotePlayer);
            var svc = new InteractionService(
                    new StreamRenderer(client.getInputStream(), new PrintStream(client.getOutputStream())),
                    remotePlayer);

            svc.start();
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
        for (var room : rooms)
            if (room.isPlayerJoined(player))
                return room;

        return null;
    }

    // 1. Creates or finds a room where player is able to join
    // 2. Joins room
    public GameRoom getFreeRoom(RemotePlayer player) {
        return null;
    }

    public int getClientsCount() {
        return clients.size();
    }

    // Checks if all players are still 'alive', otherwise
    // removes them from rooms and cleans up rooms
    // TODO: call this when update is expedient
    public void updateClients() {
    }
}
