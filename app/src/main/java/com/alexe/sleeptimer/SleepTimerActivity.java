package com.alexe.sleeptimer;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class SleepTimerActivity extends AppCompatActivity {

    private TextView remainingText;
    private TimePicker timePicker;
    private FloatingActionButton fab;

    private final String REMAINING_TIME = "REMAINING TIME";
    private long remainingTime = 0;

    private CountDownTimer timer;

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putLong(REMAINING_TIME, remainingTime);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_timer);

        remainingText = (TextView) findViewById(R.id.countdown);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            timePicker = (TimePicker) findViewById(R.id.timePicker);
        }

        fab = (FloatingActionButton) findViewById(R.id.fab);

        dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                timePicker = view;
                toggleStart(view);
            }
        }, 0,0, false);


        if (savedInstanceState != null) {
            remainingTime = savedInstanceState.getLong(REMAINING_TIME);

            if (remainingTime > 0) {
                startCountdown(remainingTime);
            }
        }
    }

    private TimePickerDialog dialog;

    public void pickTime(View view) {
        Date now = new Date();
        dialog.setTitle(null);
        dialog.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void toggleStart(View view) {
        if (remainingTime == 0)
            startClock();
        else
            endClock();
    }

    private void endClock() {
        timer.cancel();

        fab.setImageResource(android.R.drawable.ic_media_play);
        remainingText.setText(R.string.awaiting_playback);
        remainingTime = 0;
    }

    private void startClock() {

        if (timePicker == null) {
            return;
        }

        int selectedHour = timePicker.getCurrentHour();
        int selectedMin = timePicker.getCurrentMinute();
        Date now = new Date();

        Calendar cal = Calendar.getInstance();
        cal.setTime(now);

        cal.set(Calendar.HOUR_OF_DAY, selectedHour);
        cal.set(Calendar.MINUTE, selectedMin);

        if (cal.getTime().getTime() < now.getTime()) {
            cal.add(Calendar.DATE, 1);
        }

        Date target = cal.getTime();

        long millisToTarget = target.getTime() - now.getTime();

        startCountdown(millisToTarget);
    }

    private void startCountdown(long countdownMillis) {

        fab.setImageResource(android.R.drawable.ic_media_pause);

        timer = new CountDownTimer(countdownMillis, 1000) {

            public void onTick(long millisUntilFinished) {

                long millis = millisUntilFinished;

                long hours = TimeUnit.MILLISECONDS.toHours(millis);
                millis -= TimeUnit.HOURS.toMillis(hours);

                long mins = TimeUnit.MILLISECONDS.toMinutes(millis);
                millis -= TimeUnit.MINUTES.toMillis(mins);

                long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
                millis -= TimeUnit.SECONDS.toMillis(seconds);


                String timer = String.format("Time remaining - %02d:%02d:%02d", hours, mins, seconds);

                remainingTime = millisUntilFinished;
                remainingText.setText(timer);
            }

            public void onFinish() {

                fab.setImageResource(android.R.drawable.ic_media_play);
                remainingText.setText(R.string.awaiting_playback);
                remainingTime = 0;

                sendMediaButton(getApplicationContext(), KeyEvent.KEYCODE_MEDIA_PAUSE);

                // Spotify doesn't respond to media buttons
                Intent pauseSpotify = new Intent("com.spotify.mobile.android.ui.widget.PLAY");
                pauseSpotify.setPackage("com.spotify.music");
                sendBroadcast(pauseSpotify);
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
