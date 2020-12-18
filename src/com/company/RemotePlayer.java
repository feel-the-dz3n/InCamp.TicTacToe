package com.company;

import java.net.Socket;

public class RemotePlayer {
    private Socket socket;
    private Player mark;
    private String nickname;

    public RemotePlayer(Socket socket) {
        this.socket = socket;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setMark(Player mark) {
        this.mark = mark;
    }

    public Socket getSocket() {
        return socket;
    }

    public Player getMark() {
        return mark;
    }
}
