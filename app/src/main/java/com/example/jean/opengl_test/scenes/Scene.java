package com.example.jean.opengl_test.scenes;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;

import com.example.jean.opengl_test.MyGLRenderer;
import com.example.jean.opengl_test.R;
import com.example.jean.opengl_test.entity.Entity;
import com.example.jean.opengl_test.entity.Missile;
import com.example.jean.opengl_test.entity.Obstacle;
import com.example.jean.opengl_test.entity.Player;
import com.example.jean.opengl_test.shapes.Circle;
import com.example.jean.opengl_test.shapes.Shape;
import com.example.jean.opengl_test.shapes.TexturedShape;
import com.example.jean.opengl_test.shapes.Triangle;
import com.example.jean.opengl_test.ui.NumericDisplay;
import com.example.jean.opengl_test.utils.SoundPlayer;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.content.ContentValues.TAG;

/**
 * Created by jean on 04/10/16.
 */

public class Scene extends MyGLRenderer {

    public final Context _ActivityContext;

    private Player _player;
    private NumericDisplay score;

    private final int MAX_ANGLE = 25;

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

    private MediaPlayer _soundtrack;

    private long lastTime = -1;

    // for movement
    private long _time;

    private int indexObs = 0, maxObs = 2;

    private float playerDx = 0;

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        super.onSurfaceCreated(unused, config);

        score = new NumericDisplay(_ActivityContext, 3);

        // initialize a triangle
        _player = new Player(_ActivityContext, 0.0f,-0.8f,0.2f);
        _shapes.add(_player);
    }

    public Scene(Context context)
    {
        _ActivityContext = context;

        sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        capt = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Log.d(TAG, "Scene constructed");

        SoundPlayer.initSounds(context);

        sensorManager.registerListener(leListener, capt, SensorManager.SENSOR_DELAY_NORMAL);

        //_soundtrack = MediaPlayer.create(context, R.raw.soundtrack);
        //_soundtrack.setVolume(0.9f,0.9f);
        //_soundtrack.setLooping(true);
        //_soundtrack.start();

        lastTime = System.currentTimeMillis();
    }

    public void onDrawFrame(GL10 unused) {
        super.onDrawFrame(unused);

        long now = SystemClock.uptimeMillis();
        float deltaTime = (float)(now-_time)/1000.0f;
        _time = now;

        _player.setDestination(playerDx, MAX_ANGLE);

        Missile[] missiles = _player.shoot();
        if(missiles != null) {
           //SoundPlayer.playSound(_ActivityContext,R.raw.laser_launch);
            for (Missile m : missiles) {
                _shapes.add(m);
            }
        }

        ArrayList<Entity> tmp = new ArrayList<>();

        //Moving all the things and bound them to the screen
        for(Entity s : _shapes)
        {
            s.move(deltaTime);
            //If Obstacles have reached the bottom screen, they are deleted
            if(!s.bound(-1.0f, 1.0f))
            {
                tmp.add(s);
            }

            if(s instanceof Obstacle) {
                for (Entity m : _shapes) {
                    if(m instanceof Missile) {
                        Missile missile = (Missile)(m);
                        if(s.isHit(missile.pos.get_x(), missile.pos.get_y())) {
                            _player.incScore(1);
                            Log.d(TAG, "Current Score : " + _player.getScore());
                            tmp.add(s);
                            tmp.add(missile);


                            //SoundPlayer.playSound(_ActivityContext,R.raw.laser_impact);
                        }
                    }
                }
            }

            draw((Shape)s);
        }

        for(Entity e : tmp)
        {
            _shapes.remove(e);
        }

        manageObstacleWave();

        score.draw(_MVPMatrix);
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
                Log.d(TAG, "Was last Obstacle");
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
