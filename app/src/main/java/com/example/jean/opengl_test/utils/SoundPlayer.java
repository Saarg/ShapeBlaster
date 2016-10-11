package com.example.jean.opengl_test.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import com.example.jean.opengl_test.R;

import java.util.HashMap;

/**
 * Created by Kwarthys on 11/10/2016.
 */

public class SoundPlayer {

    public static final int _launchSound = R.raw.laser_launch;
    public static final int _impactSound = R.raw.laser_impact;

    private static SoundPool _soundPool;

    private static HashMap _soundPoolMap;

    public static void initSounds(Context context)
    {
        _soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC,100);

        _soundPoolMap = new HashMap(2);
        _soundPoolMap.put( _launchSound, _soundPool.load(context, R.raw.laser_launch, 1) );
        _soundPoolMap.put( _impactSound, _soundPool.load(context, R.raw.laser_impact, 2) );
    }

    public static void playSound(Context context, int soundID) {
        if(_soundPool == null || _soundPoolMap == null){
            initSounds(context);
        }
        float volume = 0.5f;// whatever in the range = 0.0 to 1.0
        // play sound with same right and left volume, with a priority of 1,
        // zero repeats (i.e play once), and a playback rate of 1f
        _soundPool.play((int)_soundPoolMap.get(soundID), volume, volume, 1, 0, 1f);
        Log.d("Sound", "played");
    }
}
