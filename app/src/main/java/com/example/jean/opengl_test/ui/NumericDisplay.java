package com.example.jean.opengl_test.ui;

import android.content.Context;

import com.example.jean.opengl_test.R;
import com.example.jean.opengl_test.shapes.TexturedShape;
import com.example.jean.opengl_test.utils.Vect;

import java.util.ArrayList;

/**
 * Created by jean on 12/10/16.
 */

public class NumericDisplay {

    public Vect pos = new Vect(0.65f, 0.9f, 0.0f);

    private ArrayList<TexturedShape> _digits = new ArrayList<>();
    private int _value;

    public NumericDisplay(Context context, int digit) {
        _value = 0;
        for (int i = 0; i < digit ; i++) {
            final float[] textureCoordinateData = {
                    0.2f, 0.0f,
                    0.2f, 0.5f,
                    0.0f, 0.5f,
                    0.0f, 0.0f
            };

            _digits.add(new TexturedShape(context, R.drawable.numbers));
            _digits.get(i).setTextureCoord(textureCoordinateData);
            _digits.get(i).scale.set_x(0.05f);
            _digits.get(i).scale.set_y(0.05f);
            _digits.get(i).pos.set_x(pos.get_x() + (-digit/2f*0.05f + i*0.05f*3));
            _digits.get(i).pos.set_y(pos.get_y());
        }
    }

    public void draw(final float[] mvpMatrix) {
        for (TexturedShape d : _digits) {
            d.draw(mvpMatrix);
        }
    }

    public void setValue(int v) {
        _value = v;

        int tmp = _value;
        for (int i = _digits.size()-1 ; i >= 0 ; i--) {
            final float[] textureCoordinateData = {
                    (tmp%5)*0.2f + 0.2f, tmp/5 *0.5f,
                    (tmp%5)*0.2f + 0.2f, tmp/5 *0.5f + 0.5f,
                    (tmp%5)*0.2f, tmp/5 *0.5f + 0.5f,
                    (tmp%5)*0.2f, tmp/5 *0.5f
            };

            _digits.get(i).setTextureCoord(textureCoordinateData);

            tmp /= 10;
        }
    }
}
