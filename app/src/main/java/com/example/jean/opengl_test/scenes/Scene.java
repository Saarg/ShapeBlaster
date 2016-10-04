package com.example.jean.opengl_test.scenes;

import com.example.jean.opengl_test.MyGLRenderer;
import com.example.jean.opengl_test.shapes.Triangle;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by jean on 04/10/16.
 */

public class Scene extends MyGLRenderer {

    private Triangle _Triangle;
    private Triangle _Triangle2;

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        super.onSurfaceCreated(unused, config);

        // initialize a triangle
        _Triangle = new Triangle();
        _Triangle.y = -0.5f;
        _Triangle.scaleX = _Triangle.scaleY = _Triangle.scaleZ = 0.2f;

        // initialize a triangle
        _Triangle2 = new Triangle();
        _Triangle2.y = -0.5f;
        _Triangle2.x = -0.2f;
        _Triangle2.scaleX = _Triangle2.scaleY = _Triangle2.scaleZ = 0.2f;
    }

    public void onDrawFrame(GL10 unused) {
        super.onDrawFrame(unused);


        _Triangle.y += 0.09f;
        if(_Triangle.y > 2.0f) {
            _Triangle.y = -2.0f;
        }

        _Triangle.y += 0.03f;
        if(_Triangle.y > 2.0f) {
            _Triangle.y = -2.0f;
        }

        // Draw shapes
        draw(_Triangle);
        draw(_Triangle2);
    }
}
