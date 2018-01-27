package com.example.ivan.mrmuzzma;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class ChatActivity extends AppCompatActivity {

    private String fileName;
    private MediaPlayer mediaPlayer;
    private ArrayList<ChatMessage> messages;
    private EditText edit;
    ChatArrayAdapter adapter;
    final String[] answers = {"cmon nigga!", "oh yeah", "homeboy", "great", "so/so", "a u stupid", "Dayuuuum", "Vabu Labu Dabs Dabs"};
    Boolean flag = false;
    Button play;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        edit = (EditText)findViewById(R.id.message);
        play = (Button)findViewById(R.id.play);
        final Random rnd = new Random();
        fileName =  Environment.getExternalStorageDirectory().getAbsolutePath() + "/rap.mp3";
        messages = new ArrayList<>();
        final ListView listView = (ListView)findViewById(R.id.messages);
        adapter = new ChatArrayAdapter(getApplicationContext(), R.layout.right);
        listView.setAdapter(adapter);
        adapter.add(new ChatMessage(true, "Your track is ready"));
        listView.post(new Runnable() {
            @Override
            public void run() {
                listView.smoothScrollToPosition(adapter.getCount() - 1);
            }
        });
        edit.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    adapter.add(new ChatMessage(false, edit.getText().toString()));
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            long start = System.currentTimeMillis();
                            int delay = rnd.nextInt(500) + 1000;
                            while (System.currentTimeMillis() - start < delay){}
                            handler.sendEmptyMessage(0);
                        }
                    });
                    thread.start();
                    edit.setText("");
                    edit.requestFocus();
                    return true;
                }
                return false;
            }
        });
        edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) listView.setSelection(adapter.getCount() - 1);
            }
        });
    }

    public void playStart() {
        try {
            releasePlayer();
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(fileName);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    flag = !flag;
                    play.setText("PLAY");
                }
            });
            mediaPlayer.start();
        } catch (Exception e) {}
    }

    private void releasePlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void stopPlaying() {
        mediaPlayer.stop();
    }

    public void start(View view) {
        Button b = (Button)view;
        if (flag) {
            stopPlaying();
            play.setText("PLAY");
        } else {
            playStart();
            play.setText("STOP");
        }
        flag = !flag;
    }

    Handler handler = new android.os.Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final Random rnd = new Random();
            adapter.add(new ChatMessage(true, answers[rnd.nextInt(answers.length)]));
        }
    };

    public void oneMoreTrack(View view) {
        Intent intent = new Intent(ChatActivity.this, record.class);
        startActivity(intent);
    }

    public void save(View view) {
        Toast.makeText(this, "Comming soon...", Toast.LENGTH_SHORT).show();
    }
}
