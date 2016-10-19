package com.example.jean.spaceshootergame.entity;

import android.content.Context;
import android.os.SystemClock;

import java.util.ArrayList;

/**
 * Created by jean on 19/10/16.
 */

public class Enemy extends Obstacle implements Entity {

    private long _time;
    protected long _shootingRate = 1000;
    private ArrayList<Missile> _missiles = new ArrayList<>();

    public Enemy(Context context, float leX, float leY, float squaredScale, float dy) {
        super(context, leX, leY, squaredScale, dy);
    }

    public Missile[] shoot() {
        if(SystemClock.uptimeMillis() - _time > _shootingRate) {
            _time = SystemClock.uptimeMillis();
            Missile missiles[];

            _missiles.add(new Missile(_ActivityContext, pos.get_x(), pos.get_y(), 0.15f, 2.0f, 10.0f));
            _missiles.add(new Missile(_ActivityContext, pos.get_x(), pos.get_y(), 0.15f, 2.0f, -10.0f));
            missiles = new Missile[]{
                    _missiles.get(_missiles.size() - 1),
                    _missiles.get(_missiles.size() - 2)
            };

            return missiles;
        }
        return null;
    }
}
