package com.example.jean.spaceshootergame.entity;

import com.example.jean.spaceshootergame.utils.Vect;

/**
 * Created by Kwarthys on 04/10/2016.
 */

public interface Entity {

    void move(float deltaTime);

    void shoot();

    void draw(float[] MVPMatrix);

    boolean bound(float limitInf, float limitSup);

    boolean isHit(float x, float y);
}
