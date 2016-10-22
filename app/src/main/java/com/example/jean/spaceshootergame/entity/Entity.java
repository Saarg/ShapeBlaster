package com.example.jean.spaceshootergame.entity;

import com.example.jean.spaceshootergame.utils.Vect;

/**
 * Created by Kwarthys on 04/10/2016.
 */

public interface Entity {

    public void move(float deltaTime);

    public void shoot();

    public void draw(float[] MVPMatrix);

    public boolean bound(float limitInf, float limitSup);

    public boolean isHit(float x, float y);
}
