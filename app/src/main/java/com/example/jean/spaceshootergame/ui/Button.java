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

    private int _nbSprites = 1;
    private int _currentSprite = 1;
    private Callback _callback;

    public Button(Context context, int image) {
        this(context, image, 1, null);
    }

    public Button(Context context, int image, int nbSprites) {
        this(context, image, nbSprites, null);
    }

    public Button(Context context, int image, Callback callback) {
        this(context, image, 1, callback);
    }

    public Button(Context context, int image, int nbSprites, Callback callback) {
        super(context, image);
        _nbSprites = nbSprites;
        _callback = callback;
        setSprite(_currentSprite);
    }

    public void update(Vect touch) {
        if(touch.get_x() < pos.get_x() + scale.get_x() &&
                touch.get_x() > pos.get_x() - scale.get_x() &&
                touch.get_y() < pos.get_y() + scale.get_y() &&
                touch.get_y() > pos.get_y() - scale.get_y()) {
            _callback.func();
        }
    }

    public void setSprite(int s) {
        if(s > _nbSprites) {
            s = 1;
        }
        if(s < 1) {
            s = _nbSprites;
        }

        _currentSprite = s;

        final float[] textureCoordinateData = {
                _currentSprite*(1.0f/_nbSprites), 0.0f,
                _currentSprite*(1.0f/_nbSprites), 1.0f,
                (_currentSprite-1)*(1.0f/_nbSprites), 1.0f,
                (_currentSprite-1)*(1.0f/_nbSprites), 0.0f
        };
        setTextureCoord(textureCoordinateData);
    }

    public void nextSprite() {
        setSprite(_currentSprite+1);
    }

    public void setCallback(Callback c) {
        _callback = c;
    }
}
