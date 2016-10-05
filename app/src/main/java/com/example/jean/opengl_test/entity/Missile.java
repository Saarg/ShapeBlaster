package com.example.jean.opengl_test.entity;

import com.example.jean.opengl_test.shapes.Triangle;

/**
 * Created by Kwarthys on 05/10/2016.
 */

public class Missile extends Triangle implements Entity {

    private float _dy = 0;

    public float getDY() {
        return _dy;
    }

    public void setDY(float newDY) {
        _dy = newDY;
    }

    public void move()
    {
        y+=_dy;
    }

    public boolean bound(float limitInf, float limitSup)
    {
        if(y < limitInf)
        {
            y = limitInf;
        }
        else if(y > limitSup)
        {
            y = limitSup;
            return false;
        }
        return true;
    }

    public Missile(float leX, float leY, float squaredScale, float dy)
    {
        y = leY;
        x = leX;
        scaleX = scaleY = scaleZ = squaredScale;
        setDY(dy);
    }
}
