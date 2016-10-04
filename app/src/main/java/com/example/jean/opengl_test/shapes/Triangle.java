package com.example.jean.opengl_test.shapes;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.example.jean.opengl_test.MyGLRenderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by jean on 03/10/16.
 */

public class Triangle {

    public float x = 0.0f, y = 0.0f, z = 0.0f;
    public float rotX = 0.0f, rotY = 0.0f, rotZ = 0.0f;
    public float scaleX = 1.0f, scaleY = 1.0f, scaleZ = 1.0f;

    private final String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
            "void main() {" +
            // the matrix must be included as a modifier of gl_Position
            // Note that the uMVPMatrix factor *must be first* in order
            // for the matrix multiplication product to be correct.
            "  gl_Position = uMVPMatrix * vPosition;" +
            "}";

    // Use to access and set the view transformation
    private int mMVPMatrixHandle;


    private final String fragmentShaderCode =
            "precision mediump float;" +
            "uniform vec4 vColor;" +
            "void main() {" +
            "  gl_FragColor = vColor;" +
            "}";

    private FloatBuffer vertexBuffer;
    private final int mProgram;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static float triangleCoords[] = {   // in counterclockwise order:
            0.0f,  0.622008459f, 0.0f, // top
            -0.5f, -0.311004243f, 0.0f, // bottom left
            0.5f, -0.311004243f, 0.0f  // bottom right
    };

    // Set color with red, green, blue and alpha (opacity) values
    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

    private int mPositionHandle;
    private int mColorHandle;

    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    private float[] _PosMatrix = new float[16];
    private float[] _RotationMatrix = new float[16];
    private float[] _ScaleMatrix = new float[16];
    private float[] _ModelMatrix = new float[16];

    public Triangle() {
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                triangleCoords.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(triangleCoords);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);

        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        // create empty OpenGL ES Program
        mProgram = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader(mProgram, vertexShader);

        // add the fragment shader to program
        GLES20.glAttachShader(mProgram, fragmentShader);

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(mProgram);

    }

    public void draw(float[] mvpMatrix) {
        // Set position and rotation
        Matrix.setIdentityM(_ModelMatrix, 0); // set identity
        Matrix.setIdentityM(_PosMatrix, 0); // set identity
        Matrix.setIdentityM(_RotationMatrix, 0); // set identity
        Matrix.setIdentityM(_ScaleMatrix, 0); // set identity

        Matrix.translateM(_PosMatrix, 0, x, y, z); // translation to the left

        Matrix.setRotateM(_RotationMatrix, 0, rotX, rotY, rotZ, -1.0f); // Rotation

        Matrix.scaleM(_ScaleMatrix, 0, scaleX, scaleY, scaleZ);

        float[] TempMatrix = new float[16];
        Matrix.multiplyMM(TempMatrix, 0, _PosMatrix, 0, _RotationMatrix, 0);
        Matrix.multiplyMM(_ModelMatrix, 0, TempMatrix, 0, _ScaleMatrix, 0);

        TempMatrix = mvpMatrix.clone();
        Matrix.multiplyMM(mvpMatrix, 0, TempMatrix, 0, _ModelMatrix, 0);

        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}