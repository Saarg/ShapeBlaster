package com.example.jean.spaceshootergame.shapes;

import android.content.Context;

/**
 * Created by jean on 04/10/16.
 */

public class Circle extends Square {
    public Circle(Context context) {
        super(context);
    }

    public void init() {
        super.init(_vertexShaderCode, _fragmentShaderCode);
    }

    public void init(String vertexShaderCode, String fragmentShaderCode) {
        super.init(vertexShaderCode, fragmentShaderCode);
    }

    // Shaders
    static String _vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
            "attribute vec2 a_TexCoordinate;" +
            "attribute vec4 vPosition;" +
            "varying vec2 position;" +
            "void main() {" +
            "   position = vPosition.xy;" +
            "   gl_Position = uMVPMatrix * vPosition;" +
            "}";

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
