package com.company;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.util.HashSet;

public class TelnetService extends Thread {
    private ServerSocket serverSocket;
    private boolean stopping;
    private HashSet<RemotePlayer> clients = new HashSet<>();
    private HashSet<GameRoom> rooms = new HashSet<>();
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

    public GameRoom getFreeRoom() {
        return null;
    }

    public int getClientsCount() {
        return clients.size();
    }
}
