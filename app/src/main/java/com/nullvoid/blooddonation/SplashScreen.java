package com.nullvoid.blooddonation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sanath on 27/06/17.
 */

public class SplashScreen extends AppCompatActivity {
    Activity context = this;

    @BindView(R.id.logo_background) ImageView logoBackground;
    @BindView(R.id.drop_1) ImageView drop1;
    @BindView(R.id.drop_2) ImageView drop2;
    @BindView(R.id.light) ImageView light;

    private static int SPLASH_TIME_OUT = 4000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ButterKnife.bind(context);

        //animation preparation
        drop1.setTranslationY(-200.f);
        drop1.setAlpha(0.f);

        drop2.setTranslationY(-50.f);
        drop2.setAlpha(0.f);

        light.setAlpha(0.f);

        drop1.animate()
                .alpha(1.f)
                .translationY(0.f)
                .setDuration(2000)
                .start();

        drop2.animate()
                .alpha(1.f)
                .translationY(0.f)
                .setDuration(1500)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        light.animate()
                                .alpha(1.f)
                                .setDuration(2500)
                                .start();
                    }
                })
                .start();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                startActivity(new Intent(SplashScreen.this, MainActivity.class));

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}