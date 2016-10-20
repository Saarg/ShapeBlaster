package com.example.jean.spaceshootergame.scenes;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.jean.spaceshootergame.MyGLRenderer;
import com.example.jean.spaceshootergame.R;
import com.example.jean.spaceshootergame.entity.Enemy;
import com.example.jean.spaceshootergame.entity.Entity;
import com.example.jean.spaceshootergame.entity.Missile;
import com.example.jean.spaceshootergame.entity.Obstacle;
import com.example.jean.spaceshootergame.entity.Player;
import com.example.jean.spaceshootergame.shapes.Shape;
import com.example.jean.spaceshootergame.shapes.TexturedShape;
import com.example.jean.spaceshootergame.ui.NumericDisplay;
import com.example.jean.spaceshootergame.utils.SoundPlayer;

import java.util.ArrayList;
import java.util.Iterator;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by jean on 04/10/16.
 */

public class Scene extends MyGLRenderer {

    public final Context _ActivityContext;

    private Player _player;
    private boolean playerIsAlive = true;

    private NumericDisplay score;

    private final String TAG = "Scene";

    private boolean _playerCommand = false;
    /*
    private final SensorManager sensorManager;
    private final Sensor capt;
    */

    private ArrayList<Entity> _shapes = new ArrayList<>();

    private boolean starting = true;
    private final int _startTime = 1000;
    private final int _waveCD = 5000;
    private final int _obsCD = 300;
    private final int _deathTime = 4000;

    private MediaPlayer _soundtrack;

    private long lastTime = -1;

    // for movement
    private long _time;

    private int indexObs = 0, maxObs = 2;

    private TexturedShape _deathScreen;

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        super.onSurfaceCreated(unused, config);

        score = new NumericDisplay(_ActivityContext, 3);
        score.setValue(0);

        // initialize a triangle
        _player = new Player(_ActivityContext, 0.0f,-0.8f,0.2f);
        _shapes.add(_player);

        _deathScreen = new TexturedShape(_ActivityContext, R.drawable.deathscreen);
        _deathScreen.scale.set_x(0.9f);
        _deathScreen.scale.set_y(0.7f);

        Log.d(TAG, "Resources Loaded");
    }

    public Scene(Context context)
    {
        _ActivityContext = context;
/*
        sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        capt = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        SoundPlayer.initSounds(context);

        sensorManager.registerListener(leListener, capt, SensorManager.SENSOR_DELAY_NORMAL);

*/

        _soundtrack = MediaPlayer.create(context, R.raw.soundtrack);
        _soundtrack.setVolume(0.9f,0.9f);
        _soundtrack.setLooping(true);
        _soundtrack.start();

        lastTime = System.currentTimeMillis();

        Log.d(TAG, "Scene constructed");
    }

    public void onDrawFrame(GL10 unused) {
        super.onDrawFrame(unused);

        long now = SystemClock.uptimeMillis();
        float deltaTime = (float)(now-_time)/1000.0f;
        _time = now;

        if(playerIsAlive) {
            _player.setDestination(_playerCommand);
            _playerCommand = false;

            Iterator<Entity> i = _shapes.iterator();
            while (i.hasNext()) {
                Entity e = i.next();
                // Move
                e.move(deltaTime);

                // Handle world bounds
                if (!e.bound(-1.0f, 1.0f)) { // World bound
                    i.remove();
                    if (e instanceof Obstacle) {
                        _player.incScore(-5);
                    }
                }
                // Handle collisions
                if (e instanceof Obstacle) {
                    Iterator<Missile> j = _player.getMissiles().iterator();
                    while (j.hasNext()) {
                        Missile m = j.next();
                        if (e.isHit(m.pos.get_x(), m.pos.get_y())) { // Player's missile hit Obstacle
                            _player.incScore(1);
                            Log.d(TAG, "Current Score : " + _player.getScore());
                            i.remove();
                            j.remove();

                            SoundPlayer.playSound(_ActivityContext, R.raw.laser_impact);

                            break;
                        }
                    }

                    if (e.isHit(_player.pos.get_x(), _player.pos.get_y())) { // Obstacle hit player
                        managePlayersDeath();
                    }

                }

                // Shoot them all!
                e.shoot();

                // Draw them all
                e.draw(_MVPMatrix);
            }

            manageObstacleWave();
        }

        if(!playerIsAlive)
        {
            _player.draw(_MVPMatrix);
            _deathScreen.draw(_MVPMatrix);
            managePlayersDeath();
        }

        score.setValue(_player.getScore());
        score.draw(_MVPMatrix);
    }

    private void managePlayersDeath()
    {
        if(playerIsAlive)
        {
            lastTime = System.currentTimeMillis();
            playerIsAlive = false;
            Log.d(TAG, "DEAD BITCH");
            _soundtrack.pause();
            SoundPlayer.playSound(_ActivityContext, R.raw.deathsound);
        }
        else
        {
            long now = System.currentTimeMillis();
            if(now - lastTime > _deathTime)
            {
                //Restart the game
                playerIsAlive=true;

                _player.incScore(-_player.getScore());
                starting = true;
                indexObs = 0;
                maxObs = 2;

                _soundtrack.start();

                Iterator<Entity> i = _shapes.iterator();
                while (i.hasNext()) {
                    if (i.next() instanceof Obstacle)
                    {
                        i.remove();
                    }
                }

                lastTime = now;
            }
        }
    }

    private void manageObstacleWave()
    {
        long now = System.currentTimeMillis();

        boolean applyTime = false;

        if(starting)
        {
            if(now - lastTime > _startTime)
            {
                starting = false;
                indexObs = 1;
                applyTime = true;
            }
            Log.d(TAG, "Waiting Beginning");
        }
        else if(indexObs != 0) //We are already launching a new wave
        {
            if(now - lastTime > _obsCD)
            {
                Log.d(TAG, "Spawning one Obstacle");
                addNewObstacle();
                applyTime = true;
                indexObs++;
            }
            if(indexObs > maxObs)
            {
                //Log.d(TAG, "Was last Obstacle");
                indexObs = 0; //Wave Finished
                maxObs = 2+(int) Math.pow(_player.getScore()/10,2);
            }
        }
        else
        {
            if(now - lastTime > _waveCD)
            {
                Log.d(TAG, "Starting new Wave after " + (now - lastTime));
                indexObs = 1;
                applyTime = true;
            }
            //Log.d(TAG, "Waiting between waves");
        }

        if(applyTime)lastTime = now;
    }

    private void addNewObstacle()
    {
        float rotation = (float)Math.random()*400-200;
        if(rotation<45 && rotation > 0)
            rotation = 45;
        else if(rotation < -45 && rotation < 0)
            rotation = -45;

        float size = 0.15f + 0.15f *(float)(Math.random()*0.2-0.1);
        if (Math.random()*1000 > 1000 - _player.getScore()) {
            _shapes.add(new Enemy(_ActivityContext, (float)(Math.random()*1.4-0.7), 1.2f, size, 0.6f));
        } else {
            _shapes.add(new Obstacle(_ActivityContext, (float) (Math.random() * 1.4 - 0.7), 1.2f, size, 0.6f, rotation));
        }

    }

    public void stopMusic()
    {
        _soundtrack.pause();
    }
    public void resumeMusic()
    {
        _soundtrack.start();
    }
    public boolean isMusicPlaying()
    {
        return _soundtrack.isPlaying();
    }

    public void redirectPlayer(float target, int maxSize)
    {
        _playerCommand = true;
        target -= (float)maxSize/2.0f; //Resizing target from [0;MAX] to [-Max/2;Max/2]
        target /= (float)maxSize/2.0f; //Resizing target to [-1;1] According to the OpenGL view
        _player.setDestination(target);
    }
}
