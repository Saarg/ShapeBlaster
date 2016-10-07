package com.example.jean.opengl_test.entity;

import com.example.jean.opengl_test.shapes.Triangle;
import com.example.jean.opengl_test.utils.Vect;

/**
 * Created by Kwarthys on 05/10/2016.
 */

public class Missile extends Triangle implements Entity {

    private float _dx = 0;
    private float _dy = 0;

    public float getDX() {
        return _dx;
    }

    public void setDX(float newDX) {
        _dx = newDX;
    }

    public float getDY() {
        return _dy;
    }

    public void setDY(float newDY) {
        _dy = newDY;
    }

    public void move()
    {
        pos.set_x(pos.get_x() + _dx);
        pos.set_y(pos.get_y() + _dy);
    }

    public boolean bound(float limitInf, float limitSup)
    {
        if(pos.get_y() < limitInf)
        {
            pos.set_y(limitInf);
        }
        else if(pos.get_y() > limitSup)
        {
            pos.set_y(limitSup);
            return false;
        }
        return true;
    }

    public Missile(float leX, float leY, float squaredScale, float dx, float dy)
    {
        pos.set_x(leX);
        pos.set_y(leY);
        scale = new Vect(squaredScale, squaredScale, squaredScale, this);
        setDX(dx);
        setDY(dy);
    }
}
