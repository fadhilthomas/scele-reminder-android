package com.wibudev.scere.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.wibudev.scere.R;

public class SplashActivity extends AppCompatActivity {

    private ImageView tvJudul;
    private ViewGroup transContainer;
    private boolean visibleJudul;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        transContainer = findViewById(R.id.transContainer);
        tvJudul = transContainer.findViewById(R.id.tvJudul);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TransitionManager.beginDelayedTransition(transContainer);
                visibleJudul = !visibleJudul;
                tvJudul.setVisibility(visibleJudul ? View.VISIBLE : View.GONE);
            }
        },1000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, TaskActivity.class));
                finish();
            }
        },2000);

    }
}
