package com.example.ivan.mrmuzzma;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;

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

                //-----------------------------------------------------------------

                String url = "http://192.168.";
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                        "/rap.3gp");

                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(url);
                    InputStreamEntity reqEntity = new InputStreamEntity(new FileInputStream(file), -1);
                    reqEntity.setContentType("binary/octet-stream");
                    reqEntity.setChunked(true);
                    httppost.setEntity(reqEntity);
                    HttpResponse response = httpclient.execute(httppost);
                    file.delete();
                    file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/rap.mp3");
                    if (file.exists()) file.delete();
                    file.createNewFile();
                    InputStream stream = response.getEntity().getContent();
                    BufferedReader buf = new BufferedReader(new InputStreamReader(stream,"UTF-8"));
                    StringBuilder sb = new StringBuilder();
                    String s;
                    FileWriter writer = new FileWriter(file);
                    while(true )
                    {
                        s = buf.readLine();
                        if(s==null || s.length()==0) break;
                        writer.append(s);
                    }
                    writer.flush();
                    writer.close();
                    buf.close();
                    stream.close();
                } catch (Exception e) {
                    Log.e("Http", "Server connection error" + url);
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
            chat();
        }

    };

}
