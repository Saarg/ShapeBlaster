package com.example.jean.opengl_test.shapes;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.example.jean.opengl_test.MyGLRenderer;
import com.example.jean.opengl_test.utils.Vect;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by jean on 04/10/16.
 */

public class Shape {

    public Vect pos;
    public Vect rot;
    public Vect scale;

    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
            "void main() {" +
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
    private ShortBuffer drawListBuffer;
    private final int mProgram;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    private float _coords[];
    private short _drawOrder[];

    // Set color with red, green, blue and alpha (opacity) values
    public float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

    private int mPositionHandle;
    private int mColorHandle;

    private int vertexCount;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    private float[] _PosMatrix = new float[16];
    private float[] _RotationMatrix = new float[16];
    private float[] _ScaleMatrix = new float[16];
    private float[] _ModelMatrix = new float[16];

    public Shape() {
        pos = new Vect(this);
        rot = new Vect(this);
        scale = new Vect(1.0f, 1.0f, 1.0f, this);

        updateModelMatrix();

        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        // create empty OpenGL ES Program
        mProgram = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader(mProgram, vertexShader);

        // add the fragment shader to program
        GLES20.glAttachShader(mProgram, fragmentShader);

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(mProgram);
    }

    protected void init(final float coords[]) {
        final short drawOrder[] = { 0, 1, 2 };
        init(coords, drawOrder);
    }

    protected void init(final float coords[], final short drawOrder[]) {
        // Set the shape coords
        _coords = coords;
        _drawOrder = drawOrder;
        vertexCount = _coords.length / COORDS_PER_VERTEX;

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                _coords.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(_coords);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);
    }

    public void updateModelMatrix() {
        // Reset matrixs
        Matrix.setIdentityM(_ModelMatrix, 0); // set identity
        Matrix.setIdentityM(_PosMatrix, 0); // set identity
        Matrix.setIdentityM(_RotationMatrix, 0); // set identity
        Matrix.setIdentityM(_ScaleMatrix, 0); // set identity

        Matrix.translateM(_PosMatrix, 0, pos.get_x(), pos.get_y(), pos.get_z()); // translation

        Matrix.setRotateEulerM(_RotationMatrix, 0, rot.get_x(), rot.get_y(), rot.get_z());

        Matrix.scaleM(_ScaleMatrix, 0, scale.get_x(), scale.get_y(), scale.get_z()); // Scaling

        float[] TempMatrix = new float[16];
        Matrix.multiplyMM(TempMatrix, 0, _PosMatrix, 0, _RotationMatrix, 0);
        Matrix.multiplyMM(_ModelMatrix, 0, TempMatrix, 0, _ScaleMatrix, 0);
    }

    public void draw(final float[] mvpMatrix) {
        float[] TempMatrix = _ModelMatrix.clone();
        Matrix.multiplyMM(_ModelMatrix, 0, TempMatrix, 0, mvpMatrix, 0);

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

        // Draw the shape
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, _drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, _ModelMatrix, 0);

        // Draw the shape
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, _drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}
