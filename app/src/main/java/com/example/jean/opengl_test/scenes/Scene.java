package com.example.jean.opengl_test.scenes;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.example.jean.opengl_test.MyGLRenderer;
import com.example.jean.opengl_test.entity.Entity;
import com.example.jean.opengl_test.entity.Obstacle;
import com.example.jean.opengl_test.entity.Player;
import com.example.jean.opengl_test.shapes.Circle;
import com.example.jean.opengl_test.shapes.Shape;
import com.example.jean.opengl_test.shapes.Triangle;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by jean on 04/10/16.
 */

public class Scene extends MyGLRenderer {

    private Player _player;
    private Obstacle _obstacle;

    private final String TAG = "Scene";

    private double playerRotationX;
    private double playerAccAngle;
    private final SensorManager sensorManager;
    private final Sensor capt;

    private ArrayList<Entity> _shapes = new ArrayList<>();
    private ArrayList<Obstacle> _obstacles = new ArrayList<>();

    private boolean starting = true;
    private final int _startTime = 2000;
    private final int _waveCD = 3000;
    private final int _obsCD = 300;

    private long lastTime = -1;

    private int indexObs = 0, maxObs = 2;

    private float playerDx = 0;

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        super.onSurfaceCreated(unused, config);

        // initialize a triangle

        _player = new Player(0.0f,-0.8f,0.2f);
        _shapes.add(_player);

        addNewObstacle();
    }

    public Scene(Context context)
    {
        sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        capt = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Log.d(TAG, "Scene constructed");

        sensorManager.registerListener(leListener, capt, SensorManager.SENSOR_DELAY_NORMAL);

        lastTime = System.currentTimeMillis();
    }

    public void onDrawFrame(GL10 unused) {
        super.onDrawFrame(unused);

        _player.setDX(playerDx);

        //Moving all the things and bound them to the screen
        for(Entity s : _shapes)
        {
            s.move();
            s.bound(-1.0f, 1.0f);
        }

        //If Obstacles have reached the bottom screen, they are deleted

        ArrayList<Obstacle> tmp = new ArrayList<>();
        for(Obstacle o : _obstacles)
        {
            if(!o.isOnField())
            {
                tmp.add(o);
                _shapes.remove(o);
            }
        }
        for(Obstacle o : tmp)
        {
            _obstacles.remove(o);
        }

        //At last, draw them all
        for(Entity s : _shapes)
        {
            draw((Shape)s);
        }

        manageObstacleWave();

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
                applyTime = true;
            }
            //Log.d(TAG, "Waiting Beginning");
        }
        else if(indexObs != 0) //We are already launching a new wave
        {
            if(now - lastTime > _obsCD)
            {
                //Log.d(TAG, "Spawning one Obstacle");
                addNewObstacle();
                applyTime = true;
                indexObs++;
            }
            if(indexObs > maxObs)
            {
                //Log.d(TAG, "Spawning last Obstacle");
                indexObs = 0; //Wave Finished
                maxObs++;
            }
        }
        else
        {
            if(now - lastTime > _waveCD)
            {
                indexObs = 1;
                applyTime = true;
            }
            //Log.d(TAG, "Waiting between waves");
        }

        if(applyTime)lastTime = now;
    }

    private void addNewObstacle()
    {
        _obstacle = new Obstacle((float)(Math.random()*2-1), 0.8f, 0.2f, 0.01f);
        _shapes.add(_obstacle);
        _obstacles.add(_obstacle);
    }

    private SensorEventListener leListener = new SensorEventListener() {

        final int GRAVITY = 15;

        @Override
        public void onSensorChanged(SensorEvent event) {

            final int rotateMax = 50, safeZone = 3;
            playerRotationX = event.values[0];
            playerAccAngle = (int)(Math.toDegrees(Math.asin(-playerRotationX/GRAVITY)));

            if(playerAccAngle > rotateMax)
                playerAccAngle = rotateMax;
            else if(playerAccAngle < -rotateMax)
                playerAccAngle = -rotateMax;
            if(playerAccAngle > safeZone)
            {
                playerDx = (float)((playerAccAngle-safeZone)/300);
            }
            else if(playerAccAngle < -safeZone)
            {
                playerDx = (float)((playerAccAngle+safeZone)/300);
            }
            else
            {
                playerDx = 0;
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
}
