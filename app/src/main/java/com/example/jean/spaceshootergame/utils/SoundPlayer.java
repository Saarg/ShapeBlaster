package com.example.jean.spaceshootergame.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.example.jean.spaceshootergame.R;

import java.util.HashMap;

/**
 * Created by Kwarthys on 11/10/2016.
 */

public class SoundPlayer {

    private static final int _launchSound = R.raw.laser_launch;
    private static final int _impactSound = R.raw.laser_impact;
    private static final int _deathSound = R.raw.deathsound;

    private static SoundPool _soundPool;

    private static HashMap _soundPoolMap;

    private static boolean _muted = false;

    public static void initSounds(Context context)
    {
        _soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC,100);

        _soundPoolMap = new HashMap(3);
        _soundPoolMap.put( _deathSound, _soundPool.load(context, R.raw.deathsound, 1) );
        _soundPoolMap.put( _launchSound, _soundPool.load(context, R.raw.laser_launch, 2) );
        _soundPoolMap.put( _impactSound, _soundPool.load(context, R.raw.laser_impact, 3) );
    }

    public static void playSound(Context context, int soundID) {
        if(_muted) { return; }

        if(_soundPool == null || _soundPoolMap == null){
            initSounds(context);
        }
        float volume = 0.25f;// whatever in the range = 0.0 to 1.0
        // play sound with same right and left volume, with a priority of 1,
        // zero repeats (i.e play once), and a playback rate of 1f
        if(soundID==R.raw.deathsound)volume=0.9f;
        _soundPool.play((int)_soundPoolMap.get(soundID), volume, volume, 1, 0, 1f);
    }

    public static void muteAll(boolean m) {
        _muted = m;
    }
}
