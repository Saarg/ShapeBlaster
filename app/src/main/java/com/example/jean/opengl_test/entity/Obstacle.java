package com.example.jean.opengl_test.entity;

import android.util.Log;

import com.example.jean.opengl_test.shapes.Square;

/**
 * Created by Kwarthys on 04/10/2016.
 */

public class Obstacle extends Square implements Entity{

    private float dy = 0;

    private boolean isOnField = true;

    public boolean isOnField(){return isOnField;}

    public void setDY(float newDY){dy = newDY;}

    public void move()
    {
        y -= dy;
    }

    public boolean bound(float limitInf, float limitSup)
    {
        if(y < limitInf) {
            isOnField = false;
            y = limitSup;
        }
        if(y > limitSup) {
            y = limitSup;
        }

        return isOnField;
    }

    public Obstacle(float leX, float leY, float squaredScale, float dy)
    {
        y = leY;
        x = leX;
        scaleX = scaleY = scaleZ = squaredScale;
        setDY(dy);
    }
}
