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

/*********************/
/*** Version 0.1.1 ***/
/*********************/

public class MainActivity extends Activity {

    private MyGLSurfaceView _GLView;

    private boolean paused = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        _GLView = new MyGLSurfaceView(this, width);
        setContentView(_GLView);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        paused = true;
        if(_GLView.getScene().isMusicPlaying())_GLView.getScene().stopMusic();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(paused)_GLView.getScene().resumeMusic();
    }

}

class MyGLSurfaceView extends GLSurfaceView {

    private final Scene _scene;

    private int _sizeOfXScreen;

    public Scene getScene()
    {
        return _scene;
    }

    public MyGLSurfaceView(Context context, int xSize){
        super(context);
        _sizeOfXScreen = xSize;

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        _scene = new Scene(context, _sizeOfXScreen);

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
            }

        }
        return true;
    }
}