package com.example.ivan.mrmuzzma;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class Processing extends AppCompatActivity {

    ImageView king;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processing);
        final Animation animation = AnimationUtils.loadAnimation(
                this, R.anim.rotate);
        king = (ImageView)findViewById(R.id.king);
        king.startAnimation(animation);
    }
}
