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

import java.util.concurrent.TimeUnit;

public class SleepTimerActivity extends AppCompatActivity {

    private TextView timeRemaining;
    private RadioGroup radios;

    private final String REMAINING_TIME = "REMAINING TIME";
    private long remainingTime = 0;

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putLong(REMAINING_TIME, remainingTime);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_timer);

        timeRemaining = (TextView) findViewById(R.id.countdown);
        radios = (RadioGroup) findViewById(R.id.radios);


        if(savedInstanceState != null) {
            remainingTime = savedInstanceState.getLong(REMAINING_TIME);
            startCountdown(remainingTime);
        }
    }

    public void onRadioTimeClicked(View view) {

        if (!((RadioButton) view).isChecked()) return;

        long timeSeconds = 0;

        switch (view.getId()) {
            case R.id.radioButton5m:
                timeSeconds = 5 * 60;
                break;
            case R.id.radioButton10m:
                timeSeconds = 10 * 60;
                break;
            case R.id.radioButton15m:
                timeSeconds = 15 * 60;
                break;
            case R.id.radioButton30m:
                timeSeconds = 30 * 60;
                break;
        }

        startCountdown(timeSeconds * 1000);
    }

    private void startCountdown(long countdownMillis) {
        new CountDownTimer(countdownMillis, 1000) {

            public void onTick(long millisUntilFinished) {

                String timer = String.format("Time remaining - %02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))
                );

                remainingTime = millisUntilFinished;
                timeRemaining.setText(timer);
            }

            public void onFinish() {
                radios.clearCheck();
                timeRemaining.setText("Done!");
                remainingTime = 0;

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
