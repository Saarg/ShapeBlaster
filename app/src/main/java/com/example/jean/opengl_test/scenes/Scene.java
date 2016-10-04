package com.example.jean.opengl_test.scenes;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.example.jean.opengl_test.MyGLRenderer;
import com.example.jean.opengl_test.entity.Player;
import com.example.jean.opengl_test.shapes.Triangle;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by jean on 04/10/16.
 */

public class Scene extends MyGLRenderer {

    private Triangle _Triangle;
    private Triangle _Triangle2;
    private Player _player;

    private final String TAG = "Scene";

    private double playerRotationX;
    private double playerAccAngle;
    private final SensorManager sensorManager;
    private final Sensor capt;

    private float playerDx = 0;

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        super.onSurfaceCreated(unused, config);

        // initialize a triangle
        _Triangle = new Triangle();
        _Triangle.y = -0.5f;
        _Triangle.scaleX = _Triangle.scaleY = _Triangle.scaleZ = 0.2f;

        // initialize a triangle
        _Triangle2 = new Triangle();
        _Triangle2.y = -0.5f;
        _Triangle2.x = 0.2f;
        _Triangle2.scaleX = _Triangle2.scaleY = _Triangle2.scaleZ = 0.2f;

        _player = new Player();
        _player.y = -0.8f;
        _player.x = 0.0f;
        _player.scaleX = _player.scaleY = _player.scaleZ = 0.2f;
    }

    public Scene(Context context)
    {
        sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        capt = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Log.d(TAG, "Scene constructed");

    }

    public void onDrawFrame(GL10 unused) {
        super.onDrawFrame(unused);

        sensorManager.registerListener(leListener, capt, SensorManager.SENSOR_DELAY_NORMAL);

        //There's all that's required for player management. Pretty nice is'n it ?
        _player.setDX(playerDx);
        _player.move();
        _player.bound(-1.0f, 1.0f);

        _Triangle.y += 0.09f;
        if(_Triangle.y > 2.0f) {
            _Triangle.y = -2.0f;
        }

        _Triangle2.y += 0.03f;
        if(_Triangle2.y > 2.0f) {
            _Triangle2.y = -2.0f;
        }

        // Draw shapes
        draw(_player);
        draw(_Triangle);
        draw(_Triangle2);
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
