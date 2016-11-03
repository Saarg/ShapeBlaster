package com.example.jean.spaceshootergame;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.example.jean.spaceshootergame.scenes.Scene;
import com.example.jean.spaceshootergame.ui.Button;
import com.example.jean.spaceshootergame.utils.Vect;

/**********************/
/*** B_SAVE v 0.3.3 ***/
/**********************/

public class MainActivity extends Activity {

    private MyGLSurfaceView _GLView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        _GLView = new MyGLSurfaceView(this, size.x, size.y);
        setContentView(_GLView);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        _GLView.getScene().pause();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        //_GLView.getScene().resume();
    }

}

class MyGLSurfaceView extends GLSurfaceView {

    private final Scene _scene;
    private final int _xScreenSize, _yScreenSize;

    public Scene getScene()
    {
        return _scene;
    }

    public MyGLSurfaceView(Context context, int xSize, int ySize){
        super(context);
        _xScreenSize = xSize;
        _yScreenSize = ySize;

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        _scene = new Scene(context, xSize, ySize);

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(_scene);

        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e)
    {
        if(_scene != null)
        {
            if(e.getAction() == MotionEvent.ACTION_UP)
            {
                _scene.stopPlayer();
            }
            else
            {
                _scene.redirectPlayer(e.getX());

                if(e.getAction() == MotionEvent.ACTION_DOWN) {

                    // adjust screen pos to world pos
                    float adjustmentX = (float)_xScreenSize/2.0f;
                    float adjustmentY = (float)_yScreenSize/2.0f;
                    Vect touch = new Vect((e.getX() - adjustmentX) / adjustmentX, (adjustmentY - e.getY()) / adjustmentY, 0.0f);

                    for (Button b : _scene.buttons) {
                        b.update(touch);
                    }
                }
            }

        }
        return true;
    }
}