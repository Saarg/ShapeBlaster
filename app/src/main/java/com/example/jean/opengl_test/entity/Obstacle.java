package com.example.jean.opengl_test.entity;

import android.util.Log;

import com.example.jean.opengl_test.shapes.Square;
import com.example.jean.opengl_test.utils.Vect;

/**
 * Created by Kwarthys on 04/10/2016.
 */

public class Obstacle extends Square implements Entity{

    public final String name = "Obstacle";

    private float dy = 0;

    private boolean isOnField = true;

    public boolean isOnField(){return isOnField;}

    public void setDY(float newDY){dy = newDY;}

    public void move(float deltaTime)
    {
        pos.set_y(pos.get_y() - dy*deltaTime);
    }

    public boolean bound(float limitInf, float limitSup)
    {
        if(pos.get_y() < limitInf) {
            isOnField = false;
        }
        else if(pos.get_y() > limitSup) {
            pos.set_y(limitSup);
        }

        return isOnField;
    }

    public boolean isHit(float x, float y) {
        return (pos.get_x() + scale.get_x() > x && pos.get_x() - scale.get_x() < x && pos.get_y() + scale.get_y() > y && pos.get_y() - scale.get_y() < y);
    }

    public Obstacle(float leX, float leY, float squaredScale, float dy)
    {
        pos.set_x(leX);
        pos.set_y(leY);
        scale = new Vect(squaredScale, squaredScale, squaredScale, this);
        setDY(dy);

        setColor(new Vect(0.63671875f, 0.22265625f, 0.22265625f));
    }
}
