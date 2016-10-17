package com.example.jean.spaceshootergame.shapes;

import android.content.Context;

/**
 * Created by jean on 03/10/16.
 */

public class Square extends Shape {

    static float squareCoords[] = {
            -0.5f,  0.5f, 0.0f,   // top left
            -0.5f, -0.5f, 0.0f,   // bottom left
            0.5f, -0.5f, 0.0f,   // bottom right
            0.5f,  0.5f, 0.0f }; // top right

    static short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices

    public Square(Context context) {
        super(context);
    }

    public void init() {
        super.init(squareCoords, drawOrder);
    }

    public void init(int vertexShader, int fragmentShader) {
        super.init(squareCoords, drawOrder, vertexShader, fragmentShader);
    }
}