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

    private ArrayList<TexturedShape> digits = new ArrayList<>();

    public NumericDisplay(Context context, int digit) {
        for (int i = 0; i < digit ; i++) {
            digits.add(new TexturedShape(context, R.drawable.numbers));
            digits.get(i).scale.set_x(0.05f);
            digits.get(i).scale.set_y(0.05f);
            digits.get(i).pos.set_x(pos.get_x() + (-digit/2f*0.05f + i*0.05f*3));
            digits.get(i).pos.set_y(pos.get_y());
        }
    }

    public void draw(final float[] mvpMatrix) {
        for (TexturedShape d : digits) {
            d.draw(mvpMatrix);
        }
    }
}
