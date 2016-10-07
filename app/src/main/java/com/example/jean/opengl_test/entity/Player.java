package com.example.jean.opengl_test.entity;

import android.os.SystemClock;

import com.example.jean.opengl_test.shapes.Triangle;
import com.example.jean.opengl_test.utils.Vect;

import java.util.ArrayList;

/**
 * Created by Kwarthys on 04/10/2016.
 */

public class Player extends Triangle implements Entity{

    private float dx = 0;
    private long _time;
    protected long _shootingRate = 500;

    public float getDX(){return dx;}

    public void setDX(float newDX){dx = newDX;}

    public void move(){
        pos.set_x(pos.get_x() + dx);
    }

    public Missile[] shoot() {
        if(SystemClock.uptimeMillis() - _time > _shootingRate) {
            _time = SystemClock.uptimeMillis();
            Missile missiles[] = {
                    new Missile(pos.get_x(), pos.get_y(), 0.1f, 0.0f, 0.03f),
                    new Missile(pos.get_x(), pos.get_y(), 0.1f, -0.02f, 0.03f),
                    new Missile(pos.get_x(), pos.get_y(), 0.1f, 0.02f, 0.03f)
            };
            return missiles;
        }
        return null;
    }

    public boolean bound(float limitInf, float limitSup)
    {
        if(pos.get_x() < limitInf) {
            pos.set_x(limitInf);
        }
        else if(pos.get_x() > limitSup) {
            pos.set_x(limitSup);
        }

        return true;
    }

    public Player(float leX, float leY, float squaredScale)
    {
        super();
        _time = SystemClock.uptimeMillis();
        pos.set_x(leX);
        pos.set_y(leY);
        scale = new Vect(squaredScale, squaredScale, squaredScale, this);
    }
}
