package com.example.jean.opengl_test.shapes;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.example.jean.opengl_test.MyGLRenderer;
import com.example.jean.opengl_test.R;
import com.example.jean.opengl_test.utils.Vect;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by jean on 04/10/16.
 */

public class Shape {

    protected final Context _ActivityContext;

    // Coordinates
    public Vect pos;
    public Vect rot;
    public Vect scale;

    // Shaders
    private final String _vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
            "attribute vec2 a_TexCoordinate;" +
            "attribute vec4 vPosition;" +
            "varying vec2 v_TexCoordinate;" +
            "void main() {" +
            "   v_TexCoordinate = a_TexCoordinate;" +
            "   gl_Position = uMVPMatrix * vPosition;" +
            "}";

    private final String _fragmentShaderCode =
            "precision mediump float;" +
            "uniform sampler2D u_Texture;" +
            "varying vec2 v_TexCoordinate;" +
            "uniform vec4 vColor;" +
            "void main() {" +
            "   gl_FragColor = vColor;" +
            "}";

    private final String _fragmentTextShaderCode =
            "precision mediump float;" +
            "uniform sampler2D u_Texture;" +
            "varying vec2 v_TexCoordinate;" +
            "uniform vec4 vColor;" +
            "void main() {" +
            "   gl_FragColor = vColor * texture2D(u_Texture, v_TexCoordinate);" +
            "}";

    // Use to access and set the view transformation
    private int mMVPMatrixHandle;

    // vertex and draw order
    private FloatBuffer vertexBuffer;
    private ShortBuffer drawListBuffer;
    private int mProgram;

    // Number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    private float _coords[];
    private short _drawOrder[];

    // Set color with red, green, blue and alpha (opacity) values
    public Vect color = new Vect(0.63671875f, 0.76953125f, 0.22265625f);

    // Shaders handles
    private int mPositionHandle;
    private int mColorHandle;

    // Vertex vars
    private int vertexCount;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    // model matrix
    private float[] _PosMatrix = new float[16];
    private float[] _RotationMatrix = new float[16];
    private float[] _ScaleMatrix = new float[16];
    private float[] _ModelMatrix = new float[16];

    // Texture
    public boolean textured = false;
    private FloatBuffer _CubeTextureCoordinates;
    private int _TextureUniformHandle;
    private int _TextureCoordinateHandle;
    private final int _TextureCoordinateDataSize = 2;
    private int _TextureDataHandle;

    public Shape(Context context) {
        _ActivityContext = context;

        pos = new Vect(this);
        rot = new Vect(this);
        scale = new Vect(1.0f, 1.0f, 1.0f, this);

        updateModelMatrix();
    }

    protected void init(final float coords[]) {
        final short drawOrder[] = { 0, 1, 2 };
        init(coords, drawOrder);
    }

    protected void init(final float coords[], final short drawOrder[]) {
        init(coords, drawOrder, _vertexShaderCode, _fragmentShaderCode, -1);
    }

    protected void init(final float coords[], final short drawOrder[], int texture) {
        init(coords, drawOrder, _vertexShaderCode, _fragmentTextShaderCode, texture);
    }

    protected void init(final float coords[], final short drawOrder[], String vertexShaderCode, String fragmentShaderCode) {
        init(coords, drawOrder, vertexShaderCode, fragmentShaderCode, -1);
    }

    protected void init(final float coords[], final short drawOrder[], String vertexShaderCode, String fragmentShaderCode, int texture) {
        setShaders(vertexShaderCode, fragmentShaderCode);

        if (texture != -1) {
            setTexture(texture);
        }

        // Set the shape coords
        _coords = coords;
        _drawOrder = drawOrder;
        vertexCount = _coords.length / COORDS_PER_VERTEX;

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer vertexbb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                _coords.length * 4);
        // use the device hardware's native byte order
        vertexbb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = vertexbb.asFloatBuffer();
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

    public void setShaders(String vertexShaderCode, String fragmentShaderCode) {
        // set shaders
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

    public void setTexture(int texture) {
        final float[] cubeTextureCoordinateData = {
                1.0f, 0.0f,
                1.0f, 1.0f,
                0.0f, 1.0f,
                0.0f, 0.0f
        };

        setTextureCoord(cubeTextureCoordinateData);

        _TextureDataHandle = MyGLRenderer.loadTexture(_ActivityContext, texture);
        textured = true;
    }

    public void setTextureCoord(float[] cubeTextureCoordinate) {
        // initialize _CubeTextureCoordinates byte buffer for texture coordinates
        ByteBuffer textureBuffer = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                cubeTextureCoordinate.length * 4);
        // use the device hardware's native byte order
        textureBuffer.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        _CubeTextureCoordinates = textureBuffer.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        _CubeTextureCoordinates.put(cubeTextureCoordinate);
        // set the buffer to read the first coordinate
        _CubeTextureCoordinates.position(0);
    }

    public void setColor(final Vect c) {
        color = c;
    }

    public final Vect getColor() {
        return color;
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
        Matrix.multiplyMM(TempMatrix, 0, _ModelMatrix, 0, mvpMatrix, 0);

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
        GLES20.glUniform4fv(mColorHandle, 1, color.to4Table(), 0);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, TempMatrix, 0);

        if (textured) {
            _TextureCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "a_TexCoordinate");
            GLES20.glEnableVertexAttribArray(_TextureCoordinateHandle);
            // Prepare the texture coordinates
            GLES20.glVertexAttribPointer(_TextureCoordinateHandle, _TextureCoordinateDataSize,
                    GLES20.GL_FLOAT, false,
                    0, _CubeTextureCoordinates);

            _TextureUniformHandle = GLES20.glGetUniformLocation(mProgram, "u_Texture");
            // Set the active texture unit to texture unit 0.
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

            // Bind the texture to this unit.
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, _TextureDataHandle);

            // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
            GLES20.glUniform1i(_TextureUniformHandle, 0);
        }

        // Draw the shape
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, _drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}
