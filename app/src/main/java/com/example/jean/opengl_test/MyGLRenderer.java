package com.example.jean.opengl_test;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.example.jean.opengl_test.shapes.Square;
import com.example.jean.opengl_test.shapes.Triangle;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by jean on 03/10/16.
 */

public class MyGLRenderer implements GLSurfaceView.Renderer {

    private Triangle _Triangle;
    private Triangle _Triangle2;

    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] _MVPMatrix = new float[16];
    private final float[] _ProjectionMatrix = new float[16];
    private final float[] _ViewMatrix = new float[16];

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

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
        float[] scratch = new float[16];

        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(_ViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(_MVPMatrix, 0, _ProjectionMatrix, 0, _ViewMatrix, 0);

        _Triangle.y += 0.09f;
        if(_Triangle.y > 2.0f) {
            _Triangle.y = -2.0f;
        }

        // Draw shape
        _Triangle.draw(_MVPMatrix);
        _Triangle2.draw(_MVPMatrix);
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(_ProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }
}