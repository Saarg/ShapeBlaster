package com.example.jean.spaceshootergame.entity;

import android.content.Context;
import android.opengl.GLES20;

import com.example.jean.spaceshootergame.MyGLRenderer;
import com.example.jean.spaceshootergame.shapes.Square;
import com.example.jean.spaceshootergame.utils.Vect;

/**
 * Created by Kwarthys on 04/10/2016.
 */

public class Obstacle extends Square implements Entity{

    private float dy = 0;
    private float rotDZ = 0;

    private boolean isOnField = true;

    public boolean isOnField(){return isOnField;}

    public void setDY(float newDY){dy = newDY;}

    public void move(float deltaTime)
    {
        pos.set_y(pos.get_y() - dy*deltaTime);
        rot.set_z(rot.get_z() - rotDZ*deltaTime);
    }

    public void shoot() {}

    public void draw(float[] MVPMatrix) {
        super.draw(MVPMatrix);
    }

    public boolean bound(float limitInf, float limitSup)
    {
        limitSup+=0.3;
        limitInf+=0.1;
        if(pos.get_y() < limitInf) {
            isOnField = false;
        }
        else if(pos.get_y() > limitSup) {
            pos.set_y(limitSup);
        }

        return isOnField;
    }

    public boolean isHit(float x, float y) {
        return (pos.get_x() + scale.get_x() > x && pos.get_x() - scale.get_x() < x && pos.get_y() + scale.get_y() > y && pos.get_y() - scale.get_y() < y);
    }

    public Obstacle(Context context, float leX, float leY, float squaredScale, float dy)
    {
        super(context);

        // Compile shaders if needed
        if (_vertexShader == -1) {
            _vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, _vertexShaderCode);
        }
        if (_fragmentShader == -1) {
            _fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, _fragmentShaderCode);
        }

        super.init(_vertexShader, _fragmentShader);
        pos.set_x(leX);
        pos.set_y(leY);
        scale = new Vect(squaredScale, squaredScale, squaredScale, this);
        setDY(dy);

        setColor(new Vect(0.63671875f, 0.22265625f, 0.22265625f));
    }

    public Obstacle(Context context, float leX, float leY, float squaredScale, float dy, float rot)
    {
        this(context, leX, leY, squaredScale, dy);
        rotDZ = rot;
    }

    // Shaders
    private static int _vertexShader = -1;
    static String _vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
            "attribute vec2 a_TexCoordinate;" +
            "attribute vec4 vPosition;" +
            "varying vec2 position;" +
            "void main() {" +
            "   position = vPosition.xy;" +
            "   gl_Position = uMVPMatrix * vPosition;" +
            "}";

    private static int _fragmentShader = -1;
    static String _fragmentShaderCode =
            "precision mediump float;" +
            "uniform sampler2D u_Texture;" +
            "uniform vec4 vColor;" +
            "varying vec2 position;" +
            "void main() {" +
            "   vec4 c = vColor;" +
            "   if(sqrt(position.x*position.x + position.y*position.y) < 0.55) {" +
            "       c.a = 1.0;" +
            "   } else { c.a = 0.0; }" +
            "   gl_FragColor = c;" +
            "}";
}
