package com.shapeblaster.game.entity;

import android.content.Context;
import android.opengl.GLES20;

import com.shapeblaster.game.MyGLRenderer;
import com.shapeblaster.game.shapes.Circle;
import com.shapeblaster.game.utils.Vect;

/**
 * Created by jean on 05/11/16.
 */

public class Missile extends Circle implements Entity {

    protected float _dx = 0;
    protected float _dy = 0;

    public float getDX() {
        return _dx;
    }

    public void setDX(float newDX) {
        _dx = newDX;
    }

    public float getDY() {
        return _dy;
    }

    public void setDY(float newDY) {
        _dy = newDY;
    }

    public void move(float deltaTime)
    {
        pos.set_x(pos.get_x() + _dx * deltaTime);
        pos.set_y(pos.get_y() + _dy * deltaTime);
    }

    public void shoot() {}

    public void draw(float[] MVPMatrix) {
        super.draw(MVPMatrix);
    }

    public boolean bound(float limitInf, float limitSup)
    {
        if(pos.get_y() < limitInf)
        {
            return false;
        }
        else if(pos.get_y() > limitSup)
        {
            return false;
        }
        return true;
    }

    public boolean isHit(float x, float y) {
        return false;
    }

    public Missile(Context context, float leX, float leY, float squaredScale, float speed, float angle, Vect color)
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

        float angleRad = (float) (angle/360 * 2*Math.PI);

        pos.set_x(leX);
        pos.set_y(leY);
        rot.set_z(angle);
        scale = new Vect(squaredScale/(speed), squaredScale, squaredScale, this);
        setDX((float) (Math.sin(angleRad)*speed));
        setDY((float) (Math.cos(angleRad)*speed));

        this.color = color;

        // Comment for more lights!
        updateModelMatrix();
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
                    "   c.a = 1.0-sqrt(position.x*position.x + position.y*position.y)*2.0;" +
                    "   gl_FragColor = c;" +
                    "}";
}