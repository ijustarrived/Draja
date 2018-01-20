package com.retroroots.alphadraja;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.VideoView;

import com.retroroots.alphadraja.CanisterEngine.Android.Application.ActivityConfig;


public class SplashScreenActivity extends ActionBarActivity {

    private static Activity activity;

    private static VideoView splashScreenVdVw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);

        activity = this;

        //Set orientation to landscape
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        //Hide upper action bar
        getSupportActionBar().hide();



        splashScreenVdVw = (VideoView) findViewById(R.id.splashScreenVdVw);

        splashScreenVdVw.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" +
                R.raw.drajasplashscreenvid));

        final Context context = this;

        splashScreenVdVw.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                splashScreenVdVw.stopPlayback();

                ActivityConfig.CreateActivity(Main.class, context, true);

                return false;
            }
        });

        splashScreenVdVw.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp)
            {
                ActivityConfig.CreateActivity(Main.class, context, true);
            }
        });

        splashScreenVdVw.start();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        splashScreenVdVw.pause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        splashScreenVdVw.start();
    }

    public static Activity GetActivity()
    {
        return activity;
    }
}
