package com.shapeblaster.game.utils;

import com.shapeblaster.game.shapes.Shape;

/**
 * Created by jean on 05/11/16.
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
        if(_parent != null) {
            _parent.updateModelMatrix();
        }
    }

    public float get_y() {
        return _y;
    }

    public void set_y(float y) {
        this._y = y;
        if(_parent != null) {
            _parent.updateModelMatrix();
        }
    }

    public float get_z() {
        return _z;
    }

    public void set_z(float z) {
        this._z = z;
        if(_parent != null) {
            _parent.updateModelMatrix();
        }
    }

    public double getMagnitude()
    {
        return Math.sqrt(_x*_x + _y*_y + _z*_z);
    }

    public float[] to3Table() {
        float tmp[] = {_x, _y, _z};
        return tmp;
    }

    public float[] to4Table() {
        float tmp[] = {_x, _y, _z, 1.0f};
        return tmp;
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
        this(x, y, z, null);
    }

    public Vect(float x, float y, float z, Shape parent) {
        _x = x;
        _y = y;
        _z = z;

        _parent = parent;
    }
}
