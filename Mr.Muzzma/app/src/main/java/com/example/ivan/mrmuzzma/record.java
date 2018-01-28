package com.example.ivan.mrmuzzma;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class record extends AppCompatActivity {

    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    private String fileName = "rap.mp3";
    private Boolean record = false;
    Animation animation;
    private ImageView vinil;
    private TextView hint;
    String[] text;
    int index = 0;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        hint = (TextView)findViewById(R.id.hint);
        text = getIntent().getStringArrayExtra("chose");
        vinil = (ImageView)findViewById(R.id.vinil);
        fileName =  Environment.getExternalStorageDirectory().getAbsolutePath() + "/rap.mp3";
        animation = AnimationUtils.loadAnimation(
                this, R.anim.rotate);
        hint.setText(text[0]);
    }

    public void record(View view) {
        if (record) {
            recordStop();
            ((Button)view).setBackgroundResource(R.drawable.mic);
            vinil.clearAnimation();
            transformAudio();
        } else {
            vinil.startAnimation(animation);
            ((Button)view).setBackgroundResource(R.drawable.mic_record);
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
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
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

    public void next(View view) {
        if (index < text.length - 1){
            index++;
            hint.setText(text[index]);
        }
    }

    public void prev(View view) {
        if (index > 0){
            index--;
            hint.setText(text[index]);
        }
    }
}
