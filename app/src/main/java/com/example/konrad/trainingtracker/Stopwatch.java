package com.example.konrad.trainingtracker;

import android.app.Activity;
import android.os.SystemClock;

/**
 * Created by Comarch on 2015-01-26.
 */
public class Stopwatch implements Runnable {
    private DurationListener listener;
    private Activity activity;

    private Duration lastValue;

    private boolean isRunning = false;
    private boolean isStopped = false;

    private long aggregatedMiliseconds = 0;
    private long startTime;

    private Thread thread = new Thread(this);

    public Stopwatch(DurationListener listener) {
        this.listener = listener;
        if (listener instanceof Activity) {
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

    public void stop() {
        pause();
        isStopped = true;
    }

    private long getCurrentMiliseconds() {
        return SystemClock.uptimeMillis() - startTime;
    }

    @Override
    public void run() {
        while (!isStopped) {
            if (isRunning) {
                notifyListener();
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
            }
        }
    }

    private void notifyListener() {
        long totalMiliseconds = aggregatedMiliseconds + getCurrentMiliseconds();
        final Duration newValue = new Duration(totalMiliseconds);
        if (!newValue.equals(lastValue)) {
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.setDuration(newValue);
                    }
                });
            } else {
                listener.setDuration(newValue);
            }
            lastValue = newValue;
        }
    }
}
