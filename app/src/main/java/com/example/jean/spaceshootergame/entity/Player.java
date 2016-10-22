package com.example.jean.spaceshootergame.entity;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import com.example.jean.spaceshootergame.shapes.Triangle;
import com.example.jean.spaceshootergame.utils.Vect;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Kwarthys on 04/10/2016.
 */

public class Player extends Triangle implements Entity{

    private final float MAX_DX = 2.1f;

    private int _score = 0;
    private float dx = 0;
    private long _time;
    protected long _shootingRate = 1000;
    private ArrayList<Missile> _missiles = new ArrayList<>();

    public float getDX(){return dx;}

    public void setDX(float newDX){dx = newDX;}

    public final int getScore() { return _score; }

    public void incScore(final int inc) {
        _score += inc;
        if(_score < 0) { _score = 0; }
    }

    public void move(float deltaTime){
        pos.set_x(pos.get_x() + dx * deltaTime);
        for (Missile m : _missiles) {
            m.move(deltaTime);
        }
    }

    public void shoot() {
        if(SystemClock.uptimeMillis() - _time > _shootingRate) {
            _time = SystemClock.uptimeMillis();

            if(_score >= 100) {
                _missiles.add(new Missile(_ActivityContext, pos.get_x(), pos.get_y(), 0.15f, 2.0f, 0.0f, this.color));
                _missiles.add(new Missile(_ActivityContext, pos.get_x(), pos.get_y(), 0.15f, 1.0f, 10.0f, this.color));
                _missiles.add(new Missile(_ActivityContext, pos.get_x(), pos.get_y(), 0.15f, 1.0f, -10.0f, this.color));
                _missiles.add(new SideMissile(_ActivityContext, pos.get_x(), pos.get_y(), 0.15f, 2.0f, 90.0f, this.color));
                _missiles.add(new SideMissile(_ActivityContext, pos.get_x(), pos.get_y(), 0.15f, 2.0f, -90.0f, this.color));

            } else if(_score >= 40) {
                _missiles.add(new Missile(_ActivityContext, pos.get_x(), pos.get_y(), 0.15f, 2.0f, 10.0f, this.color));
                _missiles.add(new Missile(_ActivityContext, pos.get_x(), pos.get_y(), 0.15f, 2.0f, -10.0f, this.color));
                _missiles.add(new SideMissile(_ActivityContext, pos.get_x(), pos.get_y(), 0.15f, 2.0f, 90.0f, this.color));
                _missiles.add(new SideMissile(_ActivityContext, pos.get_x(), pos.get_y(), 0.15f, 2.0f, -90.0f, this.color));

            } else if(_score >= 10) {
                _missiles.add(new Missile(_ActivityContext, pos.get_x(), pos.get_y(), 0.15f, 2.0f, 0.0f, this.color));
                _missiles.add(new SideMissile(_ActivityContext, pos.get_x(), pos.get_y(), 0.15f, 2.0f, 90.0f, this.color));
                _missiles.add(new SideMissile(_ActivityContext, pos.get_x(), pos.get_y(), 0.15f, 2.0f, -90.0f, this.color));

            } else {
                _missiles.add(new Missile(_ActivityContext, pos.get_x(), pos.get_y(), 0.15f, 2.0f, 0.0f, this.color));

            }
        }
    }

    public void draw(float[] MVPMatrix) {
        super.draw(MVPMatrix);

        for (Missile m : _missiles) {
            m.draw(MVPMatrix);
        }
    }

    public boolean bound(float limitInf, float limitSup)
    {
        if(pos.get_x() < limitInf) {
            pos.set_x(limitInf);
        }
        else if(pos.get_x() > limitSup) {
            pos.set_x(limitSup);
        }

        Iterator<Missile> i = _missiles.iterator();
        while (i.hasNext()) {
            Missile m = i.next();
            if (!m.bound(limitInf, limitSup)) { // World bound
                i.remove();
            }
        }

        return true;
    }

    public boolean isHit(float x, float y) {
        return (pos.get_x() + scale.get_x()/2 > x && pos.get_x() - scale.get_x()/2 < x && pos.get_y() + scale.get_y()/2 > y && pos.get_y() - scale.get_y()/2 < y);
    }

    public final ArrayList<Missile> getMissiles() { return _missiles; }

    public Player(Context context, float leX, float leY, float squaredScale)
    {
        super(context);
        super.init();
        _time = SystemClock.uptimeMillis();
        pos.set_x(leX);
        pos.set_y(leY);
        scale = new Vect(squaredScale, squaredScale, squaredScale, this);
    }

    public void stopMovement(boolean command)
    {
        if(!command)setDX(getDX()/2);
    }

    public void setDestination(float target)
    {
        float DEAD_ZONE = 0.05f;
        float FAST_ZONE = 0.15f;

        if(target - pos.get_x() > FAST_ZONE)setDX(MAX_DX);
        else if(target - pos.get_x() > DEAD_ZONE)setDX(MAX_DX/3);
        else if(target - pos.get_x() < -FAST_ZONE)setDX(-MAX_DX);       //Don't move in Dead Zone, max speed in after Fast Zone, one third speed between the two;
        else if(target - pos.get_x() < -DEAD_ZONE)setDX(-MAX_DX/3);     //Two velocities to reduce the wiggling around player's finger
    }
}
