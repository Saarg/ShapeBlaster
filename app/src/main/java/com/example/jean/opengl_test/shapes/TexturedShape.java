package com.example.jean.opengl_test.shapes;

import android.content.Context;

import com.example.jean.opengl_test.R;

/**
 * Created by jean on 12/10/16.
 */

public class TexturedShape extends Shape {
    static float texturedShapeCoords[] = {
            -1.0f,  1.0f, 0.0f,   // top left
            -1.0f, -1.0f, 0.0f,   // bottom left
            1.0f, -1.0f, 0.0f,   // bottom right
            1.0f,  1.0f, 0.0f }; // top right

    static short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices

    public TexturedShape(Context context, int texture) {
        super(context);
        super.init(texturedShapeCoords, drawOrder, texture);
    }

    public void init() {
        super.init(texturedShapeCoords, drawOrder);
    }

    public void init(String vertexShaderCode, String fragmentShaderCode) {
        super.init(texturedShapeCoords, drawOrder, vertexShaderCode, fragmentShaderCode);
    }
}
