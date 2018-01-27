package com.example.ivan.mrmuzzma;

import android.media.MediaPlayer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ChatActivity extends AppCompatActivity {

    private String fileName;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        fileName =  Environment.getExternalStorageDirectory() + "/rap.3gpp";
    }

    public void playStart() {
        try {
            releasePlayer();
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(fileName);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void releasePlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void start(View view) {
        playStart();
    }
}
