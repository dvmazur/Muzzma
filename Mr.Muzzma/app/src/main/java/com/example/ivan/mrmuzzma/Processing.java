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
        Log.i("rest", "ready");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "http://gymbank.site/dr";
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                        "/rap.3gp");
                String s = "empty";
                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(url);
                    InputStreamEntity reqEntity = new InputStreamEntity(new FileInputStream(file), -1);
                    reqEntity.setContentType("binary/octet-stream");
                    reqEntity.setChunked(true);
                    httppost.setEntity(reqEntity);
                    HttpResponse response = httpclient.execute(httppost);
                    file.delete();
                    InputStream stream = response.getEntity().getContent();
                    BufferedReader buf = new BufferedReader(new InputStreamReader(stream,"UTF-8"));
                    s = buf.readLine();
                    buf.close();
                    stream.close();
                    Log.e("url", s);
                } catch (Exception e) {
                    Log.i("rest", e.getMessage());
                }
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("url", s);
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
