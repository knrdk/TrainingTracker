package com.example.konrad.trainingtracker;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Comarch on 2015-01-26.
 */
public class Stopwatch implements Runnable {
    private TimerListener listener;
    private Activity activity;

    private StopwatchViewModel lastValue;

    private boolean isRunning = false;
    private boolean isStopped = false;

    private long aggregatedMiliseconds = 0;
    private long startTime;

    private Thread thread = new Thread(this);

    public Stopwatch(TimerListener listener) {
        this.listener = listener;
        if(listener instanceof Activity){
            activity = (Activity) listener;
        }
        thread.start();
    }

    public void start() {
        startTime = SystemClock.uptimeMillis();
        isRunning = true;
    }

    public void pause() {
        isRunning = false;
        aggregatedMiliseconds += getCurrentMiliseconds();
    }

    public void stop(){
        pause();
        isStopped = true;
    }

    private long getCurrentMiliseconds(){
        return SystemClock.uptimeMillis() - startTime;
    }

    @Override
    public void run() {
        while(!isStopped){
            if (isRunning) {
                notifyListener();
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {}
        }
    }

    private void notifyListener() {
        long totalMiliseconds = aggregatedMiliseconds + getCurrentMiliseconds();
        final StopwatchViewModel newValue = new StopwatchViewModel(totalMiliseconds);
        if(!newValue.equals(lastValue)){
            if(activity!=null){
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.updateTime(newValue);
                    }
                });
            }else{
                listener.updateTime(newValue);
            }
            lastValue = newValue;
        }
    }
}
