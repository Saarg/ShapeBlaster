package com.example.jean.spaceshootergame.ui;

import android.content.Context;

import com.example.jean.spaceshootergame.shapes.TexturedShape;
import com.example.jean.spaceshootergame.utils.Vect;

/**
 * Created by jean on 25/10/16.
 */

public class Button extends TexturedShape {

    public interface VectFunction {
        void func(Vect v);
    }

    private VectFunction _callback;
    public Vect _slot;

    public Button(Context context, int image) {
        super(context, image);
    }

    public Button(Context context, int image, VectFunction callback) {
        this(context, image);
        _callback = callback;
    }

    public void update() {
        _callback.func(_slot);
    }
}
