package com.example.konrad.trainingtracker;

import android.app.Activity;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

/**
 * Created by Comarch on 2015-01-26.
 */
public class Stopwatch implements Runnable {
    private Handler handler;

    private TimerListener listener;
    private Activity activity;

    private StopwatchViewModel lastValue;

    private boolean isRunning = false;
    private boolean isStopped = false;

    private long aggregatedMiliseconds = 0;
    private long startTime;


    public Stopwatch(TimerListener listener) {
        this.listener = listener;
        if(listener instanceof Activity){
            activity = (Activity) listener;
        }
        this.handler = new Handler();
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
