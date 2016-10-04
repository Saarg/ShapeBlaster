package com.example.jean.opengl_test.scenes;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.example.jean.opengl_test.MyGLRenderer;
import com.example.jean.opengl_test.shapes.Triangle;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by jean on 04/10/16.
 */

public class Scene extends MyGLRenderer {

    private Triangle _Triangle;
    private Triangle _Triangle2;

    private final String TAG = "Scene";

    private double playerRotationX;
    private double playerAccAngle;
    private final SensorManager sensorManager;
    private final Sensor capt;

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


        _Triangle.y += 0.09f;
        if(_Triangle.y > 2.0f) {
            _Triangle.y = -2.0f;
        }

        _Triangle2.y += 0.03f;
        if(_Triangle2.y > 2.0f) {
            _Triangle2.y = -2.0f;
        }

        // Draw shapes
        //draw(_Triangle);
        //draw(_Triangle2);
    }

    private SensorEventListener leListener = new SensorEventListener() {

        final int GRAVITY = 15;

        @Override
        public void onSensorChanged(SensorEvent event) {

            int rotateMax = 50;
            playerRotationX = event.values[0];
            playerAccAngle = (int)(Math.toDegrees(Math.asin(-playerRotationX/GRAVITY)));
            double realAngle = playerAccAngle;
            //Log.d(TAG, playerAccAngle + " Real ");

            if(playerAccAngle > rotateMax)
                playerAccAngle = rotateMax;
            else if(playerAccAngle < -rotateMax)
                playerAccAngle = -rotateMax;
            if(playerAccAngle > 10)
            {
                Log.d(TAG,  realAngle + " >  10");
            }
            else if(playerAccAngle < -10)
            {
                Log.d(TAG, realAngle + " < -10");
            }
            else
            {
                Log.d(TAG, realAngle + " B");
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
}
