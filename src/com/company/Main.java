package com.company;

public class Main {
    public static void main(String[] args) {
        var svc = new InteractionService(new StreamRenderer(System.in, System.out));
        svc.start();
        try {
            svc.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
