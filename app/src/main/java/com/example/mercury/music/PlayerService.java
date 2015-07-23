package com.example.mercury.music;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

public class PlayerService extends Service {


   // private final IBinder mBinder = new MyBinder();
   // private Messenger outMessenger;

    @Override
    public IBinder onBind(Intent arg0) {
        //Bundle extras = arg0.getExtras();
        //Log.d("service","onBind");
        // Get messager from the Activity
        //if (extras != null) {
        //    Log.d("service","onBind with extra");
        //    outMessenger = (Messenger) extras.get("MESSENGER");
        //}
        return null;
    }

    public class MyBinder extends Binder {
        PlayerService getService() {
            return PlayerService.this;
        }
    }

    private PlayerService playerService;

    private MediaPlayer mediaPlayer;
    private AssetFileDescriptor descriptor;
    private boolean isPoused = false;
    String STATE_PLAYER_ACTION;
    private final int STATE_IDLE = 0;
    private final int STATE_PLAYING = 1;
    private final int STATE_PAUSED = 2;
    public int currentState;

    public PlayerService() {
    }

    @Override
    public void onCreate() {
        Log.d("TAAG", "onCreate");
        mediaPlayer = new MediaPlayer();
        STATE_PLAYER_ACTION = getResources().getString(R.string.state);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                mp.reset();
                isPoused = false;
                updateState(STATE_IDLE);
            }
        });

    }

    @Override
    public void onStart(Intent intent, int startid) {
        Log.d("TAAG", "onStart");
        //Toast.makeText(this, "Service is launched",Toast.LENGTH_SHORT).show();
        playBegin();
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Servise is stoped", Toast.LENGTH_SHORT).show();
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("TAAG", "onStartCommand");
        if (isPoused == true) {
            updateState(STATE_PLAYING);
            mediaPlayer.start();
            isPoused = false;
            Toast.makeText(this, "Player no paused", Toast.LENGTH_SHORT).show();
        } else {
            if (mediaPlayer.isPlaying()) {
                updateState(STATE_PAUSED);
                mediaPlayer.pause();
                isPoused = true;
                Toast.makeText(this, "Player paused", Toast.LENGTH_SHORT).show();
            } else {
                updateState(STATE_PLAYING);
                //playButton.setText(getResources().getString(R.string.button_paused));
                Toast.makeText(this, "Player start", Toast.LENGTH_SHORT).show();
                playBegin();
            }
        }

        return START_STICKY;
    }


    public void playBegin() {
        try {
            descriptor = getAssets().openFd("audio_ten.mp3");
            mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();

            mediaPlayer.prepare();
            mediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateState(int state) {
        Intent statePLayerIntent = new Intent(STATE_PLAYER_ACTION);
        currentState = state;
        switch (state) {
            case STATE_IDLE:
                statePLayerIntent.putExtra("STATE", STATE_IDLE);
                break;
            case STATE_PLAYING:
                statePLayerIntent.putExtra("STATE", STATE_PLAYING);
                break;
            case STATE_PAUSED:
                statePLayerIntent.putExtra("STATE", STATE_PAUSED);
                break;
        }
        Log.d("TAAG", "upDate");
        LocalBroadcastManager.getInstance(this).sendBroadcast(statePLayerIntent);
    }


}
