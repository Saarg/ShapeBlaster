package com.example.jean.opengl_test.entity;

/**
 * Created by Kwarthys on 04/10/2016.
 */

public interface Entity {

    public final String name = "Entity";

    public void move(float deltaTime);

    public boolean bound(float limitInf, float limitSup);

    public boolean isHit(float x, float y);
}
