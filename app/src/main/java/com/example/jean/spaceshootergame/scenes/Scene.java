package com.example.jean.spaceshootergame.scenes;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.SystemClock;
import android.util.Log;

import com.example.jean.spaceshootergame.MyGLRenderer;
import com.example.jean.spaceshootergame.R;
import com.example.jean.spaceshootergame.entity.Entity;
import com.example.jean.spaceshootergame.entity.Missile;
import com.example.jean.spaceshootergame.entity.Obstacle;
import com.example.jean.spaceshootergame.entity.Player;
import com.example.jean.spaceshootergame.shapes.Shape;
import com.example.jean.spaceshootergame.shapes.TexturedShape;
import com.example.jean.spaceshootergame.ui.NumericDisplay;
import com.example.jean.spaceshootergame.utils.SoundPlayer;

import java.util.ArrayList;

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

    private final int MAX_ANGLE = 20;

    private final String TAG = "Scene";

    private float playerRotationX;
    private float playerAccAngle;
    private final SensorManager sensorManager;
    private final Sensor capt;

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

    private float playerDx = 0;

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

        sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        capt = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        SoundPlayer.initSounds(context);

        sensorManager.registerListener(leListener, capt, SensorManager.SENSOR_DELAY_NORMAL);

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
            _player.setDestination(playerDx, MAX_ANGLE);


            Missile[] missiles = _player.shoot();
            if (missiles != null) {
                SoundPlayer.playSound(_ActivityContext, R.raw.laser_launch);
                for (Missile m : missiles) {
                    _shapes.add(m);
                }
            }

            ArrayList<Entity> tmp = new ArrayList<>();

            //Moving all the things and bound them to the screen
            for (Entity s : _shapes) {
                s.move(deltaTime);
                //If Entities have reached the bottom screen, they are deleted
                if (!s.bound(-1.0f, 1.0f)) {
                    tmp.add(s);
                    if (s instanceof Obstacle) {
                        _player.incScore(-5);
                    }
                }

                if (s instanceof Obstacle) {
                    for (Entity m : _shapes) {
                        if (m instanceof Missile) {
                            Missile missile = (Missile) (m);
                            if (s.isHit(missile.pos.get_x(), missile.pos.get_y())) {
                                _player.incScore(1);
                                Log.d(TAG, "Current Score : " + _player.getScore());
                                tmp.add(s);
                                tmp.add(missile);


                                SoundPlayer.playSound(_ActivityContext, R.raw.laser_impact);
                            }
                        }
                    }

                    if (s.isHit(_player.pos.get_x(), _player.pos.get_y())) {
                        // DEAD BITCH
                        managePlayersDeath();
                    }
                }

                draw((Shape) s);
            }

            for (Entity e : tmp) {
                _shapes.remove(e);
            }

            manageObstacleWave();
        }

        if(!playerIsAlive)
        {
            draw(_player);
            draw(_deathScreen);
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


                ArrayList<Entity> tmp = new ArrayList<>();

                for(Entity s : _shapes)
                {
                    if (s instanceof Obstacle)
                    {
                        tmp.add(s);
                    }
                }
                for(Entity s : tmp)
                {
                    _shapes.remove(s);
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
        _shapes.add(new Obstacle(_ActivityContext, (float)(Math.random()*1.4-0.7), 1.2f, size, 0.6f, rotation));
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

    private SensorEventListener leListener = new SensorEventListener() {

        final int GRAVITY = 15;

        @Override
        public void onSensorChanged(SensorEvent event)
        {
            playerRotationX = event.values[0];
            playerAccAngle = (int)(Math.toDegrees(Math.asin(-playerRotationX/GRAVITY)));
            //Log.d(TAG, playerAccAngle + " ");

            if(playerAccAngle > MAX_ANGLE)
                playerAccAngle = MAX_ANGLE;
            else if(playerAccAngle < -MAX_ANGLE)
                playerAccAngle = -MAX_ANGLE;

            playerDx = playerAccAngle;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
}
