package com.example.jean.opengl_test.shapes;

/**
 * Created by jean on 03/10/16.
 */

public class Triangle extends Shape{

    private final float triangleCoords[] = {   // in counterclockwise order:
            0.0f,  0.622008459f, 0.0f, // top
            -0.5f, -0.311004243f, 0.0f, // bottom left
            0.5f, -0.311004243f, 0.0f  // bottom right
    };

    public Triangle() {
        super();
        super.init(triangleCoords);
    }
}