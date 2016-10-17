package com.example.jean.spaceshootergame.shapes;

import android.content.Context;
import android.opengl.GLES20;

import com.example.jean.spaceshootergame.MyGLRenderer;

/**
 * Created by jean on 04/10/16.
 */

public class Circle extends Square {
    public Circle(Context context) {
        super(context);

        // Compile shaders if needed
        if (_vertexShader == -1) {
            _vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, _vertexShaderCode);
        }
        if (_fragmentShader == -1) {
            _fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, _fragmentShaderCode);
        }
    }

    public void init() {
        super.init(_vertexShader, _fragmentShader);
    }

    public void init(int vertexShader, int fragmentShader) {
        super.init(vertexShader, fragmentShader);
    }

    // Shaders
    private static int _vertexShader = -1;
    private static String _vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
            "attribute vec2 a_TexCoordinate;" +
            "attribute vec4 vPosition;" +
            "varying vec2 position;" +
            "void main() {" +
            "   position = vPosition.xy;" +
            "   gl_Position = uMVPMatrix * vPosition;" +
            "}";

    private static int _fragmentShader = -1;
    private static String _fragmentShaderCode =
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
