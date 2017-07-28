package com.nullvoid.blooddonation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
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

        final Animation fadeIn = AnimationUtils.loadAnimation(context, R.anim.alpha);
        Animation fallDown = AnimationUtils.loadAnimation(context, R.anim.translate);
        final Animation fallDown2 = AnimationUtils.loadAnimation(context, R.anim.translate);

        final AnimationSet s = new AnimationSet(true);
        s.addAnimation(fadeIn);
        s.addAnimation(fallDown2);

        light.setAlpha(0.f);

        drop2.setAlpha(0.f);
        drop2.setTranslationY(-100.f);

        fallDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        light.animate()
                                .alpha(1.f)
                                .setDuration(2500)
                                .start();

                        drop2.animate()
                                .alpha(1.f)
                                .translationY(0.f)
                                .setDuration(2000)
                                .start();
                    }
                }, 1000);
            }
            @Override
            public void onAnimationEnd(Animation animation) {

            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        drop1.startAnimation(fallDown);

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
