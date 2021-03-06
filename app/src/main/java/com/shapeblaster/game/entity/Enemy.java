package com.shapeblaster.game.entity;

import android.content.Context;
import android.os.SystemClock;

import java.util.ArrayList;

/**
 * Created by jean on 05/11/16.
 */

public class Enemy extends Obstacle implements Entity {

    private long _time;
    protected long _shootingRate = 1000;
    private ArrayList<Entity> _entities = null;
    private ArrayList<Missile> _missiles = new ArrayList<>();

    public Enemy(Context context, float leX, float leY, float squaredScale, float dy) {
        super(context, leX, leY, squaredScale, dy);
    }

    public void move(float deltaTime) {
        super.move(deltaTime);

        for (Missile m : _missiles) {
            m.move(deltaTime);
        }
    }

    public void shoot() {
        if(SystemClock.uptimeMillis() - _time > 1.5*_shootingRate) {// shot was missed maybe game was paused
            _time = SystemClock.uptimeMillis();
        }
        if(SystemClock.uptimeMillis() - _time > _shootingRate) {
            _time = SystemClock.uptimeMillis();

            _entities.add(new Missile(_ActivityContext, pos.get_x(), pos.get_y(), 0.10f, -1.0f, 10.0f, this.color));
            _entities.add(new Missile(_ActivityContext, pos.get_x(), pos.get_y(), 0.10f, -1.0f, -10.0f, this.color));
        }
    }

    public void draw(float[] MVPMatrix) {
        super.draw(MVPMatrix);
    }

    public boolean bound(float limitInf, float limitSup) {
        return super.bound(limitInf, limitSup);
    }

    public final ArrayList<Missile> getMissiles() { return _missiles; }
    public void setMissilesArray(ArrayList<Entity> a) { _entities = a; }
}