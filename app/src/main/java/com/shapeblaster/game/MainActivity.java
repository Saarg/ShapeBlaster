package com.shapeblaster.game;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.shapeblaster.game.scenes.Scene;
import com.shapeblaster.game.ui.Button;
import com.shapeblaster.game.utils.Vect;

public class MainActivity extends Activity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

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

        //setContentView(_GLView);
        setContentView(R.layout.activity_main);

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.play_button).setOnClickListener(this);
        findViewById(R.id.leaderboard_button).setOnClickListener(this);
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

        // show sign-out button, hide the sign-in button
        findViewById(R.id.sign_in_button).setVisibility(View.GONE);
        findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
        findViewById(R.id.leaderboard_button).setVisibility(View.VISIBLE);
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

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            _SignInClicked = false;
            _ResolvingConnectionFailure = false;
            if (resultCode == RESULT_OK) {
                _apiClient.connect();
            } else {
                // Bring up an error dialog to alert the user that sign-in
                // failed. The R.string.signin_failure should reference an error
                // string in your strings.xml file that tells the user they
                // could not be signed in, such as "Unable to sign in."
                BaseGameUtils.showActivityResultError(this,
                        requestCode, resultCode, R.string.signin_failure);
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sign_in_button) {
            // start the asynchronous sign in flow
            _SignInClicked = true;
            _apiClient.connect();
        } else if (view.getId() == R.id.sign_out_button) {
            // sign out.
            _SignInClicked = false;
            Games.signOut(_apiClient);

            // show sign-in button, hide the sign-out button
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_button).setVisibility(View.GONE);
            findViewById(R.id.leaderboard_button).setVisibility(View.GONE);
        } else if (view.getId() == R.id.play_button) {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            _GLView = new MyGLSurfaceView(this, size.x, size.y);

            setContentView(_GLView);

            if(_GLView != null) {
                _GLView.getScene()._apiClient = _apiClient;
            }
        } else if (view.getId() == R.id.leaderboard_button) {
            if (_apiClient.isConnected()) {
                startActivityForResult(Games.Leaderboards.getLeaderboardIntent(_apiClient, "CgkIifGXkrYBEAIQAA"), 100);
            }
        }
    }

    @Override
    protected void onPause()
    {
        Log.d(TAG, "onPause()");
        super.onPause();
        if(_GLView != null) {
            _GLView.getScene().pause();
        }
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

        _scene = new Scene(context, _xScreenSize, _yScreenSize);

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
