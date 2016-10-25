package com.example.jean.spaceshootergame.ui;

import android.content.Context;

import com.example.jean.spaceshootergame.shapes.TexturedShape;
import com.example.jean.spaceshootergame.utils.Vect;

/**
 * Created by jean on 25/10/16.
 */

public class Button extends TexturedShape {

    public interface Callback {
        void func();
    }

    private Callback _callback;

    public Button(Context context, int image) {
        super(context, image);
    }

    public Button(Context context, int image, Callback callback) {
        this(context, image);
        _callback = callback;
    }

    public void update(Vect touch) {
        if(touch.get_x() < pos.get_x() + scale.get_x() &&
                touch.get_x() > pos.get_x() - scale.get_x() &&
                touch.get_y() < pos.get_y() + scale.get_y() &&
                touch.get_y() > pos.get_y() - scale.get_y()) {
            _callback.func();
        }
    }
}
