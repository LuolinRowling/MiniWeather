package cn.edu.pku.luolin.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class MyIntentService extends IntentService {
    private Timer timer = new Timer();
    static final int UPDATE_INTERVAL = 10000;

    public MyIntentService() {
        super("MyIntentService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction("CURRENT_WEATHER_UPDATE_ACTION");
                getBaseContext().sendBroadcast(broadcastIntent);
            }
        }, 0, UPDATE_INTERVAL);

        return START_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (timer != null) {
            timer.cancel();
        }
    }
}
