package com.shapeblaster.game;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.shapeblaster.game.scenes.Scene;
import com.shapeblaster.game.ui.Button;
import com.shapeblaster.game.utils.Vect;

public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "ShapeBlaster";

    private MyGLSurfaceView _GLView;

    // google signin
    private static int RC_SIGN_IN = 9001;
    private GoogleApiClient _apiClient;
    private boolean _ResolvingConnectionFailure = false;
    private boolean _AutoStartSignInflow = true;
    private boolean _SignInClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);

        _apiClient =  new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();

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
    protected void onStart() {
        Log.d(TAG, "onStart()");
        super.onStart();
        _apiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected() called. Sign in successful!");
        _GLView.getScene()._apiClient = _apiClient;
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended() called. Trying to reconnect.");
        _apiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed() called, result: " + connectionResult);

        if (_ResolvingConnectionFailure) {
            Log.d(TAG, "onConnectionFailed() ignoring connection failure; already resolving.");
            return;
        }

        // If the sign in button was clicked or if auto sign-in is enabled,
        // launch the sign-in flow
        if (_SignInClicked || _AutoStartSignInflow) {
            _AutoStartSignInflow = false;
            _SignInClicked = false;
            _ResolvingConnectionFailure = true;

            // Attempt to resolve the connection failure using BaseGameUtils.
            // The R.string.signin_other_error value should reference a generic
            // error string in your strings.xml file, such as "There was
            // an issue with sign in, please try again later."
            if (!BaseGameUtils.resolveConnectionFailure(this,
                    _apiClient, connectionResult,
                    RC_SIGN_IN, "There was an issue with sign in, please try again later.")) {
                _ResolvingConnectionFailure = false;
            }
        }

        // Put code here to display the sign-in button
    }


    @Override
    protected void onPause()
    {
        Log.d(TAG, "onPause()");
        super.onPause();
        _GLView.getScene().pause();
    }

    @Override
    protected void onResume()
    {
        Log.d(TAG, "onResume()");
        super.onResume();
    }

    protected void onStop() {
        Log.d(TAG, "onStop()");
        super.onStop();
        if (_apiClient.isConnected()) {
            _apiClient.disconnect();
        }
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
                if(e.getY() > 0.2*_yScreenSize)_scene.redirectPlayer(e.getX());

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
