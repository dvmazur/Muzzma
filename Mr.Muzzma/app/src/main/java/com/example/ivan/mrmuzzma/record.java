package com.example.ivan.mrmuzzma;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class record extends AppCompatActivity {

    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    private String fileName = "rap.3gpp";
    private Boolean record = false;
    Animation animation;
    private ImageView vinil;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        vinil = (ImageView)findViewById(R.id.vinil);
        fileName =  Environment.getExternalStorageDirectory() + "/rap.3gpp";
        animation = AnimationUtils.loadAnimation(
                this, R.anim.rotate);
    }

    public void record(View view) {
        if (record) {
            recordStop();
            vinil.clearAnimation();
            transformAudio();
        } else {
            vinil.startAnimation(animation);
            recordStart();
        }
        record = !record;
    }

    private void releaseRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    private void recordStart() {
        try {
            releaseRecorder();

            File outFile = new File(fileName);
            outFile.createNewFile();
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(fileName);
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void recordStop() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
        }
    }

    public void playStart() {
        try {
            releasePlayer();
            mediaPlayer = new MediaPlayer();
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

    private void transformAudio(){
        Intent intent = new Intent(record.this, Processing.class);
        startActivity(intent);
    }

}
