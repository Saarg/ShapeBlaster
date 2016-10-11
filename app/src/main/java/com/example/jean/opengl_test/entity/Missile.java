package com.example.jean.opengl_test.entity;

import android.os.SystemClock;

import com.example.jean.opengl_test.shapes.Circle;
import com.example.jean.opengl_test.utils.Vect;

/**
 * Created by Kwarthys on 05/10/2016.
 */

public class Missile extends Circle implements Entity {

    public final String name = "Missile";

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

    public void move(float deltaTime)
    {
        pos.set_x(pos.get_x() + _dx * deltaTime);
        pos.set_y(pos.get_y() + _dy * deltaTime);
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

    public boolean isHit(float x, float y) {
        return false;
    }

    public Missile(float leX, float leY, float squaredScale, float speed, float angle)
    {
        super(20);

        float angleRad = (float) (angle/360 * 2*Math.PI);

        pos.set_x(leX);
        pos.set_y(leY);
        rot.set_z(angle);
        scale = new Vect(squaredScale/(speed), squaredScale, squaredScale, this);
        setDX((float) (Math.sin(angleRad)*speed));
        setDY((float) (Math.cos(angleRad)*speed));

        updateModelMatrix();
    }
}
