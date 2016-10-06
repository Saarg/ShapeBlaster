package com.example.jean.opengl_test.entity;

import android.util.Log;

import com.example.jean.opengl_test.shapes.Square;
import com.example.jean.opengl_test.utils.Vect;

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
        pos.set_y(pos.get_y() - dy);
    }

    public boolean bound(float limitInf, float limitSup)
    {
        if(pos.get_y() < limitInf) {
            isOnField = false;
            pos.set_y(limitSup);
        }
        else if(pos.get_y() > limitSup) {
            pos.set_y(limitSup);
        }

        return isOnField;
    }

    public Obstacle(float leX, float leY, float squaredScale, float dy)
    {
        pos.set_x(leX);
        pos.set_y(leY);
        scale = new Vect(squaredScale, squaredScale, squaredScale, this);
        setDY(dy);
    }
}
