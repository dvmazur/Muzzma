package com.example.ivan.mrmuzzma;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Locale;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

public class RickyActivity extends AppCompatActivity {

    ArrayList<ChatMessage> messages;
    ChatArrayAdapter adapter;
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    EditText edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ricky);
        edit = (EditText)findViewById(R.id.message);
        messages = new ArrayList<>();
        final ListView listView = (ListView)findViewById(R.id.messages);
        adapter = new ChatArrayAdapter(getApplicationContext(), R.layout.right);
        listView.setAdapter(adapter);
        adapter.add(new ChatMessage(true, "I'l write you the best text which you'v ever saw."));
        edit.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    myText(edit.getText().toString());
                    edit.setText("");
                    return true;
                }
                return false;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setChosen(position);
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void myText(final String msg) {
        adapter.add(new ChatMessage(false, msg));
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    String url = "http://10.39.1.82:5000/rhyme/?phrase=" + msg;
                    url = url.replace(" ", "_");
                    Log.i("URL", url);
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
                    InputStream inputStream = httpResponse.getEntity().getContent();
                    bundle.putString("answer", convertInputStreamToString(inputStream));
                    message.setData(bundle);
                    handler.sendMessage(message);
                }catch (IOException e) {}
            }
        });
        thread.start();
    }

    public void hisText(String msg){
        adapter.add(new ChatMessage(true, msg));
    }

    public void recognize(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, Locale.ENGLISH);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Make a fire");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    myText(result.get(0));
                }
                break;
            }
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            hisText(msg.getData().getString("answer"));
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ricky, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (adapter.getAllChosen().length != 0) {
            Intent intent = new Intent(RickyActivity.this, record.class);
            intent.putExtra("chose", adapter.getAllChosen());
            startActivity(intent);
        } else Toast.makeText(this, "Please choose something", Toast.LENGTH_SHORT).show();
        return true;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }

}
