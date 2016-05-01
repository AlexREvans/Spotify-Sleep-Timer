package com.alexe.sleeptimer;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class SleepTimerActivity extends AppCompatActivity {

    private TextView timeRemaining;
    private RadioGroup radios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_timer);

        timeRemaining = (TextView) findViewById(R.id.countdown);
        radios = (RadioGroup) findViewById(R.id.radios);
    }

    public void onRadioTimeClicked(View view) {

        if (!((RadioButton) view).isChecked()) return;

        long timeSeconds = 0;

        switch (view.getId()) {
            case R.id.radioButton5s:
                timeSeconds = 5;
                break;
            case R.id.radioButton30s:
                timeSeconds = 30;
                break;
            case R.id.radioButton5m:
                timeSeconds = 5 * 60;
                break;
            case R.id.radioButton10m:
                timeSeconds = 10 * 60;
                break;
            case R.id.radioButton30m:
                timeSeconds = 30 * 60;
                break;
        }

        startCountdown(timeSeconds);
    }

    private void startCountdown(long countdownSeconds) {
        new CountDownTimer(countdownSeconds * 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                timeRemaining.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                radios.clearCheck();
                timeRemaining.setText("Done!");

                sendMediaButton(getApplicationContext(), KeyEvent.KEYCODE_MEDIA_PAUSE);

                // Spotify doesn't respond to media buttons
                Intent pauseSpotify = new Intent("com.spotify.mobile.android.ui.widget.PLAY");
                pauseSpotify.setPackage("com.spotify.music");
                sendBroadcast(pauseSpotify);

                finish();
            }
        }.start();
    }

    private static void sendMediaButton(Context context, int keyCode) {
        KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
        Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        intent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
        context.sendOrderedBroadcast(intent, null);

        keyEvent = new KeyEvent(KeyEvent.ACTION_UP, keyCode);
        intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        intent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
        context.sendOrderedBroadcast(intent, null);
    }
}
