package com.example.jean.opengl_test.entity;

/**
 * Created by Kwarthys on 04/10/2016.
 */

public interface Entity {

    public void move();

    public boolean bound(float limitInf, float limitSup);
}
