package com.shapeblaster.game.scenes;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.SystemClock;
import android.util.Log;

import com.shapeblaster.game.MyGLRenderer;
import com.shapeblaster.game.R;
import com.shapeblaster.game.entity.Enemy;
import com.shapeblaster.game.entity.Entity;
import com.shapeblaster.game.entity.Missile;
import com.shapeblaster.game.entity.Obstacle;
import com.shapeblaster.game.entity.Player;
import com.shapeblaster.game.shapes.Shape;
import com.shapeblaster.game.shapes.TexturedShape;
import com.shapeblaster.game.ui.Button;
import com.shapeblaster.game.ui.NumericDisplay;
import com.shapeblaster.game.utils.SoundPlayer;
import com.shapeblaster.game.utils.Vect;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.egl.EGLConfig;

/**
 * Created by jean on 05/11/16.
 */

public class Scene extends MyGLRenderer {

    private final Context _ActivityContext;

    private Player _player;
    private boolean playerIsAlive = true;
    private boolean _playerCommand = false;
    private float _lastPlayerTarget;

    private int _xScreenSize;
    private int _yScreenSize;
    public Vect lastTouch;

    public ArrayList<Button> buttons = new ArrayList<>();

    private NumericDisplay score;

    private final String TAG = "Scene";

    private final String SAVE_FILNAME = "save.json";

    private ArrayList<Entity> _entities = new ArrayList<>();
    private ArrayList<Entity> _projectiles = new ArrayList<>();

    private boolean starting = true;
    private final int _startTime = 1000;
    private final int _waveCD = 5000;
    private final int _obsCD = 300;
    private final int _deathTime = 4000;

    private MediaPlayer _soundtrack;
    private boolean _muted = false;

    private long lastTime = -1;

    // for movement
    private long _time;

    private int indexObs = 0, maxObs = 2;

    private TexturedShape _deathScreen;

    private boolean _paused = false;

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        super.onSurfaceCreated(unused, config);
        lastTouch = new Vect();

        //Accessing internal storage : HOW TO
        /*/----------------------------------------------------------------------------------------
        JSONObject dataToSave = new JSONObject();
        try{
            dataToSave.put("age",3);
            dataToSave.put("name","Jean Charles");
            writeToSaveFile(dataToSave);
            String s = readSaveFile();
            Log.d("SAVE", s);
            JSONObject read = new JSONObject(s);
            Log.d("SAVE", "age : " + read.get("age") + " | name : " + read.get("name"));
        }catch (Exception e){Log.d(TAG,e.toString());}
        //----------------------------------------------------------------------------------------*/

        // Init score
        score = new NumericDisplay(_ActivityContext, 3);
        score.setValue(0);

        // Init player
        _player = new Player(_ActivityContext, 0.0f,-0.8f,0.2f);

        // Init death screen
        _deathScreen = new TexturedShape(_ActivityContext, R.drawable.deathscreen);
        _deathScreen.scale.set_x(0.9f);
        _deathScreen.scale.set_y(0.7f);

        // Init mute button
        final Button muteButton = new Button(_ActivityContext, R.drawable.mute, 2);
        muteButton.nextSprite();
        muteButton.setCallback(new Button.Callback() {
            @Override
            public void func() {
                muteAll();
                muteButton.nextSprite();
            }
        });
        muteButton.scale.set_x(0.1f);
        muteButton.scale.set_y(0.1f);
        muteButton.pos.set_x(-0.8f);
        muteButton.pos.set_y(0.9f);
        buttons.add(muteButton);

        final Button pauseButton = new Button(_ActivityContext, R.drawable.pause, 2);
        pauseButton.setCallback(new Button.Callback() {
            @Override
            public void func() {
                if(isPaused()) {
                    resume();
                } else {
                    pause();
                }
            }
        });
        pauseButton.scale.set_x(0.1f);
        pauseButton.scale.set_y(0.1f);
        pauseButton.pos.set_x(-0.5f);
        pauseButton.pos.set_y(0.9f);
        pauseButton.setSprite(2);
        buttons.add(pauseButton);

        Log.d(TAG, "Resources Loaded");
    }

    public Scene(Context context, int maxXSize, int maxYSize)
    {
        _ActivityContext = context;

        _xScreenSize = maxXSize;
        _yScreenSize = maxYSize;

        _soundtrack = MediaPlayer.create(context, R.raw.soundtrack);
        _soundtrack.setVolume(0.9f,0.9f);
        _soundtrack.setLooping(true);
        resumeMusic();

        lastTime = System.currentTimeMillis();

        Log.d(TAG, "Scene constructed");
    }

    public void onDrawFrame(GL10 unused) {
        super.onDrawFrame(unused);

        long now = SystemClock.uptimeMillis();
        float deltaTime = (float)(now-_time)/1000.0f;
        _time = now;

        if(playerIsAlive && !_paused) {

            // Refresh the player target if finger has not been removed from screen
            // usefull if finger has'nt move on screen but still down, as it doesn't trigger any event
            if(_playerCommand)refreshPlayerTarget(_lastPlayerTarget);
            _player.stopMovement(_playerCommand); //Stoping player if no command is given

            _player.move(deltaTime);

            _player.bound(-1.0f, 1.0f);

            _player.shoot();

            // Entity loop
            Iterator<Entity> i = _entities.iterator();
            while (i.hasNext()) {
                Entity e = i.next();
                // Move
                e.move(deltaTime);

                // Handle world bounds
                if (!e.bound(-1.0f, 1.0f)) { // World bound
                    i.remove();
                    _player.incScore(-5);
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
            }

            // Projectile loop
            i = _projectiles.iterator();
            while (i.hasNext()) {
                Entity e = i.next();
                // Move
                e.move(deltaTime);

                // Handle world bounds
                if (!e.bound(-1.0f, 1.0f)) { // World bound
                    i.remove();
                }
                // Handle collision with player
                Shape s = (Shape) e;
                if(_player.isHit(s.pos.get_x(), s.pos.get_y())) {
                    managePlayersDeath();
                }

                // Shoot them all!
                e.shoot();
            }
            manageObstacleWave();
        }

        _player.draw(_MVPMatrix);
        // Entity draw loop
        Iterator<Entity> i = _entities.iterator();
        while (i.hasNext()) {
            Entity e = i.next();
            // Draw them all
            e.draw(_MVPMatrix);
        }
        // Projectile draw loop
        i = _projectiles.iterator();
        while (i.hasNext()) {
            Entity e = i.next();
            // Draw them all
            e.draw(_MVPMatrix);
        }

        if(!playerIsAlive)
        {
            _player.draw(_MVPMatrix);
            _deathScreen.draw(_MVPMatrix);
            managePlayersDeath();
        }

        score.setValue(_player.getScore());
        score.draw(_MVPMatrix);

        for (Button b : buttons) {
            b.draw(_MVPMatrix);
        }

    }

    private void managePlayersDeath()
    {
        if(playerIsAlive)
        {
            lastTime = System.currentTimeMillis();
            playerIsAlive = false;
            Log.d(TAG, "DEAD BITCH");
            stopMusic();
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

                resumeMusic();

                _entities.clear();
                _projectiles.clear();

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
            Enemy e = new Enemy(_ActivityContext, (float)(Math.random()*1.4-0.7), 1.2f, size, 0.6f);
            e.setMissilesArray(_projectiles);
            _entities.add(e);
        } else {
            _entities.add(new Obstacle(_ActivityContext, (float) (Math.random() * 1.4 - 0.7), 1.2f, size, 0.6f, rotation));
        }

    }

    public final boolean isPaused() {
        return _paused;
    }

    public void pause() {
        if(!_paused) {
            if (isMusicPlaying()) stopMusic();
            _paused = true;
            buttons.get(1).setSprite(1);
        }
    }

    public void resume() {
        if(_paused) {
            if(!_muted) {
                resumeMusic();
            }
            _time = SystemClock.uptimeMillis();
            _paused = false;
            buttons.get(1).setSprite(2);
        }
    }

    public void stopMusic()
    {
        _soundtrack.pause();
    }
    public void resumeMusic()
    {
        if(!_muted) {
            _soundtrack.start();
        }
    }
    public boolean isMusicPlaying()
    {
        return _soundtrack.isPlaying();
    }

    public void muteAll() {
        _muted = !_muted;
        SoundPlayer.muteAll(_muted);

        if(!isMusicPlaying() && playerIsAlive) {
            resumeMusic();
        } else {
            stopMusic();
        }

    }

    public void stopPlayer()
    {
        _playerCommand = false;
    }

    public void redirectPlayer(float target)
    {
        _playerCommand = true;
        _lastPlayerTarget = target;
    }

    private void refreshPlayerTarget(float target)
    {
        target -= (float)_xScreenSize/2.0f; //Resizing target from [0;MAX] to [-Max/2;Max/2]
        target /= (float)_xScreenSize/2.0f; //Resizing target to [-1;1] According to the OpenGL view
        _player.setDestination(target);
    }

    private void writeToSaveFile(JSONObject data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(_ActivityContext.openFileOutput(SAVE_FILNAME, Context.MODE_PRIVATE));
            outputStreamWriter.write(data.toString());
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }


    private String readSaveFile()
    {
        String ret = "";

        try {
            InputStream inputStream = _ActivityContext.openFileInput(SAVE_FILNAME);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }
}