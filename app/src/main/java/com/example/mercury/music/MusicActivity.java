package com.example.mercury.music;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PersistableBundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;



public class MusicActivity extends ActionBarActivity {

    private Button playButton;
    //private MediaPlayer mediaPlayer;
    //private AssetFileDescriptor descriptor;
    private boolean isPoused = false;
    PlayerService.MyBinder myBinder = null;
    private int currentState;
    private final int STATE_IDLE=0;
    private final int STATE_PLAYING=1;
    private final int STATE_PAUSED=2;
    private String STATE_PLAYER_ACTION;
    private TextView stateView;
    PlayerService myService;


    private PlayerService myServiceBinder;

  /*  public ServiceConnection myConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder binder) {
            myServiceBinder = ((PlayerService.MyBinder) binder).getService();
            Log.d("TAAG","connected");
            showServiceData();
        }

        public void onServiceDisconnected(ComponentName className) {
            Log.d("TAAG","disconnected");
            myService = null;
        }
    };

    public Handler myHandler = new Handler() {
        public void handleMessage(Message message) {
            Bundle data = message.getData();
        }
    };

    public void doBindService() {
        Intent intent = null;
        intent = new Intent(this, PlayerService.class);
        // Create a new Messenger for the communication back
        // From the Service to the Activity
        Messenger messenger = new Messenger(myHandler);
        intent.putExtra("TAAG", messenger);

        bindService(intent, myConnection, Context.BIND_AUTO_CREATE);
    }

    private void showServiceData() {
        currentState = myServiceBinder.currentState;
    }
*/

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(stateReceiver, new IntentFilter(STATE_PLAYER_ACTION));
        //stateReceiver.
           // if (myService == null) {
           //     doBindService();
           // }
        //if (myServiceBinder != null) {
        //    updateState(currentState = myServiceBinder.currentState);
       // }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(stateReceiver);
        //if (myService != null) {
        //    unbindService(myConnection);
        //    myService = null;
       // }
    }







    private BroadcastReceiver stateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            currentState = intent.getIntExtra("STATE", -1);
            updateState(currentState);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TAAG", "onCreate");
        setContentView(R.layout.activity_music);
        stateView = (TextView) findViewById(R.id.id_state_view);
        playButton = (Button) findViewById(R.id.id_button_play);
        STATE_PLAYER_ACTION = getResources().getString(R.string.state);
        currentState = STATE_IDLE;
        if(savedInstanceState != null){
            currentState = savedInstanceState.getInt("currentState");
            updateState(currentState);
        }else{

             //currentState = myBinder.getService().currentState;
        }

        updateState(currentState);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    startService(new Intent(MusicActivity.this, PlayerService.class));
                    currentState = STATE_PAUSED;
            }
        });
    }
    public void updateState(int state){
        switch(state){
            case STATE_IDLE:
                playButton.setText(getResources().getString(R.string.button_play));
                stateView.setText(getResources().getString(R.string.state_idle));
                break;
            case STATE_PLAYING:
                playButton.setText(getResources().getString(R.string.button_paused));
                stateView.setText(getResources().getString(R.string.state_playing));
                break;
            case STATE_PAUSED:
                playButton.setText(getResources().getString(R.string.button_play));
                stateView.setText(getResources().getString(R.string.state_paused));
                break;
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentState", currentState);
    }
}

