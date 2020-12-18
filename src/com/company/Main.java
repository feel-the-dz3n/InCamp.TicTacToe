package com.company;

public class Main {
    public static void main(String[] args) {
        new InteractionService(new StreamRenderer(System.in, System.out)).run();
    }
}
