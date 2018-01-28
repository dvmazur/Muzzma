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

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.client.methods.CloseableHttpResponse;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClients;

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
        Log.i("rest", "ready");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "http://10.39.1.82:5000/input";
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                        "/rap.mp3");
                String s = "empty";
                CloseableHttpResponse response = null;
                CloseableHttpClient client = null;
                try {
                    try {
                        client = HttpClients.createDefault();
                        HttpPost httpPost = new HttpPost(url);
                        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                        builder.addBinaryBody("audio", file);
                        HttpEntity multipart = builder.build();
                        httpPost.setEntity(multipart);
                        response = client.execute(httpPost);
                    } catch (Exception e) {
                        Log.i("ERROR", e.getMessage());
                    }
                    client.close();
                } catch (Exception e) {
                    Log.i("rest", e.getMessage());
                }
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("url", "http://10.39.1.82:5000/output/out.mp3");
                message.setData(bundle);
                handler.sendMessage(message);
            }
        });
        thread.start();
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Intent intent = new Intent(Processing.this, ChatActivity.class);
            intent.putExtra("url", msg.getData().get("url").toString());
            startActivity(intent);
        }

    };

}
