package com.example.jean.spaceshootergame.entity;

import android.content.Context;

import com.example.jean.spaceshootergame.utils.Vect;


/**
 * Created by Kwarthys on 11/10/2016.
 */

public class SideMissile extends Missile {

    private float _speed;
    private float _startingX, _startingY, _startingAngle;

    public SideMissile(Context context, float leX, float leY, float squaredScale, float speed, float angle)
    {
        super(context, leX, leY, squaredScale, speed, angle);
        _speed = speed;
        _startingX = leX;
        _startingY = leY;
        _startingAngle = angle;
    }

    public void move(float deltaTime)
    {
        rot.set_z((float)Math.exp(-(new Vect(_startingX - pos.get_x(), _startingY - pos.get_y(),(float)0)).getMagnitude()*1.8)*_startingAngle);

        float angleRad = (float) (rot.get_z()/360 * 2*Math.PI);
        setDX((float) (Math.sin(angleRad)*_speed));
        setDY((float) (Math.cos(angleRad)*_speed));

        pos.set_x(pos.get_x() + _dx * deltaTime);
        pos.set_y(pos.get_y() + _dy * deltaTime);
    }
}
