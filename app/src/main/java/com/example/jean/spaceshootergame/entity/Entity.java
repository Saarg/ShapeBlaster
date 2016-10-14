package com.example.jean.spaceshootergame.entity;

/**
 * Created by Kwarthys on 04/10/2016.
 */

public interface Entity {

    public void move(float deltaTime);

    public boolean bound(float limitInf, float limitSup);

    public boolean isHit(float x, float y);
}
