package com.example.jean.opengl_test.utils;

import com.example.jean.opengl_test.shapes.Shape;

import java.util.Objects;

/**
 * Created by jean on 06/10/16.
 */

public class Vect {
    protected float _x = 0.0f;
    protected float _y = 0.0f;
    protected float _z = 0.0f;

    private Shape _parent = null;

    public float get_x() {
        return _x;
    }

    public void set_x(float x) {
        this._x = x;
        _parent.updateModelMatrix();
    }

    public float get_y() {
        return _y;
    }

    public void set_y(float y) {
        this._y = y;
        _parent.updateModelMatrix();
    }

    public float get_z() {
        return _z;
    }

    public void set_z(float z) {
        this._z = z;
        _parent.updateModelMatrix();
    }

    public Shape getParent() {
        return _parent;
    }

    public void setParent(Shape parent) {
        this._parent = parent;
    }

    public Vect() {
        this(0.0f, 0.0f, 0.0f);
    }

    public Vect(Shape parent) {
        this(0.0f, 0.0f, 0.0f, parent);
    }

    public Vect(float x, float y, float z) {
        this(0.0f, 0.0f, 0.0f, null);
    }

    public Vect(float x, float y, float z, Shape parent) {
        _x = x;
        _y = y;
        _z = z;

        _parent = parent;
    }
}
