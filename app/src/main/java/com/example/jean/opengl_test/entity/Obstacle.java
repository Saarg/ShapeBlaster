package com.example.jean.opengl_test.entity;

import com.example.jean.opengl_test.shapes.Square;

/**
 * Created by Kwarthys on 04/10/2016.
 */

public class Obstacle extends Square implements Entity{

    private float dy = 0;

    public void setDY(float newDY){dy = newDY;}

    public void move()
    {
        y -= dy;
    }

    public void bound(float limitInf, float limitSup)
    {
        if(y < limitInf) {
            y = limitSup;
        }
        if(y > limitSup) {
            y = limitSup;
        }
    }
}
