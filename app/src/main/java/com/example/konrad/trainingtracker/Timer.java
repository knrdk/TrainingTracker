package com.example.konrad.trainingtracker;

import android.os.Handler;
import android.os.SystemClock;

/**
 * Created by Comarch on 2015-01-26.
 */
public class Timer implements Runnable  {
    private Handler handler;
    private TimerListener listener;
    private TimerViewModel lastValue;

    private boolean isRunning = false;

    private long aggregatedMiliseconds = 0;
    private long currentMiliseconds = 0;
    private long startTime;


    public Timer(TimerListener listener, Handler handler){
        this.listener = listener;
        this.handler = handler;
    }

    public void start(){
        startTime = SystemClock.uptimeMillis();
        isRunning = true;
    }

    public void stop(){
        isRunning = false;
    }

    @Override
    public void run() {
        if(isRunning){
            currentMiliseconds = SystemClock.uptimeMillis() - startTime;
            long totalMiliseconds = currentMiliseconds + aggregatedMiliseconds;
            listener.updateTime(totalMiliseconds/1000);
        }
        handler.postDelayed(this,100);
    }
}
