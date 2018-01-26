package com.example.ivan.mrmuzzma;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView)findViewById(R.id.imageView);
        imageView.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this){
            @Override
            public void onSwipeRight() {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.kizaru));
                super.onSwipeRight();
            }

            @Override
            public void onSwipeLeft() {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.kizaru2));
                super.onSwipeLeft();
            }

        });
    }

}
