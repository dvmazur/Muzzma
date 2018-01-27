package com.example.ivan.mrmuzzma;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class Processing extends AppCompatActivity {

    private ImageView king;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processing);
        final Animation animation = AnimationUtils.loadAnimation(
                this, R.anim.rotate);
        king = (ImageView)findViewById(R.id.king);
        king.startAnimation(animation);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(0);
            }
        });
        thread.start();
    }

    private void chat(){
        Intent intent = new Intent(Processing.this, ChatActivity.class);
        startActivity(intent);
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.i("thread", "finished");
            chat();
        }

    };

}
