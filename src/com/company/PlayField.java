package com.company;

public class PlayField {
    // Raw field matrix. Consider interacting through PlayFieldService!
    public Player[][] fields;
    public int fieldSize;

    public PlayField(int size) throws Exception {
        if (size <= 2) throw new Exception("Field size can't be less than 2");

        fieldSize = size;
        fields = new Player[size][size];
    }
}
