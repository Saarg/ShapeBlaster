package com.example.jean.spaceshootergame.shapes;

import android.content.Context;

/**
 * Created by jean on 03/10/16.
 */

public class Triangle extends Shape{

    static float triangleCoords[] = {   // in counterclockwise order:
            0.0f,  0.622008459f, 0.0f, // top
            -0.5f, -0.311004243f, 0.0f, // bottom left
            0.5f, -0.311004243f, 0.0f  // bottom right
    };

    static short drawOrder[] = { 0, 1, 2 };

    public Triangle(Context context) {
        super(context);
        super.init(triangleCoords);
    }

    public void init() {
        super.init(triangleCoords, drawOrder);
    }

    public void init(int vertexShader, int fragmentShader) {
        super.init(triangleCoords, drawOrder, vertexShader, fragmentShader);
    }
}