package com.example.jean.opengl_test.entity;

import android.os.SystemClock;

import com.example.jean.opengl_test.shapes.Triangle;

import java.util.ArrayList;

/**
 * Created by Kwarthys on 04/10/2016.
 */

public class Player extends Triangle implements Entity{

    private float dx = 0;
    private long _time;
    protected long _shootingRate = 1000;

    public float getDX(){return dx;}

    public void setDX(float newDX){dx = newDX;}

    public void move(){
        x += dx;
    }

    public Missile shoot() {
        if(SystemClock.uptimeMillis() - _time > _shootingRate) {
            _time = SystemClock.uptimeMillis();
            return new Missile(x, y, 0.1f, 0.03f);
        }
        return null;
    }

    public boolean bound(float limitInf, float limitSup)
    {
        if(x < limitInf) {
            x = limitInf;
        }
        else if(x > limitSup) {
            x = limitSup;
        }

        return true;
    }

    public Player(float leX, float leY, float squaredScale)
    {
        super();
        _time = SystemClock.uptimeMillis();
        x = leX; y = leY;
        scaleX = scaleY = scaleZ = squaredScale;
    }
}
