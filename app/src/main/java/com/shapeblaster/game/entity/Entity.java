package com.shapeblaster.game.entity;

/**
 * Created by jean on 05/11/16.
 */

public interface Entity {

    void move(float deltaTime);

    void shoot();

    void draw(float[] MVPMatrix);

    boolean bound(float limitInf, float limitSup);

    boolean isHit(float x, float y);
}