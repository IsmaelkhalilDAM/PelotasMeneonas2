package com.example.pelotasmeneonas;

import java.util.Random;

public class Aleatorio {

    public static int sgte(int min, int max) {
        int l = max - min + 1;
        return ((int) (Math.random() * l * 1000) % l) + min;
    }
    public static float sgtef(float min, float max) {
        float l = max - min + 1;
        return ((float) (Math.random() * l * 1000) % l) + min;
    }
}