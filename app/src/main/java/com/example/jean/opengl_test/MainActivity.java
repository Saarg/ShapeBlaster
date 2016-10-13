package com.example.jean.opengl_test;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import com.example.jean.opengl_test.scenes.Scene;

public class MainActivity extends Activity {

    private MyGLSurfaceView _GLView;

    private boolean paused = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        _GLView = new MyGLSurfaceView(this);
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

    public Scene getScene()
    {
        return _scene;
    }

    public MyGLSurfaceView(Context context){
        super(context);

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        _scene = new Scene(context);

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(_scene);

        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }
}